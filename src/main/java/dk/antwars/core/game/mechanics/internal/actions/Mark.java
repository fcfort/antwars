package dk.antwars.core.game.mechanics.internal.actions;

import dk.antwars.core.game.mechanics.external.Pheromone;

public class Mark implements Action {

    private final Pheromone.Scent scent;

    public Mark(Pheromone.Scent scent) {
        this.scent = scent;
    }

    public Pheromone.Scent getScent() {
        return scent;
    }
}
