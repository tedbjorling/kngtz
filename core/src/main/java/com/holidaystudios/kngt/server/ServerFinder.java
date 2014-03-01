/**
 * Copyright (c) 2014 by Anton Persson
 *
 */

package com.holidaystudios.kngt.server;

import com.badlogic.gdx.Gdx;
import java.lang.Thread;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceInfo;

import java.net.SocketException;
import java.util.ArrayList;

import java.io.IOException;
import java.net.InetAddress;

public class ServerFinder {
	public static class Service {
		public String name, address;
		public int port;

		public Service(String n, String a, int p) {
			name = n;
			address = a;
			port = p;
		}
	}

	private static class JmDNSThread extends Thread {
        private ServerNetworkInterface netInterface;

		private boolean running;
		private JmDNS jmdns = null;
		private ServiceListener listener = null;
		private ServiceInfo serviceInfo;
		private InetAddress _bindingAddress;

		JmDNSThread(ServerNetworkInterface _netInterface) {
			super("KngtzServerFinder");
            netInterface = _netInterface;
			netInterface.acquire();
			running = true;

			try {
				_bindingAddress = InetAddress.getByName(netInterface.getIp());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

		}

		public void run() {
			setUp();

			while(running) {
				try {
					this.sleep(1000);
				} catch(java.lang.InterruptedException e) {
				}
			}
			if (jmdns != null) {
				if (listener != null) {
					jmdns.removeServiceListener(ServerAnnouncer.SERVER_IDENTIFIER, listener);
					listener = null;
				}
				jmdns.unregisterAllServices();
				try {
					jmdns.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				jmdns = null;
			}
		}

		private void setUp() {
			try {
				jmdns = JmDNS.create(_bindingAddress);
				jmdns.addServiceListener(ServerAnnouncer.SERVER_IDENTIFIER, listener = new ServiceListener() {

						@Override
						public void serviceResolved(ServiceEvent ev) {
							String hostAddress = "";
							if (ev.getInfo().getInetAddresses() != null && ev.getInfo().getInetAddresses().length > 0) {
								hostAddress = ev.getInfo().getInetAddresses()[0].getHostAddress();
							}
							notifyUser("yabbadabba! Service resolved: " +
								   ev.getInfo().getQualifiedName() + " port:" + ev.getInfo().getPort() +
								   " " + hostAddress);

							Service srv = new Service(ev.getInfo().getQualifiedName(),
										  hostAddress,
										  ev.getInfo().getPort());
							addKngtzServer(srv);

						}

						@Override
						public void serviceRemoved(ServiceEvent ev) {
							notifyUser("Service removed: " + ev.getName());
						}

						@Override
						public void serviceAdded(ServiceEvent event) {
							// Required to force serviceResolved to be called again (after the first search)
							jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
						}
					});
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		final private ArrayList<Service> services = new ArrayList<Service>(10);

		private void addKngtzServer(Service srv) {
			synchronized(services) {
				services.add(srv);
			}

		}

		public Service[] getKngtzServers() {
			Service[] result;
			synchronized(services) {
				result = services.toArray(new Service[services.size()]);
			}
			return result;
		}

		private void notifyUser(final String msg) {
            Gdx.app.log("kngt", msg);
		}


		public void release() {
			running = false;
			try {
				this.join();
			} catch(java.lang.InterruptedException e) {
			}

			if(netInterface != null) netInterface.release();
		}
	}

	private static JmDNSThread jth;

	public static void onCreate(ServerNetworkInterface nInterface) {
		jth = new JmDNSThread(nInterface);
		jth.start();

	}

	public static void onDestroy() {
		if(jth != null)
			jth.release();
	}

	public static Service[] getKngtzServerArray() {
		if(jth != null)
			return jth.getKngtzServers();
		return null;
	}
}
