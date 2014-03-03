/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import java.lang.Thread;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import java.io.IOException;
import java.net.InetAddress;

public class ServerAnnouncer {
    public static final String SERVER_IDENTIFIER = "_kngtz-server._tcp.local.";

    private static class JmDNSThread extends Thread {
        private ServerNetworkInterface netInterface;

        private boolean running;
        private JmDNS jmdns = null;
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

                serviceInfo = ServiceInfo.create(
                        SERVER_IDENTIFIER,
                        "KNGTZ", GameServer.SERVER_PORT,
                        "KNGTZ GameServer");

                jmdns.registerService(serviceInfo);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
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
}