package dk.antwars.core.game.mechanics.internal.actions;

import dk.antwars.core.game.mechanics.external.Direction;

public class Move implements Action {

    private final Direction direction;

    public Move(final Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

}
