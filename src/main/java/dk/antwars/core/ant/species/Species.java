package dk.antwars.core.ant.species;

public class Species {

    private final int strength;
    private final int agility;
    private final int intellect;

    public Species(int strength, int agility, int intellect) {
        this.strength = getValue(strength);
        this.agility = getValue(agility);
        this.intellect = getValue(intellect);
    }

    public boolean isQueen() { return false; }
    public int getCost() {
        return strength + agility + intellect;
    }

    public int getStrength() {
        return strength;
    }

    public int getAgility() {
        return agility;
    }

    public int getIntellect() {
        return intellect;
    }

    public int getHealth() {
        return strength;
    }
    public int getArmor() {
        return (strength + agility) / 2;
    }

    public int getMemory() {
        return intellect * 4;
    }

    public int getSight() {
        return (int)((float)(intellect)/2 + (float)(agility)/2);
    }

/*

    private double increasingCost(double costPoints, double increaseFactor) {
        final double increasedCost = Math.pow(costPoints, increaseFactor);
        return increasedCost;
    }
*/

    private int getValue(int value) {
        return value > 5 ? 5 : value < 1 ? 1 : value;
    }

}
