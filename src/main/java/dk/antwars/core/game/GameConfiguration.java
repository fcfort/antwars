package dk.antwars.core.game;

import dk.antwars.core.ant.Ant;
import dk.antwars.core.util.RandomUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Properties;

public class GameConfiguration {
	
	private int numberOfTurns;
	private int foodSpread;
	private int foodMultiplier;
	private int winBoundary;
	private int halftimeWinBoundary;
	private int worldSize;
	private int worldSizeDeviation;

	private List<Class<Ant>> antRepository;

	private String randomSeed;

	public int getNumberOfTurns() {
		return numberOfTurns;
	}
	
	/**
	 * Determines how many areas are decorated with food across the world
	 * Minimum food spread is 10
	 * Maximum food spread is 50
	 */
	public int getFoodSpread() {
		if (foodSpread < 10) {
			return 10;
		}
		return foodSpread;
	}
	
	/**
	 * Determines how much food is generated as a result of time
	 * Minimum food multiplier is 10
	 * Maximum food multiplier is 200 
	 */
	public int getFoodMultiplier() {
		if (foodMultiplier < 5) {
			return 5;
		}
		return foodMultiplier;
	}
	
	public int getWinBoundary() {
		return winBoundary;
	}
	
	public int getHalftimeWinBoundary() {
		return halftimeWinBoundary;
	}
	
	private static int lowerWorldSizeLimit = (int) Math.pow(10, 1);
	private static int upperWorldSizeLimit = (int) Math.pow(10, 6);
	
	/**
	 * Minimum world size is 10^3
	 * Maximum world size is 10^6
	 */
	public int getWorldSize() {
		if (worldSize < lowerWorldSizeLimit) {
			return lowerWorldSizeLimit;
		}
		if (worldSize > upperWorldSizeLimit) {
			return upperWorldSizeLimit;
		}
		return worldSize;
	}
	
	/**
	 * Minimum world size deviation is 10% of world size
	 * Maximum world size deviation is 90% of world size
	 */
	public int getWorldSizeDeviation() {
		if (worldSizeDeviation < 10) {
			return 10;
		}
		if (worldSizeDeviation > 90) {
			return 90;
		}
		return worldSizeDeviation;
	}
	
	public List<Class<Ant>> getAntRepository() {
		return antRepository;
	}
	public void setAntRepository(List<Class<Ant>> antRepository) {
		this.antRepository = antRepository;
	}

	public void initializeFromProperties(Properties settingsProperties) {

		final String worldSizeProperty = settingsProperties.getProperty(GameSettings.WORLD_SIZE);
		if (StringUtils.isNotBlank(worldSizeProperty)) {
			this.worldSize = Integer.parseInt(worldSizeProperty);
		}
		final String worldSizeDeviationProperty = settingsProperties.getProperty(GameSettings.WORLD_SIZE_DEVIATION);
		if (StringUtils.isNotBlank(worldSizeDeviationProperty)) {
			this.worldSizeDeviation = Integer.parseInt(worldSizeDeviationProperty);
		}
		final String randomSeedProperty = settingsProperties.getProperty(GameSettings.RANDOM_SEED);
		if (StringUtils.isNotBlank(randomSeedProperty)) {
			this.randomSeed = randomSeedProperty;
		} else {
            this.randomSeed = Integer.toString(RandomUtils.getInstance().nextInt());
        }
        final String foodSpreadProperty = settingsProperties.getProperty(GameSettings.FOOD_SPREAD);
        if (StringUtils.isNotBlank(foodSpreadProperty)) {
            this.foodSpread = Integer.parseInt(foodSpreadProperty);
        }

        final String foodMultiplierProperty = settingsProperties.getProperty(GameSettings.FOOD_MULTIPLIER);
        if (StringUtils.isNotBlank(foodMultiplierProperty)) {
            this.foodMultiplier = Integer.parseInt(foodMultiplierProperty);
        }

    }

	public String getRandomSeed() {
		return randomSeed;
	}
	public void setRandomSeed(String randomSeed) {
		this.randomSeed = randomSeed;
	}
}
