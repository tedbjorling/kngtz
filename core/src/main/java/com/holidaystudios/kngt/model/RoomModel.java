package com.holidaystudios.kngt.model;

import com.badlogic.gdx.math.Rectangle;
import com.holidaystudios.kngt.Coordinate;
import com.holidaystudios.kngt.TileTypes;
import com.holidaystudios.kngt.networking.GamePacketProvider;
import com.holidaystudios.kngt.networking.GameServer;
import com.holidaystudios.kngt.tools.RandomUtils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tedbjorling on 2014-02-20.
 */
public class RoomModel {

    public class DoorData {
        public int x, y, offset;
    }

    static public class NewRoomException extends Throwable {
        public Direction direction;

        public NewRoomException(Direction newDir) {
            direction = newDir;
        }
    }

    private final static Integer MIN_WALL_LENGTH = 3;
    private Integer posX, posY, pixelX, pixelY, pixelWidth, pixelHeight, tilesPerDistance;
    private byte[][] bitmap;
    private Map<Direction, DoorData> doors = new HashMap<Direction, DoorData>();

    public RoomModel(final Integer posX, final Integer posY, final Integer tilesPerDistance) {
        this.posX = posX;
        this.posY = posY;

        //Convenience variables
        this.pixelX = tilesPerDistance*this.posX;
        this.pixelY = tilesPerDistance*this.posY;
        this.pixelWidth  = tilesPerDistance;
        this.pixelHeight = tilesPerDistance;
        this.tilesPerDistance = tilesPerDistance;

        //Init the underlying room bitmap
        initBitmap();
    }


    private void initBitmap() {
        bitmap = new byte[this.pixelHeight][];
        for (int y=0; y<this.pixelHeight; y++) {
            bitmap[y] = new byte[this.pixelWidth];
            for (int x=0; x<this.pixelWidth; x++) {
                bitmap[y][x] = TileTypes.TILE_NONE;
            }
        }
    }

    public boolean hasAnyDoor() {
        return doors.containsKey(Direction.north)
                || doors.containsKey(Direction.south)
                || doors.containsKey(Direction.east)
                || doors.containsKey(Direction.west);
    }

    public boolean hasDoor(final Direction direction) {
        return doors.containsKey(direction);
    }

    public int getDoorOffset(final Direction direction) {
        return doors.get(direction).offset;
    }

    public int getDoorPositionX(final Direction direction) {
        return doors.get(direction).x;
    }

    public int getDoorPositionY(final Direction direction) {
        return doors.get(direction).y;
    }

    public void setDoorOffset(final Direction direction, final Integer offset) {
        DoorData dd = null;
        if(doors.containsKey(direction)) {
            dd = doors.get(direction);
            dd.offset = offset;
        } else {
            dd = new DoorData();
            dd.offset = offset;
            doors.put(direction, dd);
        }
    }

    public byte[][] getBitmap() {
        return this.bitmap;
    }

    private void applyWallInBitmap() {

        //Draw the wall
        for (int x=0; x<this.tilesPerDistance; x++) {
            for (int y=0; y<this.tilesPerDistance; y++) {

                //Is any of my neighbours a floor tile? If so, I am a wall
                if (this.bitmap[y][x] == TileTypes.TILE_FLOOR) {
                    continue;
                }

                int l = this.tilesPerDistance-1;
                if ((y>0 && x>0 && this.bitmap[y-1][x-1] == TileTypes.TILE_FLOOR)
                ||  (y>0 && 	   this.bitmap[y-1][x]   == TileTypes.TILE_FLOOR)
                ||  (y>0 && x<l && this.bitmap[y-1][x+1] == TileTypes.TILE_FLOOR)
                ||  (       x>0 && this.bitmap[y][x-1]   == TileTypes.TILE_FLOOR)
                ||  (       x<l && this.bitmap[y][x+1]   == TileTypes.TILE_FLOOR)
                ||  (y<l && x>0 && this.bitmap[y+1][x-1] == TileTypes.TILE_FLOOR)
                ||  (y<l && 	   this.bitmap[y+1][x]   == TileTypes.TILE_FLOOR)
                ||  (y<l && x<l && this.bitmap[y+1][x+1] == TileTypes.TILE_FLOOR)) {
                    this.bitmap[y][x] = TileTypes.TILE_WALL;
                }
            }
        }
    }

