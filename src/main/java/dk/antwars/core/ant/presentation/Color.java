package dk.antwars.core.ant.presentation;

public class Color {

	private int red;
	private int green;
	private int blue;
		
	/**
	 * Values must be between 0 and 255, values outside this range are lost as they are cast to the primitive char
	 * @param red
	 * @param green
	 * @param blue
	 */
	public Color(int red, int green, int blue) {
		this.red = Math.max(Math.min(red, 255), 0);
		this.green = Math.max(Math.min(green, 255), 0);
		this.blue = Math.max(Math.min(blue, 255), 0);
	}
	
	public int getRed() {
		return red;
	}
    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
		return green;
	}
    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
		return blue;
	}
    public void setBlue(int blue) {
        this.blue = blue;
    }

    public static Color createColor(final String antClassName) {
        final int hashCode = antClassName.hashCode();
        final int red = (hashCode & 0x0000FF << 24);
        final int green = (hashCode & 0x00FF00 << 16);
        final int blue = (hashCode & 0xFF0000 << 8);
        return new Color(red, green, blue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;

        if (blue != color.blue) return false;
        if (green != color.green) return false;
        if (red != color.red) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) red;
        result = 31 * result + (int) green;
        result = 31 * result + (int) blue;
        return result;
    }

    @Override
    public String toString() {
        return "rbg(" + getRed() + ", " + getGreen() + ", " + getBlue() +")";
    }

    public java.awt.Color getAWTColor() {
        return new java.awt.Color(getRed(), getGreen(), getBlue());
    }

    public java.awt.Color getAWTColor(final int alpha) {
        return new java.awt.Color(Math.min(getRed(), alpha), Math.min(getGreen(), alpha), Math.min(getBlue(), alpha));
    }

}
