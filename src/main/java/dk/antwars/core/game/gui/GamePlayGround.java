package dk.antwars.core.game.gui;

import com.google.common.collect.Sets;
import dk.antwars.core.game.Game;
import dk.antwars.core.game.mechanics.internal.GameLocation;
import dk.antwars.core.game.mechanics.external.Pheromone;
import dk.antwars.core.game.mechanics.internal.GameEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.Color;
import java.util.Set;

public class GamePlayGround extends Canvas {

    private Logger logger = LoggerFactory.getLogger(GamePlayGround.class);

    private static final long serialVersionUID = 5525527872732540812L;
	
	private final Game game;

	public GamePlayGround(final Game game) {
		this.game = game;
		this.setBackground(new Color(0,0,0));
	}

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private final static dk.antwars.core.ant.presentation.Color FOOD_COLOR = new dk.antwars.core.ant.presentation.Color(128,128,128);

    @Override
	public void paint(final Graphics graphics) {
        game.getPaintSemaphore().acquireUninterruptibly();
        final Graphics2D graphics2D = (Graphics2D) graphics;
        for (final GameLocation gameLocation : game.getWorld().getGameLocations()) {
            int maximumIntensity = 0;
            dk.antwars.core.ant.presentation.Color locationColor = new dk.antwars.core.ant.presentation.Color(0,0,0);
            Set<dk.antwars.core.ant.presentation.Color> morphColors = Sets.newHashSet();
            if (!gameLocation.getGameEntities().isEmpty()) {
                for (final GameEntity entity: gameLocation.getGameEntities()) {
                    if (!morphColors.contains(entity.getGameTeam().getColor())) {
                        morphColors.add(entity.getGameTeam().getColor());
                    }
                }
                maximumIntensity = Math.max(255, maximumIntensity);
            }
            if (gameLocation.getFoodAtomic().get() > 0) {
                maximumIntensity = Math.max(192, maximumIntensity);
                morphColors.add(FOOD_COLOR);
            }
            final Set<Pheromone> pheromones = gameLocation.getPheromones();
            if (pheromones != null) {
                for (final Pheromone pheromone : pheromones) {
                    if (!morphColors.contains(pheromone.getScentColor())) {
                        morphColors.add(pheromone.getScentColor());
                    }
                    maximumIntensity = Math.max((int)(224 * pheromone.getIntensity(game.getTime())), maximumIntensity);
                }
            }
            final dk.antwars.core.ant.presentation.Color scentColor = gameLocation.getScentColor();
            if (scentColor != null) {
                maximumIntensity = Math.max(64, maximumIntensity);
                if (!morphColors.contains(scentColor)) {
                    morphColors.add(scentColor);
                }
            }

            for (dk.antwars.core.ant.presentation.Color morphColor : morphColors) {
                enhanceColor(locationColor, morphColor);
            }

            graphics2D.setColor(locationColor.getAWTColor(maximumIntensity));
            graphics2D.fillRect(gameLocation.getCoordinates().getLongitude(), game.getWorld().getNumberOfWorldLongitudes() - gameLocation.getCoordinates().getLatitude(), 1, 1);
        }
        game.getGameSemaphore().release();
	}

    private void enhanceColor(dk.antwars.core.ant.presentation.Color locationColor, dk.antwars.core.ant.presentation.Color color) {
        locationColor.setRed(locationColor.getRed() | (color.getRed()));
        locationColor.setGreen(locationColor.getGreen() | (color.getGreen()));
        locationColor.setBlue(locationColor.getBlue() | (color.getBlue()));
    }

}
