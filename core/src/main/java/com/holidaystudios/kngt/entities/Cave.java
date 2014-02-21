package com.holidaystudios.kngt.entities;

import com.holidaystudios.kngt.NumberUtils;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class Cave {

    public final static Integer MIN_WALL_LENGTH = 5;
    public final static Integer MAX_WALL_LENGTH = 15;

    private String seed;
    private Integer caveWidth;
    private Integer caveHeight;
    private Room[][] rooms;

    public Cave(final String seed, final Integer caveWidth, final Integer caveHeight) {
        NumberUtils.setSeed(seed);
        this.caveWidth = caveWidth;
        this.caveHeight = caveHeight;

        //Start mapping out the individual rooms
        this.createRooms();

        this.debugCave();
    }

    public Integer[][] getBitmap() {

        final Integer[][] caveBitmap = new Integer[MAX_WALL_LENGTH*rooms.length][MAX_WALL_LENGTH*rooms.length];

        //Fill the main bitmap
        for (int cy=0; cy<rooms.length; cy++) {
            for (int cx=0; cx<rooms[cy].length; cx++) {
                final Integer[][] bitmap = rooms[cy][cx].getBitmap();
                final Integer offsetX = cx*MAX_WALL_LENGTH;
                final Integer offsetY = cy*MAX_WALL_LENGTH;

                for (int y=0; y<bitmap.length; y++) {
                    for (int x=0; x<bitmap[y].length; x++) {

                        caveBitmap[y+offsetY][x+offsetX] = bitmap[y][x];

                        /* ENABLE FOR COORDINATES IN DEBUG OUTPUT
                        if (x == 0 && y == 0) {
                            caveBitmap[y+offsetY][x+offsetX] = 1000+cx;
                        } else if (x == 1 && y == 0) {
                            caveBitmap[y+offsetY][x+offsetX] = 2000;
                        } else if (x == 2 && y == 0) {
                            caveBitmap[y+offsetY][x+offsetX] = 1000+cy;
                        }
                        */
                    }
                }
            }
        }

        return caveBitmap;
    }

    private void debugCave() {

        final Integer[][] caveBitmap = this.getBitmap();

        //Output the cave bitmap to console
        for (int y=0; y<caveBitmap.length; y++) {
            for (int x=0; x<caveBitmap[y].length; x++) {
                final Integer p = caveBitmap[y][x];

                if (p == 2000) {
                    System.out.print(',');
                } else if (p >= 1000) {
                    System.out.print(Integer.toString(p-1000)); //Stupid java
                } else {

                    if (p == Room.TILE_FLOOR) {
                        System.out.print('.');
                    } else if (p == Room.TILE_WALL) {
                        System.out.print('#');
                    } else if (p == Room.TILE_DOOR) {
                        System.out.print('O');
                    } else {
                        System.out.print(' ');
                    }
                }
            }
            System.out.println();
        }
    }

    private Integer createDoorPositionHelper() {
        return 2 + (int) Math.round(NumberUtils.getRandom() * (MAX_WALL_LENGTH-4));
    }

    private void createRooms() {
        final Integer cavitiesX = (int) Math.round(Math.floor(this.caveWidth/MAX_WALL_LENGTH));
        final Integer cavitiesY = (int) Math.round(Math.floor(this.caveHeight/MAX_WALL_LENGTH));

        //Create the rooms
        rooms = new Room[cavitiesY][];
        for (int cy=0; cy<cavitiesY; cy++) {
            rooms[cy] = new Room[cavitiesX];
            for (int cx=0; cx<cavitiesX; cx++) {
                rooms[cy][cx] = new Room(cx, cy);
            }
        }

        //Create doors
        for (int cy=0; cy<cavitiesY; cy++) {
            for (int cx=0; cx<cavitiesX; cx++) {
                final Room thisRoom = rooms[cy][cx];

                //Make sure that each cavity has at least one door
                while (!thisRoom.hasAnyDoor()) {

                    //South door
                    if (cy<(cavitiesY-1)) {
                        final Room roomBelow = rooms[cy+1][cx];

                        //Does the room below has a door pointing upwards?
                        if (roomBelow.hasDoor(Room.DoorPosition.N)) {
                            thisRoom.setDoor(Room.DoorPosition.S, roomBelow.getDoor(Room.DoorPosition.N));
                        } else {
                            if (NumberUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(Room.DoorPosition.S, doorOffset);
                                roomBelow.setDoor(Room.DoorPosition.N, doorOffset);
                            }
                        }
                    }

                    //North door
                    if (cy>0) {
                        final Room roomAbove = rooms[cy-1][cx];

                        //Does the room above has a door pointing downwards?
                        if (roomAbove.hasDoor(Room.DoorPosition.S)) {
                            thisRoom.setDoor(Room.DoorPosition.N, roomAbove.getDoor(Room.DoorPosition.S));
                        } else {
                            if (NumberUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(Room.DoorPosition.N, doorOffset);
                                roomAbove.setDoor(Room.DoorPosition.S, doorOffset);
                            }
                        }
                    }

                    //West door
                    if (cx>0) {
                        final Room roomLeft = rooms[cy][cx-1];

                        //Does the room to the left has a door pointing right?
                        if (roomLeft.hasDoor(Room.DoorPosition.E)) {
                            thisRoom.setDoor(Room.DoorPosition.W, roomLeft.getDoor(Room.DoorPosition.E));
                        } else {
                            if (NumberUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(Room.DoorPosition.W, doorOffset);
                                roomLeft.setDoor(Room.DoorPosition.E, doorOffset);
                            }
                        }
                    }

                    //East door
                    if (cx<(cavitiesX-1)) {
                        final Room roomRight = rooms[cy][cx+1];

                        //Does the room to the right has a door pointing left?
                        if (roomRight.hasDoor(Room.DoorPosition.W)) {
                            thisRoom.setDoor(Room.DoorPosition.E, roomRight.getDoor(Room.DoorPosition.W));
                        } else {
                            if (NumberUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(Room.DoorPosition.E, doorOffset);
                                roomRight.setDoor(Room.DoorPosition.W, doorOffset);
                            }
                        }
                    }

                }
            }
        }


        //Now paint the interior of the rooms
        for (int cy=0; cy<cavitiesY; cy++) {
            for (int cx=0; cx<cavitiesX; cx++) {
                rooms[cy][cx].createInterior();
            }
        }
    }

}
