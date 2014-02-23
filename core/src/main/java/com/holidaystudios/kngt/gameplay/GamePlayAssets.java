package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.holidaystudios.kngt.tools.GifDecoder;

public class GamePlayAssets {
    public static Texture[] knightTexture;

    public static void load() {
        GifDecoder gdec = new GifDecoder();
        gdec.read(Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif").read());

        knightTexture = new Texture[gdec.getFrameCount()];

        int k;
        for(k = 0; k < gdec.getFrameCount(); k++) {
            knightTexture[k] = new Texture(gdec.getFrame(k));
        }

 //       knightTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif"));
    }
}