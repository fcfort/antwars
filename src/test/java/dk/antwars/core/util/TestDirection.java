package dk.antwars.core.util;

import dk.antwars.core.game.mechanics.external.Direction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TestDirection {

    @Test
    public void testAdjacentDirectionsNorth() {
        final Direction direction = new Direction(Direction.NORTH);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTHEAST)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTHWEST)));
    }

    @Test
    public void testAdjacentDirectionsWest() {
        final Direction direction = new Direction(Direction.WEST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTHWEST)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTHWEST)));
    }

    @Test
    public void testAdjacentDirectionsSouth() {
        final Direction direction = new Direction(Direction.SOUTH);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTHEAST)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTHWEST)));
    }

    @Test
    public void testAdjacentDirectionsEast() {
        final Direction direction = new Direction(Direction.EAST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTHEAST)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTHEAST)));
    }

    @Test
    public void testAdjacentDirectionsNorthEast() {
        final Direction direction = new Direction(Direction.NORTHEAST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTH)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.EAST)));
    }

    @Test
    public void testAdjacentDirectionsNorthWest() {
        final Direction direction = new Direction(Direction.NORTHWEST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.NORTH)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.WEST)));
    }

    @Test
    public void testAdjacentDirectionsSouthEast() {
        final Direction direction = new Direction(Direction.SOUTHEAST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTH)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.EAST)));
    }

    @Test
    public void testAdjacentDirectionsSouthWest() {
        final Direction direction = new Direction(Direction.SOUTHWEST);
        final Set<Direction> adjacentDirections = direction.getAdjacentDirections();
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.SOUTH)));
        Assert.assertTrue(adjacentDirections.contains(new Direction(Direction.WEST)));
    }

    @Test
    public void testOppositeDirectionNorth() {
        final Direction direction = new Direction(Direction.NORTHEAST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.SOUTHWEST)));
    }

    @Test
    public void testOppositeDirectionNorthEast() {
        final Direction direction = new Direction(Direction.NORTHEAST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.SOUTHWEST)));
    }

    @Test
    public void testOppositeDirectionEast() {
        final Direction direction = new Direction(Direction.EAST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.WEST)));
    }

    @Test
    public void testOppositeDirectionSouthEast() {
        final Direction direction = new Direction(Direction.SOUTHEAST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.NORTHWEST)));
    }

    @Test
    public void testOppositeDirectionSouth() {
        final Direction direction = new Direction(Direction.SOUTH);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.NORTH)));
    }

    @Test
    public void testOppositeDirectionSouthWest() {
        final Direction direction = new Direction(Direction.SOUTHWEST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.NORTHEAST)));
    }

    @Test
    public void testOppositeDirectionWest() {
        final Direction direction = new Direction(Direction.WEST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.EAST)));
    }

    @Test
    public void testOppositeDirectionNorthWest() {
        final Direction direction = new Direction(Direction.NORTHWEST);
        final Direction oppositeDirection = direction.getOppositeDirection();
        Assert.assertTrue(oppositeDirection.equals(new Direction(Direction.SOUTHEAST)));
    }

}
