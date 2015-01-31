package dk.antwars.core.game.mechanics.external;

import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import dk.antwars.core.game.mechanics.internal.GameLocation;
import dk.antwars.core.util.CoordinatesUtils;

import java.util.Set;

public class Location {

    private final GameEntity gameEntity;
    private final GameLocation gameLocation;

    public Location(GameLocation gameLocation, GameEntity gameEntity) {
        this.gameEntity = gameEntity;
        this.gameLocation = gameLocation;
    }

    public Coordinates getCoordinates() {
        return CoordinatesUtils.calculateRelativeCoordinates(gameEntity.getGameLocation().getCoordinates(), gameLocation.getCoordinates(), gameLocation.getWorld());
    }

    public int getFood() {
        return this.gameLocation.getFoodAtomic().get();
    }

    private Set<FriendlyEntity> friendlyEntities;
    public Set<FriendlyEntity> getFriendlyEntities() {
        if (friendlyEntities == null) {
            friendlyEntities = this.gameLocation.getFriendlyEntities(gameEntity);
        }
        return friendlyEntities;
    }

    private Set<HostileEntity> hostileEntities;
    public Set<HostileEntity> getHostileEntities() {
        if (hostileEntities == null) {
            hostileEntities = this.gameLocation.getHostileEntities(gameEntity);
        }
        return hostileEntities;
    }

    public boolean hasEntities() {
        return !gameLocation.getGameEntities().isEmpty();
    }

    public Set<Pheromone> getPheromones() {
        return this.gameLocation.getPheromones();
    }

    public Color getScent() {
        return this.gameLocation.getScentColor();
    }

}