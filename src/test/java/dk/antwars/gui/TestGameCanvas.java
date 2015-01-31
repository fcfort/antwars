package dk.antwars.gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TestGameCanvas {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws InterruptedException {
        final Canvas canvas = new Canvas() {
            @Override
            public void paint(Graphics g) {
                setVisible(true);
                setBounds(200, 200, 200, 200);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new RadialGradientPaint(35, 35, 25, new float[]{0,1}, new Color[]{new Color(255,0,0), new Color(255,0,0,0)}));
                g2.fillOval(10, 10, 50, 50);
                g2.setPaint(new RadialGradientPaint(55, 55, 25, new float[]{0,1}, new Color[]{new Color(255,0,0), new Color(255,0,0,0)}));
                g2.fillOval(30, 30, 50, 50);
            }
        };
        canvas.repaint();
        final Frame frame = new Frame();
        frame.setBounds(0, 0, 200, 200);
        frame.add(canvas);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        synchronized (frame) {
            try {
                frame.wait(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
