package com.holidaystudios.kngt.gameplay;

import java.util.Iterator;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GamePlayTable extends Table {
    public PlayerKnight playerKnight;

    public GamePlayTable(int hbound, int vbound) {
        setBounds(0, 0, hbound, vbound);
        setClip(true);
        playerKnight = new PlayerKnight();
        addActor(playerKnight);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }
}