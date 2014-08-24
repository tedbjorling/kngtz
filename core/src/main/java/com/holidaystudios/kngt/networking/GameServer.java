/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import com.holidaystudios.kngt.controller.Human;
import com.holidaystudios.kngt.model.GameModel;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameServer extends Thread {

    public final static byte GAME_DIRECTION_NORTH = 4;
    public final static byte GAME_DIRECTION_WEST = 5;
    public final static byte GAME_DIRECTION_SOUTH = 6;
    public final static byte GAME_DIRECTION_EAST = 7;

    public final static int SERVER_PORT = 9876;

    public final static int MAXIMUM_WAIT_FOR_READ = 10; // milliseconds to wait for a packet

    public final static byte CL_PACKET_LOGIN_USER = 4;
    public final static byte CL_PACKET_MOVE = 5;

    public final static byte SR_PACKET_ROOM_MAP = 2;
    public final static byte SR_PACKET_KNIGHT_STATE = 3;

    Map<InetAddress, Human> humans = new HashMap<InetAddress, Human>(10);

    GameModel currentGame;
    DatagramSocket serverSocket;
    boolean running;

    private GamePacketProvider packetProvider = new GamePacketProvider();

    private static GameServer instance = null;

    public static void bringUp() {
        if(instance != null)
            bringDown();

        instance = new GameServer();
        instance.start();
    }

    public static void bringDown() {
        if(instance != null) {
            instance.running = false;
            try {
                instance.wait();
            } catch(InterruptedException e) { /* ignore */ }

            instance = null;
        }
    }

    public GamePacketProvider getPacketProvider() {
        return packetProvider;
    }

    static public GameServer getInstance() {
        return instance;
    }

    private GameServer() {
        running = true;
    }

    private Human loginNewUser(InetAddress address) {
        return new Human(humans.size(), currentGame, currentGame.addKnight(GameServer.this), address);
    }

    private void decodePacket() {
        ByteBuffer data = packetProvider.getReceivePacketBuffer();

        if(humans.containsKey(packetProvider.getSourceAddress())) {
            Gdx.app.log("kngt", "human was located - first byte: " + data.array()[0] + " second byte: " + data.array()[1]);
            Human human = humans.get(packetProvider.getSourceAddress());
            try {
                switch(data.get()) {
                    case CL_PACKET_MOVE:
                        Gdx.app.log("kngt", "CL_PACKET_MOVE detected.");
                        human.doMove(serverSocket, data);
                        break;
                }
            } catch(IOException e) {
                /* xxx ignore currently */
            }
        } else if(data.get() == CL_PACKET_LOGIN_USER) {
            Gdx.app.log("kngt", "SERVER received login request.");
            Human human = loginNewUser(packetProvider.getSourceAddress());
            humans.put(packetProvider.getSourceAddress(), human);
            try {
                human.publishCurrentState(serverSocket);
            } catch(IOException e) {
                Gdx.app.log("kngt", "Failed to publish current state in GameServer.decodePacket() - aborting.");
                System.exit(-1);
            }
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
            serverSocket.setSoTimeout(MAXIMUM_WAIT_FOR_READ);
            currentGame = new GameModel("733 kru", 5, 5);
        } catch(IOException e) {
            System.exit(-1);
        }

        Gdx.app.log("kngt", "Server is bound to " + serverSocket.getLocalAddress());

        long lastTime = System.nanoTime();
        long thisTime;
        while(running) {
            // publish current state (this relies on the dirty flag being set properly
            Set<Map.Entry<InetAddress, Human>> set = humans.entrySet();
            for(Map.Entry<InetAddress, Human> entry : set) {
                Human h = entry.getValue();
                try {
                    h.publishCurrentStateRefresh(serverSocket, false);
                } catch(IOException e) { /* ignore */ }
            }

            // clear dirty flags
            currentGame.clearDirtyFlags();

            // game model should act on time
            thisTime = System.nanoTime();
            thisTime -= lastTime;
            thisTime /= 1000000; // convert from nano to milliseconds
            float delta = (float)thisTime;
            delta /= 1000.0f; // convert to seconds
            currentGame.act(delta);

            // receive new packets
            try {
                packetProvider.receive(serverSocket);
                Gdx.app.log("kngt", "SERVER received packet.");
                decodePacket();
            } catch(SocketTimeoutException e) {
            } catch(IOException e) {
            }
        }
    }
}
