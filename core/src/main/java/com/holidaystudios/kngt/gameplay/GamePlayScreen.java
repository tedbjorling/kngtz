package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GamePlayScreen implements Screen, GestureListener {
    private Stage stage;
    private GamePlayTable gameTable;
    public PlayerKnight playerKnight;


    public final int TILE_SIZE = 64;
    public final int H_TILES = 10;
    public final int V_TILES = 14;

    public GamePlayScreen() {
        stage = new Stage();
        gameTable = new GamePlayTable(TILE_SIZE * H_TILES, TILE_SIZE * V_TILES);
        stage.addActor(gameTable);

        playerKnight = new PlayerKnight();
        stage.addActor(playerKnight);
    }

    public void resize(int width, int height) {
        stage.setViewport(TILE_SIZE * H_TILES, TILE_SIZE * V_TILES, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GestureDetector(this));
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        float velocity;
        Gdx.app.log("kngts", "fling - velocityX(" + velocityX + ") - velocityY(" + velocityY + ")");
        boolean horizontalMovement;
        if(Math.abs(velocityX) > Math.abs(velocityY)) {
            horizontalMovement = true;
            velocity = velocityX;
        } else {
            horizontalMovement = false;
            velocity = velocityY;
        }

        if(Math.abs(velocity) > 10.0f) {
            if(velocity < 0.0f) {
                if(horizontalMovement)
                    playerKnight.move(PlayerKnight.Direction.west);
                else
                    playerKnight.move(PlayerKnight.Direction.north);
            } else {
                if(horizontalMovement)
                    playerKnight.move(PlayerKnight.Direction.east);
                else
                    playerKnight.move(PlayerKnight.Direction.south);
            }
        }

        return false;
    }

    @Override public boolean panStop(float x, float y, int x2, int y2) {return false;}
    @Override public void resume() {}
    @Override public void pause() {}
    @Override public void dispose() {}
    @Override public boolean tap(float x, float y, int count, int button) {return false;}
    @Override public boolean touchDown(float x, float y, int pointer, int button) {return false;}
    @Override public boolean longPress(float x, float y) {return false;}
    @Override public boolean pan(float x, float y, float deltaX, float deltaY) {return false;}
    @Override public boolean zoom(float initialDistance, float distance) {return false;}
    @Override public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {return false;}

}