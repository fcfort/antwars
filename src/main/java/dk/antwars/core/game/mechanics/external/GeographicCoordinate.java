package dk.antwars.core.game.mechanics.external;

public class GeographicCoordinate {

    private int latitude = 0;
    private int longitude = 0;

    public GeographicCoordinate() {}
    public GeographicCoordinate(int longitude, int latitude) { this.longitude = longitude; this.latitude = latitude; }

    public int getLongitude() {
        return longitude;
    }
    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }
    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void move(Direction direction) {
        final boolean north = (direction.getDirection() & Direction.NORTH) == Direction.NORTH;
        final boolean south = (direction.getDirection() & Direction.SOUTH) == Direction.SOUTH;
        final boolean west  = (direction.getDirection() & Direction.WEST) == Direction.WEST;
        final boolean east  = (direction.getDirection() & Direction.EAST) == Direction.EAST;
        if (east) longitude++; else if (west) longitude--;
        if (north) latitude++; else if (south) latitude--;
    }

    public boolean equals(Coordinates o) {
        if (latitude != o.getLatitude()) return false;
        if (longitude != o.getLongitude()) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeographicCoordinate that = (GeographicCoordinate) o;

        if (latitude != that.latitude) return false;
        if (longitude != that.longitude) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = latitude;
        result = 31 * result + longitude;
        return result;
    }
}
