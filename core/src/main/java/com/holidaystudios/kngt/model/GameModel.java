package com.holidaystudios.kngt.model;

import com.badlogic.gdx.Gdx;
import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.networking.GameServer;
import com.sun.java_cup.internal.runtime.virtual_parse_stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class GameModel {

    private CaveModel cave;
    private List<KnightModel> knights;

    public GameModel(final String seed, final Integer roomsX, final Integer roomsY) {
        cave = new CaveModel(seed, roomsX, roomsY);
        knights =  new ArrayList<KnightModel>();
    }

    public KnightModel addKnight(GameServer gsrv) {
        KnightModel knight = new KnightModel(knights.size());
        knights.add(knight);

        //Pick a random room
        final int roomX = (int) Math.floor(Math.random() * cave.getRoomsX());
        final int roomY = (int) Math.floor(Math.random() * cave.getRoomsY());
        knight.setRoom(roomX, roomY);

        //Pick a random "available" position inside of this room
        final byte[][] bitmap = cave.getRoomBitmap(roomX, roomY);
        while (true) {
            final int randPosX = (int) Math.floor(Math.random() * Defs.TILES_PER_DISTANCE);
            final int randPosY = (int) Math.floor(Math.random() * Defs.TILES_PER_DISTANCE);
            if (bitmap[randPosY][randPosX] == TileTypes.TILE_FLOOR) {
                knight.setPosition(randPosX, randPosY);
                Gdx.app.log("kngt", "New knight at position (" + randPosX + ", " + randPosY + ")");
                break;
            }
        }

        return knight;
    }

    public byte[][] getBitmap() {
        return cave.getBitmap();
    }

    public byte[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return this.cave.getRoomBitmap(cx, cy);
    }

    public boolean act(float secondsDelta) {
        boolean refreshPublish = false;

        ListIterator<KnightModel> i = knights.listIterator();

        while(i.hasNext()) {
            KnightModel k = i.next();
            refreshPublish = refreshPublish || k.act(secondsDelta);
        }

        return refreshPublish;
    }
}
