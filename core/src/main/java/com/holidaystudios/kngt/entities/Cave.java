package com.holidaystudios.kngt.entities;

import com.holidaystudios.kngt.gameplay.GamePlayTiles;
import com.holidaystudios.kngt.tools.RandomUtils;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class Cave {

    private String seed;
    private Integer roomsX;
    private Integer roomsY;
    private Integer tilesPerDistance;
    private Room[][] rooms;

    public Cave(final String seed, final Integer roomsX, final Integer roomsY, final Integer tilesPerDistance) {
        RandomUtils.setSeed(seed);
        this.roomsX = roomsX;
        this.roomsY = roomsY;
        this.tilesPerDistance = tilesPerDistance;

        //Start mapping out the individual rooms
        this.createRooms();

        this.debugCave();
    }

    public Integer[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return rooms[cy][cx].getBitmap();
    }

    public Integer[][] getBitmap() {

        final Integer[][] caveBitmap = new Integer[this.tilesPerDistance*rooms.length][this.tilesPerDistance*rooms.length];

        //Fill the main bitmap
        for (int cy=0; cy<rooms.length; cy++) {
            for (int cx=0; cx<rooms[cy].length; cx++) {
                final Integer[][] bitmap = rooms[cy][cx].getBitmap();
                final Integer offsetX = cx*this.tilesPerDistance;
                final Integer offsetY = cy*this.tilesPerDistance;

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

                    if (p == GamePlayTiles.TILE_FLOOR) {
                        System.out.print('.');
                    } else if (p == GamePlayTiles.TILE_WALL) {
                        System.out.print('#');
                    } else if (p == GamePlayTiles.TILE_DOOR) {
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
        return 2 + (int) Math.round(RandomUtils.getRandom() * (this.tilesPerDistance-4));
    }

    private void createRooms() {

        //Create the rooms
        rooms = new Room[this.roomsY][];
        for (int cy=0; cy<this.roomsY; cy++) {
            rooms[cy] = new Room[this.roomsX];
            for (int cx=0; cx<this.roomsX; cx++) {
                rooms[cy][cx] = new Room(cx, cy, this.tilesPerDistance);
            }
        }

        //Create doors
        for (int cy=0; cy<this.roomsY; cy++) {
            for (int cx=0; cx<this.roomsX; cx++) {
                final Room thisRoom = rooms[cy][cx];

                //Make sure that each cavity has at least one door
                while (!thisRoom.hasAnyDoor()) {

                    //South door
                    if (cy<(this.roomsY-1)) {
                        final Room roomBelow = rooms[cy+1][cx];

                        //Does the room below has a door pointing upwards?
                        if (roomBelow.hasDoor(Room.DoorPosition.N)) {
                            thisRoom.setDoor(Room.DoorPosition.S, roomBelow.getDoor(Room.DoorPosition.N));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
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
                            if (RandomUtils.getRandom() > 0.5) {
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
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(Room.DoorPosition.W, doorOffset);
                                roomLeft.setDoor(Room.DoorPosition.E, doorOffset);
                            }
                        }
                    }

                    //East door
                    if (cx<(this.roomsX-1)) {
                        final Room roomRight = rooms[cy][cx+1];

                        //Does the room to the right has a door pointing left?
                        if (roomRight.hasDoor(Room.DoorPosition.W)) {
                            thisRoom.setDoor(Room.DoorPosition.E, roomRight.getDoor(Room.DoorPosition.W));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
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
        for (int cy=0; cy<this.roomsY; cy++) {
            for (int cx=0; cx<this.roomsX; cx++) {
                rooms[cy][cx].createInterior();
            }
        }
    }

}
