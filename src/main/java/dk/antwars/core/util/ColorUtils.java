package dk.antwars.core.util;

import dk.antwars.core.ant.presentation.Color;

public class ColorUtils {

    public static Color createColor(final String antClassName) {
        final int hashCode = Math.abs(antClassName.hashCode());
        final int red = (hashCode << 16) & 0xFF;
        final int green = (hashCode << 8) & 0xFF;
        final int blue = (hashCode << 0) & 0xFF;
        return new Color((char)red, (char)green, (char)blue);
    }

    public static java.awt.Color convertColor(Color color) {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }
}