    private void connectDoors(final Map<Direction, Integer> twoDoors) {
        //Create a corridor
        //First, how wide should it be?
        final Integer corridorBreadth = Math.max(5, (int) Math.round(RandomUtils.getRandom()*7));

        if ((twoDoors.containsKey(Direction.north) || twoDoors.containsKey(Direction.south))
            && (twoDoors.containsKey(Direction.west) || twoDoors.containsKey(Direction.east))) {
            //Knee corridor

            //Simply enclose the room in an arbitrarily sized rectangle
            final Rectangle dim = new Rectangle(
                twoDoors.containsKey(Direction.north)? twoDoors.get(Direction.north) : twoDoors.get(Direction.south),
                twoDoors.containsKey(Direction.east)? twoDoors.get(Direction.east) : twoDoors.get(Direction.west),
                0,
                0
            );

            if (twoDoors.containsKey(Direction.east)) {
                dim.width = this.pixelWidth - dim.x;
            } else {
                dim.width = dim.x;
            }

            if (twoDoors.containsKey(Direction.south)) {
                dim.height = this.pixelWidth - dim.y;
            } else {
                dim.height = dim.y;
            }

            dim.width += 3;
            dim.height += 3;

            dim.width = Math.min(this.pixelWidth, dim.width);
            dim.height = Math.min(this.pixelHeight, dim.height);

            //Nudge
            if (twoDoors.containsKey(Direction.east)) {
                dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, dim.x));
            } else {
                dim.x = 0;
            }

