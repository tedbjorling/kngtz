package com.holidaystudios.kngt.model;

import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.tools.RandomUtils;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class CaveModel {

    private String seed;
    private Integer roomsX;
    private Integer roomsY;
    private Integer tilesPerDistance;
    private RoomModel[][] rooms;

    public CaveModel(final String seed, final Integer roomsX, final Integer roomsY, final Integer tilesPerDistance) {
        RandomUtils.setSeed(seed);
        this.setRoomsX(roomsX);
        this.setRoomsY(roomsY);
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

                    if (p == TileTypes.TILE_FLOOR) {
                        System.out.print('.');
                    } else if (p == TileTypes.TILE_WALL) {
                        System.out.print('#');
                    } else if (p == TileTypes.TILE_DOOR) {
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
        rooms = new RoomModel[this.getRoomsY()][];
        for (int cy=0; cy< this.getRoomsY(); cy++) {
            rooms[cy] = new RoomModel[this.getRoomsX()];
            for (int cx=0; cx< this.getRoomsX(); cx++) {
                rooms[cy][cx] = new RoomModel(cx, cy, this.tilesPerDistance);
            }
        }

        //Create doors
        for (int cy=0; cy< this.getRoomsY(); cy++) {
            for (int cx=0; cx< this.getRoomsX(); cx++) {
                final RoomModel thisRoom = rooms[cy][cx];

                //Make sure that each cavity has at least one door
                while (!thisRoom.hasAnyDoor()) {

                    //South door
                    if (cy<(this.getRoomsY() -1)) {
                        final RoomModel roomBelow = rooms[cy+1][cx];

                        //Does the room below has a door pointing upwards?
                        if (roomBelow.hasDoor(RoomModel.DoorPosition.N)) {
                            thisRoom.setDoor(RoomModel.DoorPosition.S, roomBelow.getDoor(RoomModel.DoorPosition.N));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(RoomModel.DoorPosition.S, doorOffset);
                                roomBelow.setDoor(RoomModel.DoorPosition.N, doorOffset);
                            }
                        }
                    }

                    //North door
                    if (cy>0) {
                        final RoomModel roomAbove = rooms[cy-1][cx];

                        //Does the room above has a door pointing downwards?
                        if (roomAbove.hasDoor(RoomModel.DoorPosition.S)) {
                            thisRoom.setDoor(RoomModel.DoorPosition.N, roomAbove.getDoor(RoomModel.DoorPosition.S));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(RoomModel.DoorPosition.N, doorOffset);
                                roomAbove.setDoor(RoomModel.DoorPosition.S, doorOffset);
                            }
                        }
                    }

                    //West door
                    if (cx>0) {
                        final RoomModel roomLeft = rooms[cy][cx-1];

                        //Does the room to the left has a door pointing right?
                        if (roomLeft.hasDoor(RoomModel.DoorPosition.E)) {
                            thisRoom.setDoor(RoomModel.DoorPosition.W, roomLeft.getDoor(RoomModel.DoorPosition.E));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(RoomModel.DoorPosition.W, doorOffset);
                                roomLeft.setDoor(RoomModel.DoorPosition.E, doorOffset);
                            }
                        }
                    }

                    //East door
                    if (cx<(this.getRoomsX() -1)) {
                        final RoomModel roomRight = rooms[cy][cx+1];

                        //Does the room to the right has a door pointing left?
                        if (roomRight.hasDoor(RoomModel.DoorPosition.W)) {
                            thisRoom.setDoor(RoomModel.DoorPosition.E, roomRight.getDoor(RoomModel.DoorPosition.W));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoor(RoomModel.DoorPosition.E, doorOffset);
                                roomRight.setDoor(RoomModel.DoorPosition.W, doorOffset);
                            }
                        }
                    }

                }
            }
        }


        //Now paint the interior of the rooms
        for (int cy=0; cy< this.getRoomsY(); cy++) {
            for (int cx=0; cx< this.getRoomsX(); cx++) {
                rooms[cy][cx].createInterior();
            }
        }
    }

    public Integer getRoomsX() {
        return roomsX;
    }

    public void setRoomsX(Integer roomsX) {
        this.roomsX = roomsX;
    }

    public Integer getRoomsY() {
        return roomsY;
    }

    public void setRoomsY(Integer roomsY) {
        this.roomsY = roomsY;
    }
}
