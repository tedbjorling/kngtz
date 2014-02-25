package com.holidaystudios.kngt.model;

import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.TileTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class GameModel {

    private CaveModel cave;
    private List<KnightModel> knights;

    public GameModel(final String seed, final Integer roomsX, final Integer roomsY, final Integer tilesPerDistance) {
        cave = new CaveModel(seed, roomsX, roomsY, tilesPerDistance);
        knights =  new ArrayList<KnightModel>();
    }

    public KnightModel addKnight() {
        final KnightModel knight = new KnightModel();

        //Pick a random room
        knight.setRoomX((int) Math.round(Math.random() * cave.getRoomsX()));
        knight.setRoomY((int) Math.round(Math.random() * cave.getRoomsY()));

        //Pick a random "available" position inside of this room
        final Integer[][] bitmap = cave.getRoomBitmap(knight.getRoomX(), knight.getRoomY());
        while (true) {
            final Integer randPosX = (int) Math.round(Math.random() * Defs.TILES_PER_DISTANCE);
            final Integer randPosY = (int) Math.round(Math.random() * Defs.TILES_PER_DISTANCE);
            if (bitmap[randPosY][randPosX] == TileTypes.TILE_FLOOR) {
                knight.setPosX(randPosX);
                knight.setPosY(randPosY);
                break;
            }
        }
        knights.add(knight);
        return knight;
    }

    public Integer[][] getBitmap() {
        return cave.getBitmap();
    }

    public Integer[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return this.cave.getRoomBitmap(cx, cy);
    }

}
