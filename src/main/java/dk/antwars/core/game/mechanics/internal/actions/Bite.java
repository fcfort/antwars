package dk.antwars.core.game.mechanics.internal.actions;

import dk.antwars.core.game.mechanics.external.Entity;

public class Bite implements Action {

	private final Entity targetEntity;

    public Bite(final Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }
}
