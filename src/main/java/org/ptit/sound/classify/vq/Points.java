package org.ptit.sound.classify.vq;

import java.io.Serializable;


public class Points implements Serializable {
    protected double coordinates[];
    protected int dimension;

    public Points(double co[]) {
        dimension = co.length;
        coordinates = co;
    }

    public double[] getAllCo() {
        return coordinates;
    }

    public double getCo(int i) {
        return coordinates[i];
    }

    public void setCo(int i, double value) {
        coordinates[i] = value;
    }

    public void changeCo(double tCo[]) {
        coordinates = tCo;
    }

    public int getDimension() {
        return dimension;
    }

    public static boolean equals(Points p1, Points p2) {
        boolean equal = true;
        int d = p1.getDimension();

        if (d == p2.getDimension()) {
            for (int k = 0; k < d && equal; k++) {
                if (p1.getCo(k) != p2.getCo(k)) {
                    equal = false;
                }
            }
        } else {
            equal = false;
        }

        return equal;
    }
}