package com.holidaystudios.kngt.model;

import com.holidaystudios.kngt.Defs;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.tools.RandomUtils;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class CaveModel {

    private String seed;
    private Integer roomsX;
    private Integer roomsY;
    private RoomModel[][] rooms;

    public CaveModel(final String seed, final Integer roomsX, final Integer roomsY) {
        RandomUtils.setSeed(seed);
        this.setRoomsX(roomsX);
        this.setRoomsY(roomsY);

        //Start mapping out the individual rooms
        this.createRooms();
    }

    public byte[][] getRoomBitmap(final Integer cx, final Integer cy) {
        return rooms[cy][cx].getBitmap();
    }

    public int getRoomDoorXPosition(final Integer cx, final Integer cy, Direction direction) {
        return rooms[cy][cx].getDoorPositionX(direction);
    }

    public int getRoomDoorYPosition(final Integer cx, final Integer cy, Direction direction) {
        return rooms[cy][cx].getDoorPositionY(direction);
    }

    public byte[][] getBitmap() {

        final byte[][] caveBitmap = new byte[Defs.TILES_PER_DISTANCE*rooms.length][Defs.TILES_PER_DISTANCE*rooms.length];

        //Fill the main bitmap
        for (int cy=0; cy<rooms.length; cy++) {
            for (int cx=0; cx<rooms[cy].length; cx++) {
                final byte[][] bitmap = rooms[cy][cx].getBitmap();
                final Integer offsetX = cx*Defs.TILES_PER_DISTANCE;
                final Integer offsetY = cy*Defs.TILES_PER_DISTANCE;

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

    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder();

        final byte[][] caveBitmap = this.getBitmap();

        for (int y=0; y<caveBitmap.length; y++) {
            for (int x=0; x<caveBitmap[y].length; x++) {
                final byte p = caveBitmap[y][x];

                if (p == 2000) {
                    sb.append(',');
                } else if (p >= 1000) {
                    sb.append(Integer.toString(p - 1000));
                } else {

                    if (p == TileTypes.TILE_FLOOR) {
                        sb.append('.');
                    } else if (p == TileTypes.TILE_WALL) {
                        sb.append('#');
                    } else if (p == TileTypes.TILE_DOOR) {
                        sb.append('O');
                    } else {
                        sb.append(' ');
                    }
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    private Integer createDoorPositionHelper() {
        return 2 + (int) Math.round(RandomUtils.getRandom() * (Defs.TILES_PER_DISTANCE-4));
    }

    private void createRooms() {

        //Create the rooms
        rooms = new RoomModel[this.getRoomsY()][];
        for (int cy=0; cy< this.getRoomsY(); cy++) {
            rooms[cy] = new RoomModel[this.getRoomsX()];
            for (int cx=0; cx< this.getRoomsX(); cx++) {
                rooms[cy][cx] = new RoomModel(cx, cy, Defs.TILES_PER_DISTANCE);
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
                        if (roomBelow.hasDoor(Direction.north)) {
                            thisRoom.setDoorOffset(Direction.south, roomBelow.getDoorOffset(Direction.north));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoorOffset(Direction.south, doorOffset);
                                roomBelow.setDoorOffset(Direction.north, doorOffset);
                            }
                        }
                    }

                    //North door
                    if (cy>0) {
                        final RoomModel roomAbove = rooms[cy-1][cx];

                        //Does the room above has a door pointing downwards?
                        if (roomAbove.hasDoor(Direction.south)) {
                            thisRoom.setDoorOffset(Direction.north, roomAbove.getDoorOffset(Direction.south));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoorOffset(Direction.north, doorOffset);
                                roomAbove.setDoorOffset(Direction.south, doorOffset);
                            }
                        }
                    }

                    //West door
                    if (cx>0) {
                        final RoomModel roomLeft = rooms[cy][cx-1];

                        //Does the room to the left has a door pointing right?
                        if (roomLeft.hasDoor(Direction.east)) {
                            thisRoom.setDoorOffset(Direction.west, roomLeft.getDoorOffset(Direction.east));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoorOffset(Direction.west, doorOffset);
                                roomLeft.setDoorOffset(Direction.east, doorOffset);
                            }
                        }
                    }

                    //East door
                    if (cx<(this.getRoomsX() -1)) {
                        final RoomModel roomRight = rooms[cy][cx+1];

                        //Does the room to the right has a door pointing left?
                        if (roomRight.hasDoor(Direction.west)) {
                            thisRoom.setDoorOffset(Direction.east, roomRight.getDoorOffset(Direction.west));
                        } else {
                            if (RandomUtils.getRandom() > 0.5) {
                                final Integer doorOffset = createDoorPositionHelper();
                                thisRoom.setDoorOffset(Direction.east, doorOffset);
                                roomRight.setDoorOffset(Direction.west, doorOffset);
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
