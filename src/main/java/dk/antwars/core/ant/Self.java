package dk.antwars.core.ant;

import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.mechanics.external.Team;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import dk.antwars.core.game.mechanics.internal.actions.Action;

public class Self {

    private final GameEntity gameEntity;

    public Self(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }

    public Species getSpecies() {
        return gameEntity.getSpecies();
    }
    public Team getTeam() {
        return gameEntity.getGameTeam().getTeam();
    }
    public int[] getMemories() {
        return gameEntity.getMemory().getMemories();
    }
    public Action[] getActionMemories() {
        return gameEntity.getMemory().getActionMemories();
    }
    public int getDamage() {
        return gameEntity.getAccumulatedDamage();
    }


}
