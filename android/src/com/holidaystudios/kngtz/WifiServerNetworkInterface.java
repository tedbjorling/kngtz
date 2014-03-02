package com.holidaystudios.kngtz;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

class WifiServerNetworkInterface implements com.holidaystudios.kngt.networking.ServerNetworkInterface {
    private android.net.wifi.WifiManager.MulticastLock lock;
    private final Context context;

    private Enumeration<InetAddress> getWifiInetAddresses(final Context _context) {
        final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        final String macAddress = wifiInfo.getMacAddress();
        final String[] macParts = macAddress.split(":");
        final byte[] macBytes = new byte[macParts.length];
        for (int i = 0; i< macParts.length; i++) {
            macBytes[i] = (byte)Integer.parseInt(macParts[i], 16);
        }
        try {
            final Enumeration<NetworkInterface> e =  NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                final NetworkInterface networkInterface = e.nextElement();
                if (Arrays.equals(networkInterface.getHardwareAddress(), macBytes)) {
                    return networkInterface.getInetAddresses();
                }
            }
        } catch (SocketException e) {
            Log.wtf("WIFIIP", "Unable to NetworkInterface.getNetworkInterfaces()");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends InetAddress> T getWifiInetAddress(final Context context, final Class<T> inetClass) {
        final Enumeration<InetAddress> e = getWifiInetAddresses(context);
        while (e.hasMoreElements()) {
            final InetAddress inetAddress = e.nextElement();
            if (inetAddress.getClass() == inetClass) {
                return (T)inetAddress;
            }
        }
        return null;
    }

    public WifiServerNetworkInterface(Context _context) {
        context = _context;
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)
                context.getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("mylockthereturn");
        lock.setReferenceCounted(true);
    }

    @Override
    public void acquire() {
        lock.acquire();
    }

    @Override
    public void release() {
        lock.release();
    }

    @Override
    public String getIp() throws SocketException {
        final Inet4Address inet4Address = getWifiInetAddress(context, Inet4Address.class);
        final Inet6Address inet6Address = getWifiInetAddress(context, Inet6Address.class);

        if(inet6Address != null) return inet6Address.getHostAddress();
        if(inet4Address != null) return inet4Address.getHostAddress();

        throw new SocketException("No local network address found.");
    }
}