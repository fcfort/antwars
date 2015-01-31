/*
package dk.antwars.core.util;

import com.google.common.collect.Sets;
import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import dk.antwars.core.game.mechanics.internal.GameTeam;
import dk.antwars.core.game.mechanics.internal.actions.Action;

import java.util.Set;

public class AntUtils {

	public final static char calculateRequiredActionPoints(Action action, Species species) {
		return 1;
	}

	public final static int calculateAttackDamage(Species attackingAntSpecies, Species defendingAntSpecies) {
		return 1;
	}

	public final static int calculateMaxCarryStrength(final Species species) {
        return species.getStrength();
	}

	public final static int calculateCost(Species species) {
        return species.getStrength() + species.getAgility() + species.getIntellect();
	}

    private static int increasingCost(int costPoints, int increasePerPoint) {
        int cost = 0;
        for (int i = 0; i < costPoints; i++) {
            cost += Math.pow(i, increasePerPoint);
        }
        return cost;
    }

    public final static int calculateFoodValue(Species species) {
        return calculateCost(species)/2;
    }

    public static Set<GameTeam> getTeams(final Set<GameEntity> entities) {
        final Set<GameTeam> teams = Sets.newHashSet();
        for (final GameEntity gameEntity: entities) {
            if (!teams.contains(gameEntity.getGameTeam())) {
                teams.add(gameEntity.getGameTeam());
            }
        }
        return teams;
    }
}
*/
