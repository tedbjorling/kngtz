package com.holidaystudios.kngt.model;

import com.badlogic.gdx.Gdx;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.networking.GameClient;
import com.holidaystudios.kngt.networking.GamePacketProvider;
import com.holidaystudios.kngt.networking.GameServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by tedbjorling on 2014-02-25.
 */
public class KnightModel {

    private final static float WALK_DURATION = 0.33f;

    public enum State {
        stand, walk
    };

    final int knightID;
    State state = State.stand;
    float stateTime = 0.0f; // in seconds
    float stateDuration = 0.0f; // in seconds
    float stateProgress = 0.0f; // from 0.0f to 1.0f inclusive
    Direction direction = Direction.north;

    int roomX;
    int roomY;
    int posX;
    int posY;

    public int getKnightID() {
        return knightID;
    }

    public State getState() {
        return state;
    }

    public float getStateTime() {
        return stateTime;
    }

    public float getStateDuration() {
        return stateDuration;
    }

    public float getStateProgress() {
        return stateProgress;
    }

    public Direction getDirection() {
        return direction;
    }

    public void publishKnight(DatagramSocket serverSocket, InetAddress IPAddress) throws IOException {
        GamePacketProvider packetProvider = GameServer.getInstance().getPacketProvider();
        ByteBuffer sendData = packetProvider.getSendBuffer();
        sendData.put(GameServer.SR_PACKET_KNIGHT_STATE);
        sendData.putInt(knightID);

        switch(state) {
            case stand:
                sendData.put((byte)1);
                break;
            case walk:
                sendData.put((byte)2);
                break;
        }

        sendData.putFloat(stateTime);
        sendData.putFloat(stateDuration);
        sendData.putFloat(stateProgress);

        switch (direction) {
            case east:
                sendData.put((byte)1);
                break;
            case west:
                sendData.put((byte)2);
                break;
            case north:
                sendData.put((byte)3);
                break;
            case south:
                sendData.put((byte)4);
                break;
        }

        sendData.putInt(posX);
        sendData.putInt(posY);

        packetProvider.send(serverSocket, IPAddress, GameClient.CLIENT_PORT);

        Gdx.app.log("kngt",
                "Published: (" + state.toString() + ") time: " + stateTime + " duration: " + stateDuration + " progress: " + stateProgress + " direction: " + direction.toString() +
                        " pos(" + posX + ", " + posY + ")");
    }

    public void consumePublishedKnight(ByteBuffer bb) {
        switch(bb.get()) {
            case (byte)1:
                state = State.stand;
                break;
            case (byte)2:
                state = State.walk;
                break;
        }

        stateTime = bb.getFloat();
        stateDuration = bb.getFloat();
        stateProgress = bb.getFloat();

        switch(bb.get()) {
            case (byte)1:
                direction = Direction.east;
                break;
            case (byte)2:
                direction = Direction.west;
                break;
            case (byte)3:
                direction = Direction.north;
                break;
            case (byte)4:
                direction = Direction.south;
                break;
        }

        posX = bb.getInt();
        posY = bb.getInt();

        Gdx.app.log("kngt",
                "Consumed: (" + state.toString() + ") time: " + stateTime + " duration: " + stateDuration + " progress: " + stateProgress + " direction: " + direction.toString() +
                " pos(" + posX + ", " + posY + ")");
    }

    public KnightModel(int _knightID) {
        knightID = _knightID;
    }

    public void move(GameModel gameModel, Direction _direction) {
        if(state == State.stand) {
            //What is the target tile?
            int px= posX; int py = posY;
            switch (_direction) {
                case east: px++; break;
                case west: py--; break;
                case north: py--; break;
                case south: px++; break;
            }

            final int targetTile = gameModel.getRoomBitmap(roomX, roomY)[py][px];

            switch(targetTile) {
                case TileTypes.TILE_DOOR:
                case TileTypes.TILE_FLOOR:
                    direction = _direction;
                    state = State.walk;
                    stateTime = 0.0f;
                    stateDuration = WALK_DURATION;
                    break;
                case TileTypes.TILE_NONE:
                case TileTypes.TILE_WALL:
                    break;
            }
        }
    }

    public boolean act(float delta) {
        int px= posX; int py = posY;
        switch (direction) {
            case east: px++; break;
            case west: px--; break;
            case north: py--; break;
            case south: py++; break;
        }
        switch(state) {
            case stand:
                break;
            case walk:
                stateTime += delta;
                stateProgress = stateTime / stateDuration;
                if(stateTime >= stateDuration) {
                    state = State.stand;
                    stateTime = stateDuration = stateProgress = 0.0f;
                    posX = px; posY = py;
                    return true;
                }
                break;
        }
        return false;
    }

    public int getRoomX() {
        return roomX;
    }
    public int getRoomY() {
        return roomY;
    }
    public int getPosX() {
        return posX;
    }
    public int getPosY() {
        return posY;
    }

    public void setRoom(int roomX, int roomY) {
        this.roomX = roomX;
        this.roomY = roomY;
    }

    public void setPosition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }
}
