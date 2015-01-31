package dk.antwars.core.util;

public class DrawingUtil {

    public static int calculatePheromoneRadius(int pheromoneIntensity) {
        return pheromoneIntensity / 20 + 1;
    }

}
