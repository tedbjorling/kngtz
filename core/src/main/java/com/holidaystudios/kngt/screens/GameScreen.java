package com.holidaystudios.kngt.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.holidaystudios.kngt.KngtzMain;
import com.holidaystudios.kngt.entities.Game;
import com.holidaystudios.kngt.entities.Room;

/**
 * Created by tedbjorling on 2014-02-21.
 */
public class GameScreen implements Screen {

    static final Integer TILE_DIM = 64;
    static final Integer VIEW_WIDTH  = 800;
    static final Integer VIEW_HEIGHT = 480;

    private KngtzMain kngtz;

    private Game game;

    private OrthographicCamera camera;
    private Rectangle glViewport;

    private Texture floorTexture;
    private Texture wallTexture;
    private Texture doorTexture;


    public GameScreen(final KngtzMain _kngtz) {
        kngtz = _kngtz;
        floorTexture = new Texture(Gdx.files.internal("tiles/basic/floor.png"));
        wallTexture = new Texture(Gdx.files.internal("tiles/basic/wall.png"));
        doorTexture = new Texture(Gdx.files.internal("tiles/basic/door.png"));

        camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
        camera.position.set(VIEW_WIDTH / 2, VIEW_HEIGHT / 2, 0);
        camera.zoom = 2f;
        glViewport = new Rectangle(0, 0, VIEW_WIDTH, VIEW_HEIGHT);

        //Create game
        game = new Game();
    }

    @Override
    public void render(float delta) {
        handleInput();

        GL10 gl = Gdx.graphics.getGL10();
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                      (int) glViewport.width, (int) glViewport.height);
        camera.update();
        camera.apply(gl);

        kngtz.batch.setProjectionMatrix(camera.combined);
		kngtz.batch.begin();

        //Draw the cave bitmap
        final Integer[][] caveBitmap = game.getBitmap();
        for (int y=0; y<caveBitmap.length; y++) {
            for (int x=0; x<caveBitmap[y].length; x++) {
                final Integer p = caveBitmap[y][x];

                if (p == Room.TILE_FLOOR) {
                    kngtz.batch.draw(floorTexture, x * TILE_DIM, y * TILE_DIM);
                } else if (p == Room.TILE_WALL) {
                    kngtz.batch.draw(wallTexture, x * TILE_DIM, y * TILE_DIM);
                } else if (p == Room.TILE_DOOR) {
                    kngtz.batch.draw(doorTexture, x*TILE_DIM, y*TILE_DIM);
                }
            }
        }

        kngtz.batch.end();
    }

    private void handleInput() {

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.1;
            System.out.println(camera.zoom);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            if (camera.zoom > 0.1)
                camera.zoom -= 0.1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-13*(camera.zoom/3), 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(13*(camera.zoom/3), 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -13*(camera.zoom/3), 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 13*(camera.zoom/3), 0);
        }
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        doorTexture.dispose();
    }

    //OrthographicCamera camera;

}
