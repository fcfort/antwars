//package dk.antwars.core.util;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Map;
//import java.util.Properties;
//
//import org.junit.Test;
//
//import dk.antwars.core.game.GameConfiguration;
//import dk.antwars.core.game.GameSettings;
//import dk.antwars.core.game.mechanics.external.Coordinates;
//import dk.antwars.core.game.mechanics.World;
//
//public class WorldUtilsTest {
//
//	@Test
//	public void testCreateWorld() {
//
//		final Properties settingsProperties = new Properties();
//		settingsProperties.setProperty(GameSettings.WORLD_SIZE, Integer.toString(4000));
//		settingsProperties.setProperty(GameSettings.WORLD_SIZE_DEVIATION, Integer.toString(33));
//
//		final GameConfiguration gameConfiguration = new GameConfiguration();
//		gameConfiguration.initializeFromProperties(settingsProperties);
//
//		final World world = WorldUtils.createWorldDefinition(gameConfiguration);
//		final int worldSize = world.getWorldSize();
//
//		int maxWorldSize = gameConfiguration.getWorldSize() * (100 + gameConfiguration.getWorldSizeDeviation()) / 100;
//		double maxWorldSizeDouble = Math.ceil(Math.sqrt(maxWorldSize));
//		maxWorldSize = (int) Math.pow(maxWorldSizeDouble, 2);
//
//		int minWorldSize = gameConfiguration.getWorldSize() * (100 - gameConfiguration.getWorldSizeDeviation()) / 100;
//		double minWorldSizeDouble = Math.floor(Math.sqrt(minWorldSize));
//		minWorldSize = (int) Math.pow(minWorldSizeDouble, 2);
//
//		assertTrue(maxWorldSize >= worldSize);
//		assertTrue(minWorldSize <= worldSize);
//
//
//	}
//
//}
