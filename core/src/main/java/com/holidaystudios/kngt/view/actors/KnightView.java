package com.holidaystudios.kngt.view.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.Direction;
import com.holidaystudios.kngt.tools.GifDecoder;

public class KnightView extends Actor {


    private enum State {
        stand, walk
    };

    private Rectangle bounds = new Rectangle();

    float stateTime = 0.0f; // in seconds
    float stateDuration = 0.0f; // in seconds
    float stateProgress = 0.0f; // from 0.0f to 1.0f inclusive
    float positionXDelta = 0.0f;
    float positionYDelta = 0.0f;
    State state = State.stand;
    Direction direction = Direction.north;
    static Animation knightAnimation;

    boolean newMoveEnqueued = false;
    Direction enqueuedDirection = Direction.north;

    public KnightView() {
        setColor(Color.WHITE);
        setWidth(Defs.TILE_SIZE);
        setHeight(Defs.TILE_SIZE);
        knightAnimation = GifDecoder.loadGIFAnimation(Animation.LOOP, Gdx.files.internal("tiles/basic/gameplay-knight_red_walk.gif").read());
    }

    private void doWalk() {
        switch(direction) {
            case north:
                positionYDelta = stateProgress * getHeight();
                break;
            case west:
                positionXDelta = -stateProgress * getWidth();
                break;
            case south:
                positionYDelta = -stateProgress * getHeight();
                break;
            case east:
                positionXDelta = stateProgress * getWidth();
                break;
        }
    }

    private void finishWalk() {
        switch(direction) {
            case north:
                setPosition(getX(), getY() + getHeight());
                break;
            case west:
                setPosition(getX() - getWidth(), getY());
                break;
            case south:
                setPosition(getX(), getY() - getHeight());
                break;
            case east:
                setPosition(getX() + getHeight(), getY());
                break;
        }
        state = State.stand;
        stateDuration = stateTime = 0.0f;
        positionYDelta = positionXDelta = 0.0f;

        if(newMoveEnqueued) {
            newMoveEnqueued = false;
            move(enqueuedDirection);
        }
    }

    @Override
    public void act(float delta){
        super.act(delta);
        updateBounds();

        if(stateTime < stateDuration) {
            stateTime += delta;
            stateProgress = stateTime / stateDuration;
            switch(state) {
                case walk:
                    doWalk();
                    break;
                case stand:
                default:
                    // do nothing
                    break;
            }
        } else {
            switch(state) {
                case walk:
                    finishWalk();
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
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
        batch.draw(knightAnimation.getKeyFrame(stateTime), getX() + positionXDelta, getY() + positionYDelta, 32.0f, 32.0f, 64.0f, 64.0f, 1.0f, 1.0f, getRotation());
    }

    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    private void enqueueNewMove(Direction _direction) {
        newMoveEnqueued = true;
        enqueuedDirection = _direction;
    }

    public void move(Direction _direction) {
        if(state == State.stand) {
            direction = _direction;
            state = State.walk;
            stateTime = 0.0f;
            stateDuration = knightAnimation.animationDuration;

            switch(direction) {
                case north:
                    setRotation(0.0f);
                    break;
                case west:
                    setRotation(90.0f);
                    break;
                case south:
                    setRotation(180.0f);
                    break;
                case east:
                    setRotation(270.0f);
                    break;
            }
        } else {
            enqueueNewMove(_direction);
        }
    }
}