package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.holidaystudios.kngt.tools.GifDecoder;

public class GamePlayAssets {
    public static Animation knightAnimation;

    public static void load() {
        knightAnimation = GifDecoder.loadGIFAnimation(Animation.LOOP, Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif").read());
    }
}