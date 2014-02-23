package com.holidaystudios.kngt.gameplay;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerKnight extends Actor {
    private Rectangle bounds = new Rectangle();

    public PlayerKnight() {
        setWidth(64);
        setHeight(64);
        setPosition(64, 64);
        setColor(Color.WHITE);
    }

    @Override
    public void act(float delta){
        super.act(delta);
        updateBounds();
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
        batch.draw(GamePlayAssets.knightTexture[3], getX(), getY());
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }
}