package dk.antwars.core.game;

import com.google.common.collect.Lists;
import dk.antwars.core.ant.Ant;
import dk.antwars.core.classloading.AntClassLoader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameLoader {

    private static Logger logger = LoggerFactory.getLogger(GameLoader.class);

    @SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			logger.info("Usage: GameLoader <settings file>");
		}
		final String settingsFileString = args[0];
		final File settingsFile = new File(settingsFileString);
		final Properties settingsProperties = new Properties();
		try {
			final FileInputStream fileInputStream = new FileInputStream(settingsFile);
			settingsProperties.load(fileInputStream);
		} catch (FileNotFoundException e) {
            logger.error("Settings file: " + settingsFileString + " could not be found");
		} catch (IOException e) {
            logger.error("Settings file: " + settingsFileString + " could not be read");
		}
		
		final GameConfiguration gameConfiguration = new GameConfiguration();
		gameConfiguration.initializeFromProperties(settingsProperties);

		if (StringUtils.isNotBlank(gameConfiguration.getRandomSeed())) {
			System.setProperty("randomSeed", gameConfiguration.getRandomSeed());
		}

		final String racesDirectoryString = settingsProperties.getProperty(GameSettings.RACES_DIRECTORY);
		final File racesDirectory = new File(racesDirectoryString);

		final String antClassesNamesProperty = settingsProperties.getProperty(GameSettings.ANT_CLASSES);
		final List<String> antClassNames = extractAntClassNames(antClassesNamesProperty);

		if (racesDirectory.exists() && racesDirectory.isDirectory()) {
			gameConfiguration.setAntRepository(loadAnts(racesDirectory, antClassNames));
		} else {
            logger.error(GameSettings.RACES_DIRECTORY + " does not point to a valid directory");
		}

        final Game game = new Game(gameConfiguration);
        game.startGame();

	}

	private static List<String> extractAntClassNames(String antClassesProperty) {
		final String[] antClassesArray = antClassesProperty.split("[, ]+");
		final List<String> antClasses = Lists.newArrayList();
		for (String antClass: antClassesArray) {
			if (StringUtils.isNotBlank(antClass)) {
				antClasses.add(StringUtils.trim(antClass));
			}
		}
		return antClasses;
	}

	@SuppressWarnings("rawtypes")
	private static List<Class<Ant>> loadAnts(File racesDirectory, List<String> antClassNames) {
		final List<Class<Ant>> antClasses = new ArrayList<Class<Ant>>();
		final AntClassLoader antClassLoader = new AntClassLoader(racesDirectory.getAbsolutePath());
		for (String antClassName: antClassNames) {
            final Class<Ant> antClass = antClassLoader.loadAntClass(antClassName, true);
            if (antClass != null) {
                antClasses.add(antClass);
            }
		}
		return antClasses;
	}
}