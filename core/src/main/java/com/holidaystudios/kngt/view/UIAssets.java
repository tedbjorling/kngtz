package com.holidaystudios.kngt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class UIAssets {

    public static Texture emptyTexture;
    public static Texture floorTexture;
    public static Texture wallTexture;
    public static Texture doorTexture;

    public static void load() {

        floorTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-floor_01.png"));
        wallTexture = new Texture(Gdx.files.internal("tiles/basic/wall.png"));
        doorTexture = new Texture(Gdx.files.internal("tiles/basic/door.png"));
        emptyTexture = new Texture(Gdx.files.internal("tiles/basic/empty.png"));
    }
}