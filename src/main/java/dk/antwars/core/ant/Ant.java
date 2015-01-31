package dk.antwars.core.ant;

import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.mechanics.external.Area;
import dk.antwars.core.game.mechanics.internal.actions.Action;

public interface Ant {

    /**
	 * This method is called once each time increment
	 */
	public Action act(final Self self, final Area visibleArea);

    /**
     * Guess!
     * @return a color
     */
    public Color getPreferredColor();
}
