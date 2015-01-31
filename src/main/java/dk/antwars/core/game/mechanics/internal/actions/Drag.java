package dk.antwars.core.game.mechanics.internal.actions;

import dk.antwars.core.game.mechanics.external.Direction;

public class Drag implements Action {

    private final Direction direction;

    public Drag(final Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

}
