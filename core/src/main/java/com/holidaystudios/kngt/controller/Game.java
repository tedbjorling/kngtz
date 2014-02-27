package com.holidaystudios.kngt.controller;

import com.badlogic.gdx.Input;
import com.holidaystudios.kngt.Direction;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.model.GameModel;
import com.holidaystudios.kngt.view.GameView;
import com.holidaystudios.kngt.view.ViewListener;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public class Game implements ViewListener {

    private GameModel model;
    private GameView view;

    //XXX put in a list instead
    private Knight knight;

    public Game() {
        model = new GameModel("733 kru", 5, 5);
        view = new GameView();
        view.addListener(this);

        //Add a knight
        knight = new Knight();
        model.addKnight(knight);

        /*
        knight.getModel().setRoomX(0);
        knight.getModel().setRoomY(2);
        */

        view.addToStage(knight.getView());
        view.renderRoom(model.getRoomBitmap(knight.getModel().getRoomX(), knight.getModel().getRoomY()));

        //knight.setPosition(12, 1);
        //System.out.println(knight);
    }

    public GameModel getModel() {
        return model;
    }

    public GameView getView() {
        return view;
    }

    private void handleMovement(final Knight knight, final Direction dir) {
        Integer posX = knight.getModel().getPosX();
        Integer posY = knight.getModel().getPosY();
        switch (dir) {
            case east: posX++; break;
            case west: posX--; break;
            case north: posY--; break;
            case south: posY++; break;
        }

        final Integer targetTile = model.getRoomBitmap(knight.getModel().getRoomX(), knight.getModel().getRoomY())[posY][posX];
        if (targetTile == TileTypes.TILE_FLOOR) {
            knight.move(dir);
        } else if (targetTile == TileTypes.TILE_DOOR) {
            knight.gotoNextRoom(dir);
            view.renderRoom(model.getRoomBitmap(knight.getModel().getRoomX(), knight.getModel().getRoomY()));
        }
    }

    @Override
    public void handleViewEvent(final EventType type, final Object data) {
        switch (type) {
            case fling: {
                    handleMovement(knight, (Direction) data);
                }
                break;
            case keyDown: {
                    final Integer key = (Integer) data;
                    Direction dir = null;
                    switch (key) {
                        case Input.Keys.UP:
                            dir = Direction.north;
                            break;
                        case Input.Keys.DOWN:
                            dir = Direction.south;
                            break;
                        case Input.Keys.LEFT:
                            dir = Direction.west;
                            break;
                        case Input.Keys.RIGHT:
                            dir = Direction.east;
                            break;
                    }
                    if (dir != null) {
                        handleMovement(knight, dir);
                    }
                }
                break;
        }
    }
}
