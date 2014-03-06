/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GamePacketProvider {
    static final int PACKET_LENGTH = 1024;

    ByteBuffer sendPacketBuffer;
    DatagramPacket sendPacket;

    public ByteBuffer getSendBuffer() {
        sendPacketBuffer.rewind();
        return sendPacketBuffer;
    }

    public void send(DatagramSocket serverSocket, InetAddress IPAddress, int port) throws IOException {
        sendPacket.setData(sendPacketBuffer.array());
        sendPacket.setAddress(IPAddress);
        sendPacket.setPort(port);

//        Gdx.app.log("kngt", "SEND peek at packet: " + sendPacketBuffer.array()[0]);

        serverSocket.send(sendPacket);
    }

    ByteBuffer receivePacketBuffer;
    DatagramPacket receivePacket;

    public void receive(DatagramSocket socket) throws IOException, SocketTimeoutException {
        socket.receive(receivePacket);
//        Gdx.app.log("kngt", "peek at packet A: " + receivePacketBuffer.array()[0]);
        receivePacketBuffer.rewind();
//        Gdx.app.log("kngt", "peek at packet B: " + receivePacketBuffer.array()[0]);
    }

    public ByteBuffer getReceivePacketBuffer() {
        return receivePacketBuffer;
    }

    public InetAddress getSourceAddress() {
        return receivePacket.getAddress();
    }

    public int getSourcePort() {
        return receivePacket.getPort();
    }

    public GamePacketProvider() {
        if(sendPacketBuffer == null) {
            sendPacketBuffer = ByteBuffer.allocate(PACKET_LENGTH);
            sendPacketBuffer.order(ByteOrder.BIG_ENDIAN);
            sendPacket = new DatagramPacket(sendPacketBuffer.array(), PACKET_LENGTH);
        }

        if(receivePacketBuffer == null) {
            receivePacketBuffer = ByteBuffer.allocate(PACKET_LENGTH);
            receivePacketBuffer.order(ByteOrder.BIG_ENDIAN);
            receivePacket = new DatagramPacket(receivePacketBuffer.array(), PACKET_LENGTH);
        }
    }
}