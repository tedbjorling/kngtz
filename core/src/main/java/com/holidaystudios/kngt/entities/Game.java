package com.holidaystudios.kngt.entities;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class Game {

    private Cave cave;

    public Game() {
        cave = new Cave("Foo", 100, 100);
    }

    public Integer[][] getBitmap() {
        return cave.getBitmap();
    }

}
