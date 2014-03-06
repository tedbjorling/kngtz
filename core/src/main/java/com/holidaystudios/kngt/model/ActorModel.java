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
public class ActorModel {

    public boolean dirtyFlag; // this is a flag that indicates the server state of this actor change - i.e the actor needs to be republished to the clients observing it.

    int roomX;
    int roomY;
    int posX;
    int posY;

    public boolean isInSameRoomAs(ActorModel actor) {
        return (roomX == actor.roomX && roomY == actor.roomY);
    }

}