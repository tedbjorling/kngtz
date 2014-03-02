/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GamePacket {
    static final int PACKET_LENGTH = 1024;

    static ByteBuffer sendPacketBuffer;
    static DatagramPacket sendPacket;

    public static ByteBuffer getSendBuffer() {
        sendPacketBuffer.rewind();
        return sendPacketBuffer;
    }

    public static void send(DatagramSocket serverSocket, InetAddress IPAddress, int port) throws IOException {
        sendPacket.setData(sendPacketBuffer.array());
        sendPacket.setAddress(IPAddress);
        sendPacket.setPort(port);
        serverSocket.send(sendPacket);
    }

    static ByteBuffer receivePacketBuffer;
    static DatagramPacket receivePacket;

    public static void receive(DatagramSocket socket) throws IOException, SocketTimeoutException {
        socket.receive(receivePacket);
    }

    public static ByteBuffer getReceivePacketBuffer() {
        return receivePacketBuffer;
    }

    public static InetAddress getSourceAddress() {
        return receivePacket.getAddress();
    }

    public static int getSourcePort() {
        return receivePacket.getPort();
    }

    static {
        if(sendPacketBuffer == null) {
            sendPacketBuffer = ByteBuffer.allocate(PACKET_LENGTH);
            sendPacketBuffer.order(ByteOrder.BIG_ENDIAN);
            sendPacket = new DatagramPacket(sendPacketBuffer.array(), PACKET_LENGTH);
        }

        if(receivePacketBuffer == null) {
            receivePacketBuffer = ByteBuffer.allocate(1024);
            receivePacketBuffer.order(ByteOrder.BIG_ENDIAN);
            receivePacket = new DatagramPacket(sendPacketBuffer.array(), PACKET_LENGTH);
        }
    }
}