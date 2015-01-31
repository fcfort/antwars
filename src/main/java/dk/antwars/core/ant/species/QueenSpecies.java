package dk.antwars.core.ant.species;

public class QueenSpecies extends Species {

    public QueenSpecies() {
        super(5,5,5);
    }

    @Override
    public boolean isQueen() {
        return true;
    }

    @Override
    public int getCost() {
        return 50;
    }
}
