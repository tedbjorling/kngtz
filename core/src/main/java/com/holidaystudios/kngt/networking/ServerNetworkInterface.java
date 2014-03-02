package com.holidaystudios.kngt.networking;

import java.net.SocketException;

public interface ServerNetworkInterface {
    public abstract void acquire();
    public abstract void release();
    public abstract String getIp() throws SocketException;
}