            if (twoDoors.containsKey(Direction.south)) {
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
                        this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                    }
                }
            }
        } else if (twoDoors.containsKey(Direction.north) && twoDoors.containsKey(Direction.south)) {
            //Vertical corridor

            int nX = Math.max(1, Math.min(this.pixelWidth-corridorBreadth-1, Math.round(twoDoors.get(Direction.north) - corridorBreadth/2)));
            int sX = Math.max(1, Math.min(this.pixelWidth-corridorBreadth-1, Math.round(twoDoors.get(Direction.south) - corridorBreadth/2)));

            if (nX == sX) {
                //Straight corridor
                for (int y=0; y<this.tilesPerDistance; y++) {
                    for (int x=nX; x<nX+corridorBreadth; x++) {
                        if (y == 0 || y == this.tilesPerDistance-1) {
                            //room.bitmap[y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                        }
                    }
                }
            } else {

                int kneePos = (int) Math.round(this.tilesPerDistance/2);

                //First paint the straight corridors
                for (int y=0; y<this.pixelHeight; y++) {
                    int x;
                    if (y<kneePos) {
                        x = nX;
                    } else {
                        x = sX;
                    }
                    for (int _x=x; _x<x+corridorBreadth; _x++) {
                        if (y == 0 || y == this.tilesPerDistance-1) {
                            //room.bitmap[y][_x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][_x] = TileTypes.TILE_FLOOR;
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
                        this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                    }
                }
            }


        } else if (twoDoors.containsKey(Direction.east) && twoDoors.containsKey(Direction.west)) {
            //Horizontal corridor

            int wY = Math.max(1, Math.min(this.pixelHeight-corridorBreadth-1, Math.round(twoDoors.get(Direction.west) - corridorBreadth/2)));
            int eY = Math.max(1, Math.min(this.pixelHeight-corridorBreadth-1, Math.round(twoDoors.get(Direction.east) - corridorBreadth/2)));

            if (wY == eY) {
                //Straight corridor
                for (int x=0; x<this.tilesPerDistance; x++) {
                    for (int y=wY; y<wY+corridorBreadth; y++) {
                        if (x == 0 || x == this.tilesPerDistance-1) {
                            //room.bitmap[y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                        }
                    }
                }
            } else {

                int kneePos = Math.round(this.tilesPerDistance/2);

                //First paint the straight corridors
                for (int x=0; x<this.pixelWidth; x++) {
                    int y;
                    if (x<kneePos) {
                        y = wY;
                    } else {
                        y = eY;
                    }
                    for (int _y=y; _y<y+corridorBreadth; _y++) {
                        if (x == 0 || x == this.tilesPerDistance-1) {
                            //room.bitmap[_y][x] = defs.tiles.types.wall;
                        } else {
                            this.bitmap[_y][x] = TileTypes.TILE_FLOOR;
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
                        this.bitmap[y][x] = TileTypes.TILE_FLOOR;
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

        final Map.Entry<Direction, DoorData>[] availableDoors = (Map.Entry<Direction, DoorData>[]) this.doors.entrySet().toArray(new Map.Entry[this.doors.size()]);

        switch (availableDoors.length) {
            case 1:
                final Rectangle dim = new Rectangle(
                    0,
                    0,
                    Math.round(MIN_WALL_LENGTH + RandomUtils.getRandom() * (this.tilesPerDistance - MIN_WALL_LENGTH)),
                    Math.round(MIN_WALL_LENGTH + RandomUtils.getRandom() * (this.tilesPerDistance - MIN_WALL_LENGTH))
                );
                switch (availableDoors[0].getKey()) {
                    case south:
                        dim.y = this.pixelHeight - dim.height;
                        dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, Math.round(availableDoors[0].getValue().offset - dim.width/2)));
                        break;
                    case north:
                        dim.y = 0;
                        dim.x = Math.max(0, Math.min(this.pixelWidth-dim.width, Math.round(availableDoors[0].getValue().offset - dim.width/2)));
                        break;
                    case west:
                        dim.y = Math.max(0, Math.min(this.pixelHeight - dim.height, Math.round(availableDoors[0].getValue().offset - dim.height / 2)));
                        dim.x = 0;
                        break;
                    case east:
                        dim.y = Math.max(0, Math.min(this.pixelHeight-dim.height, Math.round(availableDoors[0].getValue().offset - dim.height/2)));
                        dim.x = this.pixelWidth - dim.width;
                        break;
                }
                //Draw room
                for (int y=(int)dim.y; y<dim.y+dim.height; y++) {
                    for (int x=(int)dim.x; x<dim.x+dim.width; x++) {
                        if (x == dim.x || x == dim.x+dim.width-1
                        ||  y == dim.y || y == dim.y+dim.height-1) {
                            this.bitmap[y][x] = TileTypes.TILE_WALL;
                        } else {
                            this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                        }
                    }
                }
                break;


            case 2: // we can reuse the code for case 3
            case 3:
                Map<Direction, Integer> twoDoors = new HashMap<Direction, Integer>();
                if (this.hasDoor(Direction.north)) {
                    if (this.hasDoor(Direction.east)) {
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.south)) {
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.west)) {
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        this.connectDoors(twoDoors);
                    }
                } else if (this.hasDoor(Direction.south)) {
                    if (this.hasDoor(Direction.east)) {
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.north)) {
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.west)) {
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        this.connectDoors(twoDoors);
                    }
                } else if (this.hasDoor(Direction.west)) {
                    if (this.hasDoor(Direction.east)) {
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.north)) {
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.south)) {
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        this.connectDoors(twoDoors);
                    }
                } else if (this.hasDoor(Direction.east)) {
                    if (this.hasDoor(Direction.west)) {
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        twoDoors.put(Direction.west, doors.get(Direction.west).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.north)) {
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        twoDoors.put(Direction.north, doors.get(Direction.north).offset);
                        this.connectDoors(twoDoors);
                    }
                    if (this.hasDoor(Direction.south)) {
                        twoDoors.put(Direction.east, doors.get(Direction.east).offset);
                        twoDoors.put(Direction.south, doors.get(Direction.south).offset);
                        this.connectDoors(twoDoors);
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
                            this.bitmap[y][x] = TileTypes.TILE_WALL;
                        } else {
                            this.bitmap[y][x] = TileTypes.TILE_FLOOR;
                        }
                    }
                }
                break;
        }

        //Apply doors to local bitmap
        if (this.hasDoor(Direction.south)) {
            DoorData dd = doors.get(Direction.south);
            dd.x = dd.offset;
            dd.y = pixelHeight - 1;
            this.bitmap[dd.y][dd.x] = TileTypes.TILE_DOOR;
        }

        if (this.hasDoor(Direction.north)) {
            DoorData dd = doors.get(Direction.north);
            dd.x = dd.offset;
            dd.y = 0;
            this.bitmap[dd.y][dd.x] = TileTypes.TILE_DOOR;
        }

        if (this.hasDoor(Direction.west)) {
            DoorData dd = doors.get(Direction.west);
            dd.x = 0;
            dd.y = dd.offset;
            this.bitmap[dd.y][dd.x] = TileTypes.TILE_DOOR;
        }

        if (this.hasDoor(Direction.east)) {
            DoorData dd = doors.get(Direction.east);
            dd.x = pixelWidth - 1;
            dd.y = dd.offset;
            this.bitmap[dd.y][dd.x] = TileTypes.TILE_DOOR;
        }

    }

    public static void publishRoomBitmap(byte[][] map, DatagramSocket serverSocket, InetAddress IPAddress, int port) throws IOException, BufferOverflowException {
        GamePacketProvider packetProvider = GameServer.getInstance().getPacketProvider();
        ByteBuffer bb = packetProvider.getSendBuffer();

        bb.put(GameServer.SR_PACKET_ROOM_MAP);
        bb.put((byte)map.length);
        bb.put((byte)map[0].length);

        int x, y;
        for(x = 0; x < map.length; x++) {
            byte[] row = map[x];
            for(y = 0; y < row.length; y++) {
                bb.put(row[y]);
            }
        }
        packetProvider.send(serverSocket, IPAddress, port);
    }

    public static byte[][] consumePublishedRoomBitmap(ByteBuffer bb) {
        byte sizeX = bb.get();
        byte sizeY = bb.get();

        byte[][] newMap = new byte[sizeX][];
        int x, y;

        for(x = 0; x < sizeX; x++) {
            byte[] row = new byte[sizeY];
            newMap[x] = row;
            for(y = 0; y < sizeY; y++) {
                row[y] = bb.get();
            }
        }

        return newMap;
    }
}
