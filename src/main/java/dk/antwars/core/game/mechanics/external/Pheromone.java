package dk.antwars.core.game.mechanics.external;

import dk.antwars.core.ant.presentation.Color;
import dk.antwars.core.game.Game;
import dk.antwars.core.game.mechanics.internal.GameEntity;

public class Pheromone {

    private final Scent scent;

    private final int birth;
    private final int age;

    public Pheromone(final Scent scent, final int birth, final int age) {
        this.scent = scent;
        this.birth = birth;
        this.age = age;
    }

    public Scent getScent() {
        return scent;
    }

    public double getIntensity(int time) {
        final int current = (time - birth);
        if (current > age) {
            return 0;
        }
        return (double) (age - current) / (double) age;
    }

    public Color getScentColor() {
        return this.scent != null ? this.scent.getColor() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (scent != ((Pheromone) o).scent) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return scent.hashCode();
    }

    public enum Scent {

        Black(0, 0, 0), Red(255, 0, 0), Green(0, 255, 0), Blue(0, 0, 255), Yellow(255, 255, 0), Cyan(0, 255, 255), Magenta(255, 0, 255), White(255, 255, 255), Trace;

        private Color color;
        Scent() {}
        Scent(int red, int green, int blue) {
            this.color = new Color((char)(red), (char)(green), (char)(blue));
        }

        public Color getColor() {
            return color;
        }
    }

}
