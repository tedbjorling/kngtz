package com.holidaystudios.kngt.view.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.model.Direction;
import com.holidaystudios.kngt.model.KnightModel;
import com.holidaystudios.kngt.view.UIAssets;
import com.holidaystudios.kngt.view.ViewListener;
import java.util.ArrayList;
import java.util.List;

public class KnightView extends Actor {
    public KnightModel model;

    private List<ViewListener> listeners = new ArrayList<ViewListener>();

    private Rectangle bounds = new Rectangle();

    float positionXDelta = 0.0f;
    float positionYDelta = 0.0f;

    public KnightView(KnightModel _knightModel) {
        model = _knightModel;
        setColor(Color.WHITE);
        setWidth(Defs.TILE_SIZE);
        setHeight(Defs.TILE_SIZE);
    }

    public void addListener(final ViewListener toAdd) {
        listeners.add(toAdd);
    }


    private void doWalk() {
        switch(model.getDirection()) {
            case north:
                positionYDelta = model.getStateProgress() * getHeight();
                break;
            case west:
                positionXDelta = -model.getStateProgress() * getWidth();
                break;
            case south:
                positionYDelta = -model.getStateProgress() * getHeight();
                break;
            case east:
                positionXDelta = model.getStateProgress() * getWidth();
                break;
        }
    }

    public Boolean isWalking() {
        return model.getState() == KnightModel.State.walk;
    }

    @Override
    public void act(float delta){
        super.act(delta);
        updateBounds();

        if(model.getStateTime() < model.getStateDuration()) {
            switch(model.getState()) {
                case walk:
                    doWalk();
                    break;
                case stand:
                default:
                    // do nothing
                    break;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        Animation anim = UIAssets.knightAnimation;
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
        batch.draw(anim.getKeyFrame(model.getStateProgress() * anim.animationDuration), getX() + positionXDelta, getY() + positionYDelta, 32.0f, 32.0f, 64.0f, 64.0f, 1.0f, 1.0f, getRotation());
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

}