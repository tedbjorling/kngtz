package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class GamePlayAssets {
    public static Texture knightTexture;

    public static void load() {
        knightTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif"));
    }
}