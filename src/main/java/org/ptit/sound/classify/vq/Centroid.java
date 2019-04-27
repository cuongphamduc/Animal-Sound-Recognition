package org.ptit.sound.classify.vq;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;


public class Centroid extends Points implements Serializable {

    protected double distortion = 0;
    protected Vector<Points> pts = new Vector<Points>(0);
    protected int total_pts;

    public Centroid(double[] Co) {
        super(Co);
        total_pts = 0;
    }

    public Points getPoint(int index) {
        return pts.get(index);
    }

    public int getNumPts() {
        return total_pts;
    }

    public void remove(Points pt, double dist) {
        Points tmpPoint = pts.get(0);
        int i = -1;

        Enumeration enums = pts.elements();
        boolean found = false;
        while (enums.hasMoreElements() && !found) {
            tmpPoint = (Points) enums.nextElement();
            i++;

            if (Points.equals(pt, tmpPoint)) {
                found = true;
            }
        }

        if (found) {
            pts.remove(i);
            distortion -= dist;
            total_pts--;
        } else {
            System.out.println("err: point not found");
        }
    }

    public void add(Points pt, double dist) {
        total_pts++;
        pts.add(pt);
        distortion += dist;
    }

    public void update() {
        double[] sum_coordinates = new double[dimension];
        Points tmpPoint;
        Enumeration enums = pts.elements();

        while (enums.hasMoreElements()) {
            tmpPoint = (Points) enums.nextElement();

            for (int k = 0; k < dimension; k++) {
                sum_coordinates[k] += tmpPoint.getCo(k);
            }
        }

        for (int k = 0; k < dimension; k++) {
            setCo(k, sum_coordinates[k] / total_pts);
            pts = new Vector(0);
        }

        // reset number of points
        total_pts = 0;
        // reset distortion measure
        distortion = 0;
    }

    public double getDistortion() {
        return distortion;
    }
}