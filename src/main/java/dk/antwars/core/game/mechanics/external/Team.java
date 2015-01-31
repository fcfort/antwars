package dk.antwars.core.game.mechanics.external;

import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.mechanics.internal.GameTeam;

public class Team {

    private final GameTeam gameTeam;

    public Team(final GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    public Color getColor() {
        return gameTeam.getColor();
    }

}
