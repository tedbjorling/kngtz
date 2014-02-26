package com.holidaystudios.kngt.controller;

import com.badlogic.gdx.Input;
import com.holidaystudios.kngt.Direction;
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
        model = new GameModel("733 kru", 10, 10);
        view = new GameView();
        view.addListener(this);

        //Add a knight
        knight = new Knight();
        model.addKnight(knight);

        view.addToStage(knight.getView());
        view.renderRoom(model.getRoomBitmap(knight.getModel().getRoomX(), knight.getModel().getRoomY()));
    }

    public GameModel getModel() {
        return model;
    }

    public GameView getView() {
        return view;
    }

    @Override
    public void handleViewEvent(final EventType type, final Object data) {
        switch (type) {
            case fling:
                final Direction dir = (Direction) data;
                knight.move(dir);
                break;
            case keyDown:
                final Integer key = (Integer) data;
                switch (key) {
                    case Input.Keys.UP:
                        knight.move(Direction.north);
                        break;
                    case Input.Keys.DOWN:
                        knight.move(Direction.south);
                        break;
                    case Input.Keys.LEFT:
                        knight.move(Direction.west);
                        break;
                    case Input.Keys.RIGHT:
                        knight.move(Direction.east);
                        break;
                }
        }
    }
}
