package com.holidaystudios.kngt.entities;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class Game {

    private Cave cave;

    public Game(final String seed, final Integer roomsX, final Integer roomsY, final Integer tilesPerDistance) {
        cave = new Cave(seed, roomsX, roomsY, tilesPerDistance);
    }

    public Integer[][] getBitmap() {
        return cave.getBitmap();
    }

    public Integer[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return this.cave.getRoomBitmap(cx, cy);
    }

}
