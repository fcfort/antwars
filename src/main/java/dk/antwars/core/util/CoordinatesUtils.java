package dk.antwars.core.util;

import dk.antwars.core.game.mechanics.external.Coordinates;
import dk.antwars.core.game.mechanics.World;
import dk.antwars.core.game.mechanics.external.Direction;

public class CoordinatesUtils {

    public static int calculateDistance(final Coordinates sourceCoordinates, final Coordinates targetCoordinates, final World world) {
        final int latitudeDistance = calculateRelativeLatitudeCoordinate(targetCoordinates, sourceCoordinates, world);
        final int longitudeDistance = calculateRelativeLongitudeCoordinate(targetCoordinates, sourceCoordinates, world);
        return longitudeDistance + latitudeDistance;
    }

    public static Coordinates calculateRelativeCoordinates(Coordinates sourceCoordinates, Coordinates targetCoordinates, World world) {
        return new Coordinates(calculateRelativeLongitudeCoordinate(sourceCoordinates, targetCoordinates, world), calculateRelativeLatitudeCoordinate(sourceCoordinates, targetCoordinates, world));
    }

    public static int calculateRelativeLatitudeCoordinate(final Coordinates sourceCoordinates, final Coordinates targetCoordinates, final World world) {
        return calculateRelativeCoordinate(sourceCoordinates.getLatitude(), targetCoordinates.getLatitude(), world.getNumberOfWorldLatitudes());
    }

    public static int calculateRelativeLongitudeCoordinate(final Coordinates sourceCoordinates, final Coordinates targetCoordinates, final World world) {
        return calculateRelativeCoordinate(sourceCoordinates.getLongitude(), targetCoordinates.getLongitude(), world.getNumberOfWorldLongitudes());
    }

    public static int calculateRelativeCoordinate(int source, int target, int size) {
        return calculateRange(target, source, size);
    }

    public static int calculateRange(int target, int source, int size) {
        int range = target - source;
        if (Math.abs(range) > (size / 2)) {
            range = ((range > 0 ? -1 : 1) * (size)) + range;
        }
        return range;
    }

    public static Coordinates calculateCoordinates(final Coordinates positionCoordinates, final Direction direction, final World world) {

        final boolean north = (direction.getDirection() & Direction.NORTH) == Direction.NORTH;
        final boolean south = (direction.getDirection() & Direction.SOUTH) == Direction.SOUTH;
        final boolean west  = (direction.getDirection() & Direction.WEST) == Direction.WEST;
        final boolean east  = (direction.getDirection() & Direction.EAST) == Direction.EAST;

        int longitude = positionCoordinates.getLongitude(), latitude = positionCoordinates.getLatitude();

        if (east) longitude++; else if (west) longitude--;
        if (north) latitude++; else if (south) latitude--;

        if (longitude > world.getNumberOfWorldLongitudes()) {
            longitude = 0;
        } else if (longitude < 0) {
            longitude = world.getNumberOfWorldLongitudes();
        }
        if (latitude > world.getNumberOfWorldLatitudes()) {
            latitude = 0;
        } else if (latitude < 0) {
            latitude = world.getNumberOfWorldLatitudes();
        }
        return new Coordinates(longitude, latitude);
    }

    public static int correctCoordinate(int coordinate, int numberOfWorldCoordinates) {
        if (coordinate > numberOfWorldCoordinates) {
            return coordinate - numberOfWorldCoordinates;
        }
        if (coordinate < 0) {
            return numberOfWorldCoordinates + coordinate;
        }
        return coordinate;
    }
}
