package dk.antwars.core.game.mechanics.external;

import java.lang.*;

public class Coordinates {

    private final int longitude;
	private final int latitude;

	public Coordinates(final int longitude, final int latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
	}

    public final int getLongitude() {
        return longitude;
    }
	public final int getLatitude() {
		return latitude;
	}

    @Override
	public boolean equals(java.lang.Object object) {
		if (object instanceof Coordinates) {
			return this.latitude == ((Coordinates)object).getLatitude() && this.longitude == ((Coordinates)object).getLongitude();
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + latitude;
		result = prime * result + longitude;
		return result;
	}

	@Override
	public String toString() {
		return "(" + getLongitude() + "," + getLatitude() + ")";
	}

}
