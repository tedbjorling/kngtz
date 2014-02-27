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

    public void gotoNextRoom(final Direction d) {
        switch (d) {
            case north:
                setRoom(getModel().getRoomX(), getModel().getRoomY()-1);
                setPosition(getModel().getPosX(), Defs.TILES_PER_DISTANCE-2);
                break;
            case south:
                setRoom(getModel().getRoomX(), getModel().getRoomY()+1);
                setPosition(getModel().getPosX(), 1);
                break;
            case east:
                setRoom(getModel().getRoomX()+1, getModel().getRoomY());
                setPosition(1, getModel().getPosY());
                break;
            case west:
                setRoom(getModel().getRoomX()-1, getModel().getRoomY());
                setPosition(Defs.TILES_PER_DISTANCE-2, getModel().getPosY());
                break;
        }
    }

    public void setRoom(final Integer roomX, final Integer roomY) {
        this.model.setRoomX(roomX);
        this.model.setRoomY(roomY);
    }

    public void setPosition(final Integer posX, final Integer posY) {
        this.model.setPosX(posX);
        this.model.setPosY(posY);
        this.view.setPosition(posX * Defs.TILE_SIZE, (Defs.TILE_SIZE * (Defs.TILES_PER_DISTANCE-1- posY))); //Inverse Y-axis
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Room : " + getModel().getRoomX() + ", " + getModel().getRoomY());
        sb.append(System.lineSeparator());
        sb.append("Pos  : " + getModel().getPosX() + ", " + getModel().getPosY());
        return sb.toString();
    }


}
