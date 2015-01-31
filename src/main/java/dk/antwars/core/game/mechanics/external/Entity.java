package dk.antwars.core.game.mechanics.external;

import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.mechanics.WorldObject;
import dk.antwars.core.game.mechanics.internal.GameEntity;

public class Entity extends WorldObject {

    private final GameEntity gameEntity;

    public Entity(final GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }

    protected GameEntity getGameEntity() {
        return gameEntity;
    }

    public Species getSpecies() {
        return gameEntity.getSpecies();
    }

}
