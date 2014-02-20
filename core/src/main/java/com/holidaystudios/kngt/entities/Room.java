package com.holidaystudios.kngt.entities;

import com.badlogic.gdx.math.Rectangle;
import com.holidaystudios.kngt.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class Room {

    public enum DoorPosition {
        S, W, E, N
    }

    public final static Integer TILE_NONE   = 0;
    public final static Integer TILE_FLOOR  = 1;
    public final static Integer TILE_WALL   = 2;
    public final static Integer TILE_DOOR   = 3;


    private Integer posX, posY, pixelX, pixelY, pixelWidth, pixelHeight;
    private Integer[][] bitmap;
    private Map<DoorPosition, Integer> doors = new HashMap<DoorPosition, Integer>();

    public Room(final Integer posX, final Integer posY) {
        this.posX = posX;
        this.posY = posY;

        //Convenience variables
        this.pixelX = Cave.MAX_WALL_LENGTH*this.posX;
        this.pixelY = Cave.MAX_WALL_LENGTH*this.posY;
        this.pixelWidth  = Cave.MAX_WALL_LENGTH;
        this.pixelHeight = Cave.MAX_WALL_LENGTH;

        //Init the underlying room bitmap
        initBitmap();
    }


    private void initBitmap() {
        bitmap = new Integer[this.pixelHeight][];
        for (int y=0; y<this.pixelHeight; y++) {
            bitmap[y] = new Integer[this.pixelWidth];
            for (int x=0; x<this.pixelWidth; x++) {
                bitmap[y][x] = TILE_NONE;
            }
        }
    }

    private boolean hasAnyDoor(final Map<DoorPosition, Integer> _doors) {
        return _doors.containsKey(DoorPosition.N)
            || _doors.containsKey(DoorPosition.S)
            || _doors.containsKey(DoorPosition.E)
            || _doors.containsKey(DoorPosition.W);
    }

    public boolean hasAnyDoor() {
        return this.hasAnyDoor(this.doors);
    }

    private boolean hasDoor(final Map<DoorPosition, Integer> _doors, final DoorPosition pos) {
        return _doors.containsKey(pos);
    }

    public boolean hasDoor(final DoorPosition pos) {
        return this.hasDoor(this.doors, pos);
    }

    private Integer getDoor(final Map<DoorPosition, Integer> _doors, final DoorPosition pos) {
        return _doors.get(pos);
    }

    public Integer getDoor(final DoorPosition pos) {
        return this.getDoor(this.doors, pos);
    }

    public void setDoor(final DoorPosition pos, final Integer offset) {
        doors.put(pos, offset);
    }

    public Integer[][] getBitmap() {
        return this.bitmap;
    }

    private void applyWallInBitmap() {

        //Draw the wall
        for (int x=0; x<Cave.MAX_WALL_LENGTH; x++) {
            for (int y=0; y<Cave.MAX_WALL_LENGTH; y++) {

                //Is any of my neighbours a floor tile? If so, I am a wall
                if (this.bitmap[y][x] == TILE_FLOOR) {
                    continue;
                }

                int l = Cave.MAX_WALL_LENGTH-1;
                if ((y>0 && x>0 && this.bitmap[y-1][x-1] == TILE_FLOOR)
                ||  (y>0 && 	   this.bitmap[y-1][x]   == TILE_FLOOR)
                ||  (y>0 && x<l && this.bitmap[y-1][x+1] == TILE_FLOOR)
                ||  (       x>0 && this.bitmap[y][x-1]   == TILE_FLOOR)
                ||  (       x<l && this.bitmap[y][x+1]   == TILE_FLOOR)
                ||  (y<l && x>0 && this.bitmap[y+1][x-1] == TILE_FLOOR)
                ||  (y<l && 	   this.bitmap[y+1][x]   == TILE_FLOOR)
                ||  (y<l && x<l && this.bitmap[y+1][x+1] == TILE_FLOOR)) {
                    this.bitmap[y][x] = TILE_WALL;
                }
            }
        }
    }

    private void connectDoors(final Map<DoorPosition, Integer> _doors) {
        //Create a corridor
        //First, how wide should it be?
        final Integer corridorBreadth = Math.max(5, (int) Math.round(NumberUtils.getRandom()*7));

        if ((this.hasDoor(_doors, DoorPosition.N) || this.hasDoor(_doors, DoorPosition.S))
         && (this.hasDoor(_doors, DoorPosition.W) || this.hasDoor(_doors, DoorPosition.E))) {
            //Knee corridor

            //Simply enclose the room in an arbitrarily sized rectangle
            final Rectangle dim = new Rectangle(
                this.hasDoor(_doors, DoorPosition.N)? this.getDoor(_doors, DoorPosition.N) : this.getDoor(_doors, DoorPosition.S),
                this.hasDoor(_doors, DoorPosition.E)? this.getDoor(_doors, DoorPosition.E) : this.getDoor(_doors, DoorPosition.W),
                0,
                0
            );

            if (this.hasDoor(_doors, DoorPosition.E)) {
                dim.width = this.pixelWidth - dim.x;
            } else {
                dim.width = dim.x;
            }

            if (this.hasDoor(_doors, DoorPosition.S)) {
                dim.height = this.pixelWidth - dim.y;
            } else {
                dim.height = dim.y;
            }

            dim.width += 3;
            dim.height += 3;

            dim.width = Math.min(this.pixelWidth, dim.width);
            dim.height = Math.min(this.pixelHeight, dim.height);

            //Nudge
            if (this.hasDoor(_doors, DoorPosition.E)) {
                dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, dim.x));
            } else {
                dim.x = 0;
            }

            if (this.hasDoor(_doors, DoorPosition.S)) {
                dim.y = Math.max(0, Math.min(this.pixelHeight-dim.height, dim.y));
            } else {
                dim.y = 0;
            }

            //Draw room
            for (int y=(int)dim.y; y<dim.y+dim.height; y++) {
                for (int x=(int)dim.x; x<dim.x+dim.width; x++) {
                    if (x == dim.x || x == dim.x+dim.width-1
                    ||  y == dim.y || y == dim.y+dim.height-1) {
                        //room.bitmap[y][x] = defs.tiles.types.wall;
                    } else {
                        this.bitmap[y][x] = TILE_FLOOR;
                    }
                }
            }
        } else if (this.hasDoor(_doors, DoorPosition.N) && this.hasDoor(_doors, DoorPosition.S)) {
            //Vertical corridor

            int nX = Math.max(1, Math.min(this.pixelWidth-corridorBreadth-1, Math.round(this.getDoor(_doors, DoorPosition.N) - corridorBreadth/2)));
            int sX = Math.max(1, Math.min(this.pixelWidth-corridorBreadth-1, Math.round(this.getDoor(_doors, DoorPosition.S) - corridorBreadth/2)));

            if (nX == sX) {
                //Straight corridor
                for (int y=0; y<Cave.MAX_WALL_LENGTH; y++) {
                    for (int x=nX; x<nX+corridorBreadth; x++) {
                        if (y == 0 || y == Cave.MAX_WALL_LENGTH-1) {
                            //room.bitmap[y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][x] = TILE_FLOOR;
                        }
                    }
                }
            } else {

                int kneePos = (int) Math.round(Cave.MAX_WALL_LENGTH/2);

                //First paint the straight corridors
                for (int y=0; y<this.pixelHeight; y++) {
                    int x;
                    if (y<kneePos) {
                        x = nX;
                    } else {
                        x = sX;
                    }
                    for (int _x=x; _x<x+corridorBreadth; _x++) {
                        if (y == 0 || y == Cave.MAX_WALL_LENGTH-1) {
                            //room.bitmap[y][_x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][_x] = TILE_FLOOR;
                        }
                    }
                }

                //Then connect them using a passage
                int kneeStart = Math.round(kneePos - corridorBreadth/2);
                int startX, endX;
                if (nX < sX) {
                    startX = nX;
                    endX = sX+corridorBreadth;
                } else {
                    startX = sX;
                    endX = nX+corridorBreadth;
                }

                for (int x=startX; x<endX; x++) {
                    for (int y=kneeStart; y<kneeStart+corridorBreadth; y++) {
                        this.bitmap[y][x] = TILE_FLOOR;
                    }
                }
            }


        } else if (this.hasDoor(_doors, DoorPosition.E) && this.hasDoor(_doors, DoorPosition.W)) {
            //Horizontal corridor

            int wY = Math.max(1, Math.min(this.pixelHeight-corridorBreadth-1, Math.round(this.getDoor(_doors, DoorPosition.W) - corridorBreadth/2)));
            int eY = Math.max(1, Math.min(this.pixelHeight-corridorBreadth-1, Math.round(this.getDoor(_doors, DoorPosition.E) - corridorBreadth/2)));

            if (wY == eY) {
                //Straight corridor
                for (int x=0; x<Cave.MAX_WALL_LENGTH; x++) {
                    for (int y=wY; y<wY+corridorBreadth; y++) {
                        if (x == 0 || x == Cave.MAX_WALL_LENGTH-1) {
                            //room.bitmap[y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][x] = TILE_FLOOR;
                        }
                    }
                }
            } else {

                int kneePos = Math.round(Cave.MAX_WALL_LENGTH/2);

                //First paint the straight corridors
                for (int x=0; x<this.pixelWidth; x++) {
                    int y;
                    if (x<kneePos) {
                        y = wY;
                    } else {
                        y = eY;
                    }
                    for (int _y=y; _y<y+corridorBreadth; _y++) {
                        if (x == 0 || x == Cave.MAX_WALL_LENGTH-1) {
                            //room.bitmap[_y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[_y][x] = TILE_FLOOR;
                        }
                    }
                }

                //Then connect them using a passage
                int kneeStart = (int) Math.round(kneePos - corridorBreadth/2);
                int startY, endY;

                if (wY < eY) {
                    startY = wY;
                    endY = eY+corridorBreadth;
                } else {
                    startY = eY;
                    endY = wY+corridorBreadth;
                }

                for (int y=startY; y<endY; y++) {
                    for (int x=kneeStart; x<kneeStart+corridorBreadth; x++) {
                        this.bitmap[y][x] = TILE_FLOOR;
                    }
                }
            }
        }
    }

    public void createInterior() {

        /*
            Idea: The number of doors determines the characteristics of the room
            ----------------------------
            Number of doors = 1
                Any shape

            Number of doors = 2
                Doors opposite?
                -Corridor (optionally with a bend)
                -Else corridor with a knee

            Number of doors = 3
                Bounding box + random bulbs

            Number of doors = 4
                Full room + cut-outs
        */

        final Map.Entry<DoorPosition, Integer>[] availableDoors = (Map.Entry<DoorPosition, Integer>[]) this.doors.entrySet().toArray(new Map.Entry[this.doors.size()]);

        switch (availableDoors.length) {
            case 1:
                final Rectangle dim = new Rectangle(
                    0,
                    0,
                    Math.round(Cave.MIN_WALL_LENGTH + NumberUtils.getRandom() * (Cave.MAX_WALL_LENGTH - Cave.MIN_WALL_LENGTH)),
                    Math.round(Cave.MIN_WALL_LENGTH + NumberUtils.getRandom() * (Cave.MAX_WALL_LENGTH - Cave.MIN_WALL_LENGTH))
                );
                switch (availableDoors[0].getKey()) {
                    case S:
                        dim.y = this.pixelHeight - dim.height;
                        dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, Math.round(availableDoors[0].getValue() - dim.width/2)));
                        break;
                    case N:
                        dim.y = 0;
                        dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, Math.round(availableDoors[0].getValue() - dim.width/2)));
                        break;
                    case W:
                        dim.y = Math.max(0, Math.min(this.pixelHeight - dim.height, Math.round(availableDoors[0].getValue() - dim.height / 2)));
                        dim.x = 0;
                        break;
                    case E:
                        dim.y = Math.max(0, Math.min(this.pixelHeight-dim.height, Math.round(availableDoors[0].getValue() - dim.height/2)));
                        dim.x = this.pixelWidth - dim.width;
                        break;
                }
                //Draw room
                for (int y=(int)dim.y; y<dim.y+dim.height; y++) {
                    for (int x=(int)dim.x; x<dim.x+dim.width; x++) {
                        if (x == dim.x || x == dim.x+dim.width-1
                        ||  y == dim.y || y == dim.y+dim.height-1) {
                            this.bitmap[y][x] = TILE_WALL;
                        } else {
                            this.bitmap[y][x] = TILE_FLOOR;
                        }
                    }
                }
                break;


            case 2:
                this.connectDoors(this.doors);
                this.applyWallInBitmap();
                break;

            case 3:
                Map<DoorPosition, Integer> _doors;
                if (this.hasDoor(DoorPosition.N)) {
                    if (this.hasDoor(DoorPosition.E)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.S)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.W)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        this.connectDoors(_doors);
                    }
                } else if (this.hasDoor(DoorPosition.S)) {
                    if (this.hasDoor(DoorPosition.E)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.N)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.W)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        this.connectDoors(_doors);
                    }
                } else if (this.hasDoor(DoorPosition.W)) {
                    if (this.hasDoor(DoorPosition.E)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.N)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.S)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        this.connectDoors(_doors);
                    }
                } else if (this.hasDoor(DoorPosition.E)) {
                    if (this.hasDoor(DoorPosition.W)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        _doors.put(DoorPosition.W, doors.get(DoorPosition.W));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.N)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        _doors.put(DoorPosition.N, doors.get(DoorPosition.N));
                        this.connectDoors(_doors);
                    }
                    if (this.hasDoor(DoorPosition.S)) {
                        _doors = new HashMap<DoorPosition, Integer>();
                        _doors.put(DoorPosition.E, doors.get(DoorPosition.E));
                        _doors.put(DoorPosition.S, doors.get(DoorPosition.S));
                        this.connectDoors(_doors);
                    }
                }
                this.applyWallInBitmap();
                break;

            case 4:
                //Draw room
                for (int y=0; y<this.pixelHeight; y++) {
                    for (int x=0; x<this.pixelWidth; x++) {
                        if (x == 0 || x == this.pixelWidth-1
                        ||  y == 0 || y == this.pixelHeight-1) {
                            this.bitmap[y][x] = TILE_WALL;
                        } else {
                            this.bitmap[y][x] = TILE_FLOOR;
                        }
                    }
                }
                break;
        }

        //Apply doors to local bitmap
        if (this.hasDoor(DoorPosition.S)) {
            this.bitmap[this.pixelHeight-1][this.getDoor(DoorPosition.S)] = TILE_DOOR;
        }

        if (this.hasDoor(DoorPosition.N)) {
            this.bitmap[0][this.getDoor(DoorPosition.N)] = TILE_DOOR;
        }

        if (this.hasDoor(DoorPosition.W)) {
            this.bitmap[this.getDoor(DoorPosition.W)][0] = TILE_DOOR;
        }

        if (this.hasDoor(DoorPosition.E)) {
            this.bitmap[this.getDoor(DoorPosition.E)][this.pixelWidth-1] = TILE_DOOR;
        }

    }

}
