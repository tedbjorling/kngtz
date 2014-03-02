package com.holidaystudios.kngt.controller;

import com.badlogic.gdx.Input;
import com.holidaystudios.kngt.model.Direction;
import com.holidaystudios.kngt.model.GameModel;
import com.holidaystudios.kngt.model.KnightModel;
import com.holidaystudios.kngt.model.RoomModel;
import com.holidaystudios.kngt.networking.GamePacket;
import com.holidaystudios.kngt.networking.GameServer;
import com.holidaystudios.kngt.view.GameView;
import com.holidaystudios.kngt.view.ViewListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import sun.security.x509.IPAddressName;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public class Human {

    private GameModel model;
    private KnightModel knight;

    public Human(KnightModel _knight) {
        knight = _knight;
    }

    public void doMove(ByteBuffer data) {
        switch(data.get()) {
            case 0: // north
                knight.move(model, Direction.north);
                break;
            case 1: // west
                knight.move(model, Direction.west);
                break;
            case 2: // south
                knight.move(model, Direction.south);
                break;
            case 3: // east
                knight.move(model, Direction.east);
                break;
        }
    }

    public void publishCurrentState(DatagramSocket serverSocket, InetAddress IPAddress, int port) throws IOException {
        RoomModel.publishRoomBitmap(model.getRoomBitmap(Integer.valueOf(knight.getRoomX()), Integer.valueOf(knight.getRoomY())), serverSocket, IPAddress, port);
        knight.publishKnight(serverSocket, IPAddress, port);
    }
}
