package dk.antwars.core.util;

import com.google.common.collect.Sets;
import dk.antwars.core.game.Game;
import dk.antwars.core.game.GameConfiguration;
import dk.antwars.core.game.mechanics.external.Coordinates;
import dk.antwars.core.game.mechanics.World;

import java.util.Random;
import java.util.Set;

public class WorldUtils {

	public static World createWorldDefinition(Game game, GameConfiguration gameConfiguration) {
		final boolean deviationIsPositive = RandomUtils.getInstance().nextBoolean();
		final float deviation = (float)(100 + (deviationIsPositive ? 1 : -1) * gameConfiguration.getWorldSizeDeviation()) / (float)100;
		final int worldSize = (int) (gameConfiguration.getWorldSize() * deviation);
		final int worldSizeLongitudes = worldSize;
		final int worldSizeLatitudes = worldSize;
		return new World(game, worldSizeLongitudes, worldSizeLatitudes);
	}

	public static Set<Integer> generateFood(GameConfiguration gameConfiguration) {
		final Set<Integer> generatedFood = Sets.newHashSet();
		int remainingFood = gameConfiguration.getAntRepository().size() * gameConfiguration.getFoodMultiplier();
		while (remainingFood > 0) {
			final Random random = RandomUtils.getInstance();
			int randomFoodSpread = random.nextInt(gameConfiguration.getFoodSpread());
			if (randomFoodSpread > remainingFood) {
				randomFoodSpread = remainingFood;
			}
			remainingFood -= randomFoodSpread;
			generatedFood.add(randomFoodSpread);
		}		
		return generatedFood;
	}

	public static char calculateRandomLongitude(World world) {
		final Random random = RandomUtils.getInstance();
		return (char) (random.nextInt(world.getNumberOfWorldLongitudes()) + 1);
	}

	public static char calculateRandomLatitude(World world) {
		final Random random = RandomUtils.getInstance();
		return (char) (random.nextInt(world.getNumberOfWorldLatitudes()) + 1);
	}

    public static Coordinates getRandomCoordinate(final World world) {
        final int randomLongitude = WorldUtils.calculateRandomLongitude(world);
        final int randomLatitude = WorldUtils.calculateRandomLatitude(world);
        return new Coordinates(randomLongitude, randomLatitude);
    }

}
