/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gravitysimulation;

/**
 *
 * @author Drew
 */
import java.awt.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;

public class GravitySimulation extends JPanel implements MouseInputListener {

    static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) (screenSize.getWidth() * 19 / 20);
    int height = (int) (screenSize.getHeight() * 17 / 20);
    static int paintDelay = 10;
    static int runDelay = 10;
    static GravitySimulation animation = new GravitySimulation();
    Point start = null;
    long startTime = -1;
    Point current = null;
    static int pointDiam = 15;
    static ArrayList<Particle> particles = new ArrayList<Particle>();

    public static void main(String[] args) {
        animation.addMouseListener(animation);
        animation.addMouseMotionListener(animation);
        animation.setPreferredSize(new Dimension(animation.width, animation.height));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame f = new JFrame("Gravity Simulation");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setLocation(0, 0);
                f.add(animation);
                f.pack();
                f.setLocationByPlatform(true);
                f.setResizable(true);
                f.setVisible(true);
            }
        });

        Timer timer = new Timer(true);
        TimerTask painting = new paintTiming();
        TimerTask running = new runTiming();
        timer.schedule(painting, 0, paintDelay);
        timer.schedule(running, 0, runDelay);
    }

    //paint method
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.red);
        for (Particle p : particles) {
            p.drawMe(g);
        }
        if (start != null) {
            g.setColor(Color.blue);
            int cx = (int) start.getX();
            int cy = (int) start.getY();
            int sx = cx - pointDiam / 2;
            int sy = cy - pointDiam / 2;
            int ex = (int) current.getX();
            int ey = (int) current.getY();
            g.fillOval(sx, sy, pointDiam, pointDiam);
            g.drawLine(cx, cy, ex, ey);
        }
        

    }

    //helper methods
    public void newParticle(Point p, double dx, double dy, double mass) {
        particles.add(new Particle(p, dx, dy, mass));
    }

    //timing classes
    static class paintTiming extends TimerTask {

        @Override
        public void run() {
            animation.repaint();
        }
    }

    static class runTiming extends TimerTask {

        @Override
        public void run() {
            Particle.checkHits();
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).next();
            }
        }
    }

    //Mouse Listener Methods and attributes
    public void mousePressed(MouseEvent e) {
        System.out.println("pressed : " + e.getButton());
        switch (e.getButton()) {
            case 1:
                startTime = (new Date()).getTime();
                start = e.getPoint();
                break;
        }
    }

    public void mouseReleased(MouseEvent e) {
        //System.out.println("released : " + e.getButton());
        switch (e.getButton()) {
            case 1:
                Point end = e.getPoint();
                double dx = end.getX() - start.getX();
                double dy = end.getY() - start.getY();
                dx *= Particle.dragCoef;
                dy *= Particle.dragCoef;
                int timePressed = (int) ((new Date()).getTime() - startTime);
                double mass = timePressed * 3;
                System.out.println("new mass = " + mass);
                newParticle(start, dx, dy, mass);
                start = null;
                break;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        current = e.getPoint();
    }

    public void mouseMoved(MouseEvent e) {
        current = e.getPoint();
    }
}
