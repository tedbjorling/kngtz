/*************************************
 *
 * Copyright (c) 2014 by Anton Persson
 *
 ************************************/

package com.holidaystudios.kngt.networking;

import com.holidaystudios.kngt.model.KnightModel;
import com.holidaystudios.kngt.model.RoomModel;
import com.holidaystudios.kngt.view.GameView;
import com.holidaystudios.kngt.view.ViewListener;
import com.holidaystudios.kngt.view.actors.KnightView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class GameClient extends Thread implements ViewListener {
    public final static int CLIENT_PORT = 9877;
    public final static int DEFAULT_NUMBER_OF_FREE_EVENTS = 50;

    InetAddress serverAddress;

    Queue<ByteBuffer> freeBuffers = new PriorityQueue<ByteBuffer>(50);
    Queue<ByteBuffer> consumedBuffers = new PriorityQueue<ByteBuffer>(50);
    GameView view = new GameView();
    Map<Integer, KnightView> knights = new HashMap<Integer, KnightView>();

    DatagramSocket socket;

    private ByteBuffer getFreeBuffer() {
        ByteBuffer head = freeBuffers.poll();
        if(head == null) { // only create new packets if none is available
            head = ByteBuffer.allocate(GamePacket.PACKET_LENGTH);
            head.order(ByteOrder.BIG_ENDIAN);
        }

        return head;
    }

    public GameClient instance = null;
    public GameClient() throws SocketException {
        if(instance != null) throw new SocketException("GameClient instance already created.");
        instance = this;
        socket = new DatagramSocket(CLIENT_PORT);
    }

    public void logInTo(InetAddress address) throws IOException {
        serverAddress = address;
        ByteBuffer bb = GamePacket.getSendBuffer();

        bb.put(GameServer.CL_PACKET_LOGIN_USER);

        GamePacket.send(socket, serverAddress, GameServer.SERVER_PORT);
    }

    @Override
    public void run() {
        byte[] nonUsed = new byte[GamePacket.PACKET_LENGTH];
        DatagramPacket recvPacket = new DatagramPacket(nonUsed, GamePacket.PACKET_LENGTH);
        while(true) {
            ByteBuffer bb = getFreeBuffer();
            try {
                recvPacket.setData(bb.array());
                socket.receive(recvPacket);
                while(bb != null) {
                    try {
                        consumedBuffers.add(bb);
                        bb = null;
                    } catch(IllegalStateException e) {
                    }
                    if(bb != null) {
                        try {
                            sleep(10); // sleep, and try again
                        } catch(InterruptedException e) {
                            // ignore
                        }
                    }
                }
            } catch(SocketTimeoutException e) {
            } catch(IOException e) {
            }

            if(bb != null) {
                // not consumed - return
                freeBuffers.add(bb);
            }
        }
    }

    @Override
    public void handleViewEvent(EventType type, Object data) {

    }

    public GameView getView() {
        return view;
    }

    private void parseKnightState(ByteBuffer bb) {
        int knightId = bb.getInt();
        KnightView kng;
        if(knights.containsKey(Integer.valueOf(knightId))) {
            kng = knights.get(Integer.valueOf(knightId));
        } else {
            kng = new KnightView(new KnightModel(knightId));
            knights.put(Integer.valueOf(knightId), kng);
            view.addToStage(kng);
        }

        kng.model.consumePublishedKnight(bb);
    }

    private void parsePacket(ByteBuffer bb) {
        switch(bb.get()) {
            case GameServer.SR_PACKET_ROOM_MAP:
                view.renderRoom(RoomModel.consumePublishedRoomBitmap(bb));
                break;
            case GameServer.SR_PACKET_KNIGHT_STATE:
                parseKnightState(bb);
                break;
        }

        freeBuffers.add(bb);
    }

    public void processEvents() {
        int kount = 50;
        ByteBuffer bb;
        while((bb = consumedBuffers.poll()) != null && kount > 0) {
            parsePacket(bb);
            kount--;
        }
    }
}
