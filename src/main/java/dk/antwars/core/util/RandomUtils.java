package dk.antwars.core.util;

import java.util.Random;

import dk.antwars.core.game.mechanics.external.Direction;
import org.apache.commons.lang.StringUtils;

public class RandomUtils {

	private static Random random;
	
	public static Random getInstance() {
		if (random == null) {
			final String randomSeed = System.getProperty("randomSeed");
			if (StringUtils.isNotBlank(randomSeed)) {
				final Long randomSeedLong = Long.parseLong(randomSeed);
				random = new Random(randomSeedLong);
			} else {
				random = new Random(System.currentTimeMillis());
			}
		}
		return random;
	}

    public static Direction getRandomDirection(final Direction... directions) {
        return directions[RandomUtils.getInstance().nextInt(directions.length)];
    }

}

