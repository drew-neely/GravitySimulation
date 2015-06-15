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
import java.awt.event.*;

public class Particle {

    public static final double sizeCoef = 5.0;
    public static final double dragCoef = .01;
    public static final double gravCoef = 50;

    public static void checkHits() {
        ArrayList<Particle> allParts = GravitySimulation.particles;
        boolean changed = false;
        for (int a = 0; a < allParts.size() - 1 && !changed; a++) {
            for (int b = a + 1; b < allParts.size() && !changed; b++) {
                if (colliding(allParts.get(a), allParts.get(b))) {
                    joinParts(a, b);
                    changed = true;
                    break;
                }
            }
        }
        if (changed) {
            checkHits();
        }
    }

    public static boolean colliding(Particle p1, Particle p2) {
        double minDist = (p1.diameter() + p2.diameter()) / 2;
        double dist = Math.hypot(p1.x - p2.x, p1.y - p2.y);
        if (dist < minDist) {
            return true;
        }
        return false;
    }

    public static void joinParts(int a, int b) {
        ArrayList<Particle> allParts = GravitySimulation.particles;
        Particle p1 = allParts.get(a);
        Particle p2 = allParts.get(b);
        double x = (p1.x + p2.x) / 2;  //(p1.x * p1.mass + p2.x * p2.mass) / (2 * (p1.mass + p2.mass));
        double y = (p1.y + p2.y) / 2;  //(p1.y * p1.mass + p2.y * p2.mass) / (2 * (p1.mass + p2.mass));
        double dx = (p1.dx * p1.mass + p2.dx * p2.mass) / (2 * (p1.mass + p2.mass));
        double dy = (p1.dy * p1.mass + p2.dy * p2.mass) / (2 * (p1.mass + p2.mass));
        double mass = p1.mass + p2.mass;
        Particle p3 = new Particle(new Point((int) x, (int) y), dx, dy, mass);
        allParts.remove(a);
        allParts.remove(b - ((a < b) ? 1 : 0));
        allParts.add(p3);

    }
    public double x;
    public double y;
    public double dx;
    public double dy;
    public double mass;
    //diameter and gravity is dependant on mass
    //diameter is 5 * log(mass)

    public Particle() {
    }

    public Particle(Point p, double dx, double dy, double mass) {
        this.x = p.getX();
        this.y = p.getY();
        this.dx = dx;
        this.dy = dy;
        this.mass = mass;
    }

    public void next() {
        try {
            ArrayList<Particle> allParts = GravitySimulation.particles;
            ArrayList<Double> impulseX = new ArrayList<Double>();
            ArrayList<Double> impulseY = new ArrayList<Double>();


            for (Particle p : allParts) {
                if (!this.equals(p)) {
                    double distSqr = Math.pow(distance(p), 2);
                    if (distSqr <= 25) {
                        distSqr = 25;
                    }
                    impulseX.add(p.mass / distSqr * ((p.x > this.x) ? 1 : -1));
                    impulseY.add(p.mass / distSqr * ((p.y > this.y) ? 1 : -1));
                }
            }

            double ddx = 0;
            double ddy = 0;
            for (int i = 0; i < impulseX.size(); i++) {
                ddx += impulseX.get(i).doubleValue();
                ddy += impulseY.get(i).doubleValue();
            }

            ddx *= gravCoef / mass;
            ddy *= gravCoef / mass;

            dx += ddx;
            dy += ddy;
            x += dx;
            y += dy;
        } catch (Exception e) {
        }
    }

    public double diameter() {
        return sizeCoef * Math.log10(mass);
    }

    public void drawMe(Graphics g) {
        double diam = diameter();
        int xCorner = (int) (x - diam / 2);
        int yCorner = (int) (y - diam / 2);
        g.fillOval(xCorner, yCorner, (int) diam, (int) diam);
    }

    public double distance(Particle p) {
        return Math.hypot(p.x - this.x, p.y - this.y);
    }
}
