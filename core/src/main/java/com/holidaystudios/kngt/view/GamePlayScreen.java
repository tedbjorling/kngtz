package com.holidaystudios.kngt.view;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.model.GameModel;
import com.holidaystudios.kngt.view.actors.PlayerKnight;

public class GamePlayScreen implements Screen, GestureListener {

    private Stage stage;
    public PlayerKnight playerKnight;

    private GameModel game;
    private TiledMap map;
    private TiledMapRenderer renderer;
    private OrthographicCamera camera;


    public GamePlayScreen() {
        stage = new Stage();

        playerKnight = new PlayerKnight();
        stage.addActor(playerKnight);

        //Load assets
        UIAssets.load();

        Integer dimension = Defs.TILES_PER_DISTANCE * Defs.TILE_SIZE;

        camera = new OrthographicCamera(dimension, dimension);
        camera.position.set(dimension / 2, dimension / 2, 0);
        camera.update();
        stage.setCamera(camera);

        game = new GameModel("Foo", 10, 10, 15);
        game.addKnight();
        setRoom(1, 0);
    }

    public void setRoom(final Integer cx, final Integer cy) {
        map = new TiledMap();
        MapLayers layers = map.getLayers();
        TiledMapTileLayer layer = new TiledMapTileLayer(
            Defs.TILES_PER_DISTANCE,
            Defs.TILES_PER_DISTANCE,
            Defs.TILE_SIZE,
            Defs.TILE_SIZE
        );

        //Add the first room
        Integer[][] bitmap = game.getRoomBitmap(cx, cy);
        for (int y=0; y<bitmap.length; y++) {
            for (int x=0; x<bitmap[y].length; x++) {
                final Integer p = bitmap[y][x];
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                StaticTiledMapTile mapTile = null;

                if (p == TileTypes.TILE_FLOOR) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(UIAssets.floorTexture));
                } else if (p == TileTypes.TILE_WALL) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(UIAssets.wallTexture));
                } else if (p == TileTypes.TILE_DOOR) {
                    mapTile = new StaticTiledMapTile(new TextureRegion(UIAssets.doorTexture));
                }
                cell.setTile(mapTile);
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);
        renderer = new OrthogonalTiledMapRenderer(map);
    }

    public void resize(int width, int height) {
        stage.setViewport(Defs.TILE_SIZE * Defs.TILES_PER_DISTANCE, Defs.TILE_SIZE * Defs.TILES_PER_DISTANCE, true);
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