package dk.antwars.core.game.mechanics;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dk.antwars.core.game.Game;
import dk.antwars.core.game.mechanics.internal.GameLocation;
import dk.antwars.core.game.mechanics.external.Location;
import dk.antwars.core.game.mechanics.external.Coordinates;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import dk.antwars.core.util.CoordinatesUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class World {

    private final Game game;
	private final int numberOfLongitudes;
	private final int numberOfLatitudes;
    private final Map<Coordinates, GameLocation> coordinatesGameLocationMap;

    public World(final Game game, final int numberOfWorldLongitudes, final int numberOfWorldLatitudes) {
        this.game = game;
		this.numberOfLongitudes = numberOfWorldLongitudes;
		this.numberOfLatitudes = numberOfWorldLatitudes;
        this.coordinatesGameLocationMap = Maps.newHashMapWithExpectedSize(getWorldSize());
	}
	
	public int getNumberOfWorldLongitudes() {
		return numberOfLongitudes;
	}
	public int getNumberOfWorldLatitudes() {
		return numberOfLatitudes;
	}

	public int getWorldSize() {
		return numberOfLongitudes * numberOfLatitudes;
	}

    public Map<Coordinates, Location> getVisibleLocations(final GameEntity gameEntity) {
        final Map<Coordinates, Location> visibleLocations = Maps.newHashMap();
        final Set<Coordinates> visibleCoordinates = getVisibleCoordinates(gameEntity.getGameLocation().getCoordinates(), gameEntity.getSpecies().getSight());
        for (final Coordinates coordinates: visibleCoordinates) {
            visibleLocations.put(coordinates, getGameLocation(coordinates).getLocation(gameEntity));
        }
        return visibleLocations;
    }

    private Set<Coordinates> getVisibleCoordinates(final Coordinates centerCoordinates, final int sightDistance) {
        final Set<Coordinates> visibleCoordinates = Sets.newHashSet();
        for (int x = -sightDistance; x < sightDistance + 1; x++) {
            for (int y = -sightDistance; y < sightDistance + 1; y++) {
                final int longitude = CoordinatesUtils.correctCoordinate(centerCoordinates.getLongitude() + x, getNumberOfWorldLongitudes());
                final int latitude = CoordinatesUtils.correctCoordinate(centerCoordinates.getLatitude() + y, getNumberOfWorldLatitudes());
                visibleCoordinates.add(new Coordinates(longitude, latitude));
            }
        }
        return visibleCoordinates;
    }

    private Map<Coordinates, GameLocation> getCoordinatesGameLocationMap() {
        return coordinatesGameLocationMap;
    }

    public Game getGame() {
        return game;
    }

    public GameLocation getGameLocation(Coordinates coordinate) {
        if (getCoordinatesGameLocationMap().containsKey(coordinate)) {
            return getCoordinatesGameLocationMap().get(coordinate);
        } else {
            final GameLocation gameLocation = new GameLocation(game, coordinate);
            getCoordinatesGameLocationMap().put(coordinate, gameLocation);
            return gameLocation;
        }
    }

    public Collection<GameLocation> getGameLocations() {
        return getCoordinatesGameLocationMap().values();
    }
}
