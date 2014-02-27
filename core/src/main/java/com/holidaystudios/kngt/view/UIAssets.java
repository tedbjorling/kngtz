package com.holidaystudios.kngt.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Texture;
import com.holidaystudios.kngt.tools.GifDecoder;

public class UIAssets {

    public static Texture emptyTexture;
    public static Texture floorTexture;
    public static Texture wallTexture;
    public static Texture doorTexture;

    private static GifDecoder.GIFAnimation knightAnimation_GIF;

    public static Animation knightAnimation;

    public static void load() {
        emptyTexture = new Texture(Gdx.files.internal("tiles/basic/empty.png"));
        floorTexture = new Texture(Gdx.files.internal("tiles/basic/gameplay-floor_01.png"));
        wallTexture = new Texture(Gdx.files.internal("tiles/basic/wall.png"));
        doorTexture = new Texture(Gdx.files.internal("tiles/basic/door.png"));

        knightAnimation_GIF = GifDecoder.loadGIFAnimation(Animation.LOOP, Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif").read());
        refresh();
    }

    public static void refresh() {
        // xxx because of some internal thing in libgdx the Texture that is created from a pixmap is not properly recovered when
        // an Android activity resumes. So at resume, we mush recreate the Texture object using the same pixmap. (The pixmap seems to survive OK though..)
        knightAnimation = knightAnimation_GIF.rebuildAnimation();
    }
}