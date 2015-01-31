package dk.antwars.core.game.gui;

import java.awt.*;
import java.util.concurrent.Semaphore;

import dk.antwars.core.game.Game;
import dk.antwars.core.game.mechanics.internal.GameTeam;
import dk.antwars.core.util.ColorUtils;

public class GameScoreBoard extends Canvas {

	private static final long serialVersionUID = 5525527872732540812L;

    private final Game game;

	public GameScoreBoard(final Game game) {
        this.game = game;
		this.setBackground(new Color(0,0,0));
	}
	
	@Override
	public void paint(Graphics g) {
        game.getPaintSemaphore().acquireUninterruptibly();
        int i = 0;
        final Graphics2D graphics2D = (Graphics2D) g;
        for (final GameTeam team: game.getGameTeams()) {
            graphics2D.setColor(team.getColor().getAWTColor());
            graphics2D.fillRect((i) * 5, (i + 1) * 5, team.getGameEntities().size() / game.getGameEntities().size() * getWidth(), 5);
            i++;
        }
        game.getGameSemaphore().release();
	}

}
