package dk.antwars.core.game.mechanics.internal.actions;

import dk.antwars.core.ant.species.Species;

public class Hatch implements Action {

    public final Species species;

    public Hatch(final Species species) {
        this.species = species;
    }

    public Species getSpecies() {
        return species;
    }

}
