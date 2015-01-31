package dk.antwars.core.ant;

import dk.antwars.core.game.mechanics.internal.actions.Action;

import java.util.Arrays;

public class Memory {

    private final int[] memories;
    private final Action[] actionMemories;

    public Memory(final int memorySize) {
        this.memories = new int[memorySize];
        this.actionMemories = new Action[memorySize];
    }

    public Memory(final int[] memories, final Action[] actionMemories) {
        this.memories = memories;
        this.actionMemories = actionMemories;
    }

    public int[] getMemories() {
        return memories;
    }
    public Action[] getActionMemories() {
        return Arrays.copyOf(actionMemories, actionMemories.length);
    }
}
