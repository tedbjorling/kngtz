package com.holidaystudios.kngt.controller;

import com.badlogic.gdx.Gdx;
import com.holidaystudios.kngt.model.Direction;
import com.holidaystudios.kngt.model.GameModel;
import com.holidaystudios.kngt.model.KnightModel;
import com.holidaystudios.kngt.model.RoomModel;
import com.holidaystudios.kngt.networking.GameClient;
import com.holidaystudios.kngt.networking.GameServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public class Human {

    private GameModel model;
    private KnightModel knight;
    private InetAddress clientAddress;

    public Human(GameModel _model, KnightModel _knight, InetAddress IPAddress) {
        model = _model;
        knight = _knight;
        clientAddress = IPAddress;
    }

    public void doMove(DatagramSocket serverSocket, ByteBuffer data) {
        switch(data.get()) {
            case GameServer.GAME_DIRECTION_NORTH:
                Gdx.app.log("kngt", "SERVER will move knight: north");
                knight.move(model, Direction.north);
                break;
            case GameServer.GAME_DIRECTION_WEST:
                Gdx.app.log("kngt", "SERVER will move knight: west");
                knight.move(model, Direction.west);
                break;
            case GameServer.GAME_DIRECTION_SOUTH:
                Gdx.app.log("kngt", "SERVER will move knight: south");
                knight.move(model, Direction.south);
                break;
            case GameServer.GAME_DIRECTION_EAST:
                Gdx.app.log("kngt", "SERVER will move knight: east");
                knight.move(model, Direction.east);
                break;
        }
        try {
            knight.publishKnight(serverSocket, clientAddress);
        } catch(IOException e) { /* xxx ignore */ }
    }

    public void publishCurrentState(DatagramSocket serverSocket) throws IOException {
        byte[][] room = model.getRoomBitmap(Integer.valueOf(knight.getRoomX()), Integer.valueOf(knight.getRoomY()));
        RoomModel.publishRoomBitmap(
                room, serverSocket,
                clientAddress, GameClient.CLIENT_PORT);
        knight.publishKnight(serverSocket, clientAddress);
    }

    public void publishCurrentStateRefresh(DatagramSocket serverSocket) throws IOException {
        knight.publishKnight(serverSocket, clientAddress);
    }


}
