package dk.antwars.core.game.mechanics.external;

import com.google.common.collect.Sets;
import dk.antwars.core.game.mechanics.internal.GameEntity;

import java.util.Map;
import java.util.Set;

public class Area {

    private final static Coordinates CENTER_COORDINATES = new Coordinates(0,0);

    private Map<Coordinates, Location> coordinatesLocationMap;

    public Area(final Map<Coordinates, Location> coordinatesLocationMap) {
        this.coordinatesLocationMap = coordinatesLocationMap;
    }

    public Location getCurrentLocation() {
        return coordinatesLocationMap.get(CENTER_COORDINATES);
    }
    public Location getLocationByCoordinates(final Coordinates coordinates) {
        return coordinatesLocationMap.get(coordinates);
    }

    private boolean isAreaInitialized = false;

    private void initialize() {
        for (final Location location: coordinatesLocationMap.values()) {
            if (location.getFood() > 0) {
                locationsWithFood.add(location);
            }
            if (location.hasEntities()) {
                locationsWithEntities.add(location);
                if (!location.getFriendlyEntities().isEmpty()) {
                    locationsWithFriendlies.add(location);
                }
                if (!location.getHostileEntities().isEmpty()) {
                    locationsWithHostiles.add(location);
                }
            }
            final Set<Pheromone> pheromones = location.getPheromones();
            if (pheromones != null && !pheromones.isEmpty()) {
                locationsWithPheromones.add(location);
            }
        }
    }

    private Set<Location> locationsWithFood = Sets.newHashSet();
    public Set<Location> getLocationsWithFood() {
        if (!isAreaInitialized) {
            initialize();
        }
        return locationsWithFood;
    }

    private Set<Location> locationsWithEntities = Sets.newHashSet();
    public Set<Location> getLocationsWithEntities() {
        if (!isAreaInitialized) {
            initialize();
        }
        return locationsWithEntities;
    }

    private Set<Location> locationsWithFriendlies = Sets.newHashSet();;
    public Set<Location> getLocationsWithFriendlies() {
        if (!isAreaInitialized) {
            initialize();
        }
        return locationsWithFriendlies;
    }

    private Set<Location> locationsWithHostiles = Sets.newHashSet();
    public Set<Location> getLocationsWithHostiles() {
        if (!isAreaInitialized) {
            initialize();
        }
        return locationsWithHostiles;
    }

    private Set<Location> locationsWithPheromones = Sets.newHashSet();
    public Set<Location> getLocationsWithPheromones() {
        if (!isAreaInitialized) {
            initialize();
        }
        return locationsWithPheromones;
    }

}