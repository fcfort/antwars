package dk.antwars.demo;

import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.ant.Self;
import dk.antwars.core.game.mechanics.external.Area;
import dk.antwars.core.game.mechanics.internal.actions.Action;
import dk.antwars.core.game.mechanics.internal.actions.Move;
import dk.antwars.core.game.mechanics.external.Direction;

public class NorthAnt implements Ant {

    @Override
    public Action act(final Self self, Area visibleArea) {
        return new Move(new Direction(Direction.NORTH));
    }

    @Override
    public Color getPreferredColor() {
        return new Color(0,255,0);
    }

}
