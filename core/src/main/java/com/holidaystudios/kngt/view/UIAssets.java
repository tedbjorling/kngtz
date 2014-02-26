package com.holidaystudios.kngt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Texture;
import com.holidaystudios.kngt.tools.GifDecoder;

public class UIAssets {

    public static Texture floorTexture;
    public static Texture wallTexture;
    public static Texture doorTexture;
    public static Animation knightAnimation;

    public static void load() {
        floorTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-floor_01.png"));
        wallTexture = new Texture(Gdx.files.internal("tiles/basic/wall.png"));
        doorTexture = new Texture(Gdx.files.internal("tiles/basic/door.png"));
        knightAnimation = GifDecoder.loadGIFAnimation(Animation.LOOP, Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif").read());
    }
}