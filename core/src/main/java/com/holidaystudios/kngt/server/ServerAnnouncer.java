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

public class ServerAnnouncer {
    public static final String SERVER_IDENTIFIER = "_kngtz-server._tcp.local.";

    private static class JmDNSThread extends Thread {
        private ServerNetworkInterface netInterface;

        private boolean running;
        private static final String type = "_kngtz-server._tcp.local.";
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
                        "KNGTZ", 0,
                        "KNGTZ Server");

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