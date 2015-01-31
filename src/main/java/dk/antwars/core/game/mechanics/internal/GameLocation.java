package dk.antwars.core.game.mechanics.internal;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.Game;
import dk.antwars.core.game.mechanics.external.Coordinates;
import dk.antwars.core.game.mechanics.external.FriendlyEntity;
import dk.antwars.core.game.mechanics.external.HostileEntity;
import dk.antwars.core.game.mechanics.external.Location;
import dk.antwars.core.game.mechanics.external.Pheromone;
import dk.antwars.core.game.mechanics.World;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GameLocation {

    private final Game game;
    private Coordinates coordinates;

    private final AtomicInteger foodAtomic = new AtomicInteger();

    private Set<GameEntity> gameEntities;
    private Set<Pheromone> pheromones;

    private Color scentColor;

    public GameLocation(final Game game, final Coordinates coordinates) {
        this.game = game;
        this.coordinates = coordinates;
    }

    public Location getLocation(final GameEntity gameEntity) {
        return new Location(this, gameEntity);
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public AtomicInteger getFoodAtomic() {
        return foodAtomic;
    }

    public Set<GameEntity> getGameEntities() {
        if (gameEntities == null) {
            gameEntities = Sets.newHashSet();
        }
        return gameEntities;
    }

    public Set<Pheromone> getPheromones() {
        if (pheromones != null) {
            final Iterator<Pheromone> pheromoneIterator = pheromones.iterator();
            while (pheromoneIterator.hasNext()) {
                final Pheromone pheromone = pheromoneIterator.next();
                if (pheromone.getIntensity(game.getTime()) < 1) {
                    pheromoneIterator.remove();
                }
            }
            if (pheromones.isEmpty()) {
                pheromones = null;
            }
        }
        if (pheromones != null && pheromones.size() > 0) {
            return Collections.unmodifiableSet(pheromones);
        }
        return null;
    }

    public Color getScentColor() {
        final Set<Pheromone> pheromones = getPheromones();
        if (pheromones != null && pheromones.size() > 0) {
            Color morphedScentColor = this.scentColor != null ? this.scentColor : Pheromone.Scent.Black.getColor();
            for (final Pheromone pheromone : pheromones) {
                enhanceColor(morphedScentColor, pheromone.getScentColor());
            }
            return morphedScentColor;
        }
        return this.scentColor;
    }

    public void setScentColor(Color scentColor) {
        this.scentColor = scentColor;
    }

    private void enhanceColor(dk.antwars.core.ant.presentation.Color locationColor, dk.antwars.core.ant.presentation.Color color) {
        locationColor.setRed(locationColor.getRed() | (color.getRed()));
        locationColor.setGreen(locationColor.getGreen() | (color.getGreen()));
        locationColor.setBlue(locationColor.getBlue() | (color.getBlue()));
    }

    public void addPheromone(final Pheromone pheromone) {
        if (pheromones == null) {
            pheromones = Sets.newHashSet();
        }
        pheromones.add(pheromone);
    }

    public Set<FriendlyEntity> getFriendlyEntities(final GameEntity gameEntity) {
        final Set<FriendlyEntity> friendlyEntities = Sets.newHashSet();
        for (final GameEntity iteratedGameEntity: gameEntities) {
            if (iteratedGameEntity.isFriendlyTowards(gameEntity)) {
                friendlyEntities.add(iteratedGameEntity.getFriendlyEntity());
            }
        }
        return Collections.unmodifiableSet(friendlyEntities);
    }

    public Set<HostileEntity> getHostileEntities(final GameEntity gameEntity) {
        final Set<HostileEntity> hostileEntities = Sets.newHashSet();
        for (final GameEntity iteratedGameEntity: gameEntities) {
            if (!iteratedGameEntity.isFriendlyTowards(gameEntity)) {
                hostileEntities.add(iteratedGameEntity.getHostileEntity());
            }
        }
        return Collections.unmodifiableSet(hostileEntities);
    }

    public Game getGame() {
        return game;
    }

    public World getWorld() {
        return game.getWorld();
    }

    @Override
    public String toString() {
        return coordinates.toString() + ": food=" + (foodAtomic != null ? foodAtomic.get() : 0) + ", entities=" + getGameEntities().size() + '}';
    }

}
