package com.holidaystudios.kngt.model;

import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.controller.Knight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class GameModel {

    private CaveModel cave;
    private List<Knight> knights;

    public GameModel(final String seed, final Integer roomsX, final Integer roomsY) {
        cave = new CaveModel(seed, roomsX, roomsY);
        knights =  new ArrayList<Knight>();
    }

    public void addKnight(final Knight knight) {

        //Pick a random room
        final Integer roomX = (int) Math.floor(Math.random() * cave.getRoomsX());
        final Integer roomY = (int) Math.floor(Math.random() * cave.getRoomsY());
        knight.setRoom(roomX, roomY);

        //Pick a random "available" position inside of this room
        final Integer[][] bitmap = cave.getRoomBitmap(roomX, roomY);
        while (true) {
            final Integer randPosX = (int) Math.floor(Math.random() * Defs.TILES_PER_DISTANCE);
            final Integer randPosY = (int) Math.floor(Math.random() * Defs.TILES_PER_DISTANCE);
            if (bitmap[randPosY][randPosX] == TileTypes.TILE_FLOOR) {
                knight.setPosition(randPosX, randPosY);
                break;
            }
        }
        knights.add(knight);
    }

    public Integer[][] getBitmap() {
        return cave.getBitmap();
    }

    public Integer[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return this.cave.getRoomBitmap(cx, cy);
    }

}
