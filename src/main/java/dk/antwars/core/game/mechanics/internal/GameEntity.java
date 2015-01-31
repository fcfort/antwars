package dk.antwars.core.game.mechanics.internal;

import dk.antwars.core.ant.Ant;
import dk.antwars.core.ant.Memory;
import dk.antwars.core.ant.Self;
import dk.antwars.core.ant.species.Species;
import dk.antwars.core.game.mechanics.external.FriendlyEntity;
import dk.antwars.core.game.mechanics.external.HostileEntity;

public class GameEntity {

    private final int identifier;
    private final Ant ant;
    private final Self self;

    private final Species species;
    private final GameTeam team;

    private HostileEntity hostileEntity;
    private FriendlyEntity friendlyEntity;

    private final Memory memory;

    private int accumulatedDamage;

    private GameLocation gameLocation;

    public GameEntity(final int identifier, final Ant ant, final Species species, GameTeam team) {
        this.ant = ant;
        this.self = new Self(this);
        this.identifier = identifier;
        this.species = species;
        this.team = team;
        this.memory = new Memory(species.getMemory());
        this.friendlyEntity = new FriendlyEntity(this);
        this.hostileEntity = new HostileEntity(this);
    }

    public Ant getAnt() {
        return ant;
    }
    public Self getSelf() {
        return self;
    }
    public Species getSpecies() {
        return species;
    }

    @Override
    final public boolean equals(Object obj) {
        if (obj instanceof GameEntity) {
            if (identifier == ((GameEntity) obj).identifier) {
                return true;
            }
        }
        return super.equals(obj);
    }

    public int getHealth() {
        return species.getHealth() + species.getArmor() - accumulatedDamage;
    }
    public void damage(int damage) {
        this.accumulatedDamage += damage;
    }
    public void heal(final int healing) {
        if (this.accumulatedDamage > healing) {
            this.accumulatedDamage -= healing;
        } else {
            this.accumulatedDamage = 0;
        }
    }
    public int getAccumulatedDamage() {
        return accumulatedDamage;
    }

    private int usedActionPoints = 0;
    public void useActionPoint() {
        usedActionPoints++;
    }
    public void resetActionPoints() {
        usedActionPoints = 0;
    }
    public int getRemainingActionPoints() {
        return getSpecies().getAgility() - usedActionPoints;
    }

    public Memory getMemory() {
        return memory;
    }

    public GameTeam getGameTeam() {
        return team;
    }
    public FriendlyEntity getFriendlyEntity() {
        return friendlyEntity;
    }
    public HostileEntity getHostileEntity() {
        return hostileEntity;
    }

    public GameLocation getGameLocation() {
        return gameLocation;
    }
    public void setGameLocation(GameLocation gameLocation) {
        this.gameLocation = gameLocation;
    }

    final public int hashCode() {
        return identifier;
    }

    public String toString() {
        return getAnt().getClass().getName() + "(id:" + identifier + ", damage:" + accumulatedDamage + ")";
    }

    public boolean isFriendlyTowards(GameEntity gameEntity) {
        return this.getGameTeam().equals(gameEntity.getGameTeam());
    }

    public boolean hasActionPointsLeft() {
        return getRemainingActionPoints() > 0;
    }
}
