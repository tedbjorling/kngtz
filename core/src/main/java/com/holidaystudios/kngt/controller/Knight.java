package com.holidaystudios.kngt.controller;

import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.Direction;
import com.holidaystudios.kngt.model.KnightModel;
import com.holidaystudios.kngt.view.actors.KnightView;

/**
 * Created by tedbjorling on 2014-02-26.
 */
public class Knight {

    private KnightModel model;
    private KnightView view;

    public Knight() {
        model = new KnightModel();
        view = new KnightView();
    }

    public KnightModel getModel() {
        return model;
    }

    public KnightView getView() {
        return view;
    }

    public void move(final Direction d) {
        switch (d) {
            case north:
                model.setPosY(model.getPosY()-1);
                break;
            case south:
                model.setPosY(model.getPosY()+1);
                break;
            case east:
                model.setPosX(model.getPosX()+1);
                break;
            case west:
                model.setPosX(model.getPosX()-1);
                break;
        }
        view.move(d);
    }

    public void setRoom(final Integer roomX, final Integer roomY) {
        this.model.setRoomX(roomX);
        this.model.setRoomY(roomY);
    }

    public void setPosition(final Integer posX, final Integer posY) {
        this.model.setPosX(posX);
        this.model.setPosY(posY);
        this.view.setPosition(posX * Defs.TILE_SIZE, posY * Defs.TILE_SIZE);
    }

}
