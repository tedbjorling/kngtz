package com.holidaystudios.kngt.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.holidaystudios.kngt.entities.Game;

public class GamePlayScreen implements Screen, GestureListener {
    private Stage stage;
    public PlayerKnight playerKnight;


    public final Integer TILE_SIZE = 64;
    public final Integer TILES_PER_DISTANCE = 15;
    static final Integer VIEW_WIDTH  = 800;
    static final Integer VIEW_HEIGHT = 480;

    private Game game;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Rectangle glViewport;

    public GamePlayScreen() {
        stage = new Stage();

        playerKnight = new PlayerKnight();
        stage.addActor(playerKnight);

        //Load assets
        GamePlayAssets.load();

        Integer dimension = TILES_PER_DISTANCE * TILE_SIZE;

        camera = new OrthographicCamera(dimension, dimension);
        camera.position.set(dimension / 2, dimension / 2, 0);
        //camera.zoom = 2f;
        camera.update();

        game = new Game("Foo", 10, 10, 15);
        map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(TILES_PER_DISTANCE, TILES_PER_DISTANCE, TILE_SIZE, TILE_SIZE);

        //Add the first room
        Integer[][] bitmap = game.getRoomBitmap(1, 0);
        for (int y=0; y<bitmap.length; y++) {
            for (int x=0; x<bitmap[y].length; x++) {
                final Integer p = bitmap[y][x];
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                StaticTiledMapTile mapTile = null;

                if (p == GamePlayTiles.TILE_FLOOR) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(GamePlayAssets.floorTexture));
                } else if (p == GamePlayTiles.TILE_WALL) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(GamePlayAssets.wallTexture));
                } else if (p == GamePlayTiles.TILE_DOOR) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(GamePlayAssets.doorTexture));
                }
                cell.setTile(mapTile);
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void resize(int width, int height) {
        stage.setViewport(TILE_SIZE * TILES_PER_DISTANCE, TILE_SIZE * TILES_PER_DISTANCE, true);
        stage.getCamera().translate(-stage.getGutterWidth(), -stage.getGutterHeight(), 0);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setView(camera);
        renderer.render();

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