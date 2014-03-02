/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import com.holidaystudios.kngt.controller.Human;
import com.holidaystudios.kngt.model.GameModel;
import com.holidaystudios.kngt.model.KnightModel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Map;

public class GameServer extends Thread {
    public final static int SERVER_PORT = 9876;

    public final static int MAXIMUM_WAIT_FOR_READ = 10; // milliseconds to wait for a packet

    public final static byte CL_PACKET_LOGIN_USER = 04;
    public final static byte CL_PACKET_MOVE = 05;

    public final static byte SR_PACKET_ROOM_MAP = 02;
    public final static byte SR_PACKET_KNIGHT_STATE = 03;

    Map<InetAddress, Human> humans;

    GameModel currentGame;
    DatagramSocket serverSocket;

    public GameServer() throws SocketException {
        serverSocket = new DatagramSocket(SERVER_PORT);
        serverSocket.setSoTimeout(MAXIMUM_WAIT_FOR_READ);
        currentGame = new GameModel("733 kru", 5, 5);
    }

    public void setClientKnightState(int knightID, KnightModel.State state, float stateDuration) {

    }

    private Human loginNewUser() {
        return new Human(currentGame.addKnight(GameServer.this));
    }

    private void decodePacket() {
        ByteBuffer data = GamePacket.getReceivePacketBuffer();

        if(humans.containsKey(GamePacket.getSourceAddress())) {
            Human human = humans.get(GamePacket.getSourceAddress());
            switch(data.get()) {
                case CL_PACKET_MOVE:
                    human.doMove(data);
                    break;
            }
        } else if(data.get() == CL_PACKET_LOGIN_USER) {
            humans.put(GamePacket.getSourceAddress(), loginNewUser());
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long thisTime;
        while(true) {
            // game model should act on time
            thisTime = System.nanoTime();
            thisTime -= lastTime;
            thisTime /= 1000000; // convert to milliseconds
            currentGame.act((int)thisTime);

            try {
                GamePacket.receive(serverSocket);
                decodePacket();
            } catch(SocketTimeoutException e) {
            } catch(IOException e) {
            }
        }
    }
}
