package dk.antwars.core.game.mechanics.internal;

import com.google.common.collect.Sets;
import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.mechanics.external.Team;

import java.util.Set;

public class GameTeam {

    private final Team team;
    private final Class<Ant> antClass;
    private final Color color;

    private Set<GameEntity> gameEntities;

    public GameTeam(Class<Ant> antClass, Color color) {
        this.antClass = antClass;
        this.color = color;
        this.team = new Team(this);
        this.gameEntities = Sets.newHashSet();
    }

    public Class<Ant> getAntClass() {
        return antClass;
    }
    public Color getColor() {
        return color;
    }

    public Team getTeam() {
        return team;
    }

    public boolean equals(GameTeam team) {
        return this.color.equals(team.getColor());
    }

    public String toString() {
		return getAntClass().getSimpleName() + " " + getColor();
	}

    public void addGameEntity(GameEntity gameEntity) {
        gameEntities.add(gameEntity);
    }
    public void removeGameEntity(GameEntity gameEntity) {
        gameEntities.remove(gameEntity);
    }

    public Set<GameEntity> getGameEntities() {
        return gameEntities;
    }

}
