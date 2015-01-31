package dk.antwars.core.game.mechanics.external;

import dk.antwars.core.game.mechanics.internal.GameEntity;

public class FriendlyEntity extends Entity {

    public FriendlyEntity(GameEntity gameEntity) {
        super(gameEntity);
    }

//    public Memory getMemory() {
//        return getGameEntity().getMemory();
//    }

    public Team getTeam() {
        return getGameEntity().getGameTeam().getTeam();
    }

}
