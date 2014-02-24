package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class GamePlayAssets {

    public static Texture floorTexture;
    public static Texture wallTexture;
    public static Texture doorTexture;

    public static void load() {
        floorTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-floor_01.png"));
        wallTexture = new Texture(Gdx.files.internal("tiles/basic/wall.png"));
        doorTexture = new Texture(Gdx.files.internal("tiles/basic/door.png"));
    }
}