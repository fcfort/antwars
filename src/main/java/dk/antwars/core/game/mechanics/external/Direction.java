package dk.antwars.core.game.mechanics.external;

import com.google.common.collect.Sets;

import java.util.Set;

public class Direction {

    public static final byte CENTER     = 0x0;  //0000
    public static final byte SOUTH      = 0x1;  //0001
    public static final byte NORTH      = 0x2;  //0010
    public static final byte WEST       = 0x4;  //0100
    public static final byte SOUTHWEST  = 0x5;  //0101
    public static final byte NORTHWEST  = 0x6;  //0110
    public static final byte EAST       = 0x8;  //1000
    public static final byte SOUTHEAST  = 0x9;  //1001
    public static final byte NORTHEAST  = 0xA;  //1010

    public static final byte VERTICAL   = 0x3;  //0011
    public static final byte HORIZONTAL = 0xC;  //1100

    private final byte direction;

    public Direction(final byte direction) {
        this.direction = direction;
    }

    public byte getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        if (direction == CENTER) return "CENTER";
        if (direction == SOUTH) return "SOUTH";
        if (direction == NORTH) return "NORTH";
        if (direction == WEST) return "WEST";
        if (direction == SOUTHWEST) return "SOUTHWEST";
        if (direction == NORTHWEST) return "NORTHWEST";
        if (direction == EAST) return "EAST";
        if (direction == SOUTHEAST) return "SOUTHEAST";
        if (direction == NORTHEAST) return "NORTHEAST";
        return "UNKNOWN";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Direction d = (Direction) o;
        if (direction != d.direction) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) direction;
    }

    public Direction getOppositeDirection() {
        if ((direction & VERTICAL) == direction) {
            return new Direction((byte)(~direction & (VERTICAL)));
        } else
        if ((direction & HORIZONTAL) == direction) {
            return new Direction((byte)(~direction & (HORIZONTAL)));
        }
        if (direction != CENTER) {
            return new Direction((byte)(~direction & (HORIZONTAL | VERTICAL)));
        }
        return this;
    }

    public static final Set<Direction> getPossibleDirections() {
        return Sets.newHashSet(new Direction(WEST),new Direction(EAST),new Direction(NORTH),new Direction(SOUTH),new Direction(SOUTHEAST),new Direction(NORTHWEST),new Direction(NORTHEAST),new Direction(SOUTHWEST));
    }
    public Set<Direction> getAdjacentDirections() {
        final Set<Direction> adjacentDirections = Sets.newHashSet();
        if (direction == Direction.CENTER) {
            adjacentDirections.add(new Direction(WEST));
            adjacentDirections.add(new Direction(NORTH));
            adjacentDirections.add(new Direction(EAST));
            adjacentDirections.add(new Direction(SOUTH));
            adjacentDirections.add(new Direction(SOUTHEAST));
            adjacentDirections.add(new Direction(SOUTHWEST));
            adjacentDirections.add(new Direction(NORTHEAST));
            adjacentDirections.add(new Direction(NORTHWEST));
        } else if ((direction & VERTICAL) == direction) {
            adjacentDirections.add(new Direction((byte)(direction | WEST)));
            adjacentDirections.add(new Direction((byte)(direction | EAST)));
        } else if ((direction & HORIZONTAL) == direction) {
            adjacentDirections.add(new Direction((byte)(direction | NORTH)));
            adjacentDirections.add(new Direction((byte)(direction | SOUTH)));
        } else {
            adjacentDirections.add(new Direction((byte)(direction & VERTICAL)));
            adjacentDirections.add(new Direction((byte)(direction & HORIZONTAL)));
        }
        return adjacentDirections;
    }

    public boolean isValid() {
        if (direction == SOUTH) return true;
        if (direction == NORTH) return true;
        if (direction == WEST) return true;
        if (direction == SOUTHWEST) return true;
        if (direction == NORTHWEST) return true;
        if (direction == EAST) return true;
        if (direction == SOUTHEAST) return true;
        if (direction == NORTHEAST) return true;
        return false;
    }

//        SOUTH & VERTICAL = SOUTH
//        SOUTH | WEST = SOUTHWEST
//        SOUTH | EAST = SOUTHEAST
//        EAST & HORIZONTAL = EAST
//        EAST | NORTH = NORTHEAST
//        EAST | SOUTH = SOUTHEAST
//        SOUTHWEST & VERTICAL = WEST
//        SOUTHWEST & HORIZONTAL = SOUTH

    // DIRECTION THOUGHTS:

    // NORTH & WEST = 0010 | 0100 = 0110 = NORTHWEST
    // !NORTHWEST = !0110 = 1001 = SOUTHEAST

    // VISUALIZATION: x = enemy, o = you, # = escape
    //  _ x _
    //  x o _
    //  _ _ #




}
