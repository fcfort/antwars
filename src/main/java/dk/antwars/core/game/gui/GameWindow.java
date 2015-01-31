package dk.antwars.core.game.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Semaphore;

import dk.antwars.core.game.Game;

public class GameWindow extends Frame {

	private static final long serialVersionUID = 7017063040855355452L;

    private Game game;
	private GamePlayGround playground;
/*
	private GameScoreBoard scoreboard;
*/

	public GameWindow(final Game game) {
		super("Antwars!");
        this.game = game;
		
/*
		this.scoreboard = new GameScoreBoard(game);
        this.scoreboard.setLocation(0, 0);
        this.scoreboard.setSize(game.getWorld().getNumberOfWorldLongitudes(), 20 * game.getGameTeams().size());
        this.add(scoreboard);
*/

        this.playground = new GamePlayGround(game);
        this.playground.setLocation(0, 0);
        this.playground.setSize(game.getWorld().getNumberOfWorldLongitudes(), game.getWorld().getNumberOfWorldLatitudes());
        this.add(playground);

		final int calculatedWidth = this.playground.getWidth();
		final int calculatedHeight = this.playground.getHeight();// + this.scoreboard.getHeight();

		this.setBounds(0, 0, calculatedWidth, calculatedHeight);

		this.setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                game.closeRequested();
            }
        });
	}

    public void update() {
        this.playground.repaint();
//        this.scoreboard.repaint();
    }
}
