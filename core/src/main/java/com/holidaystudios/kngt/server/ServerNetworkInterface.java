package com.holidaystudios.kngt.server;

import java.net.SocketException;

public interface ServerNetworkInterface {
    public abstract void acquire();
    public abstract void release();
    public abstract String getIp() throws SocketException;
}

