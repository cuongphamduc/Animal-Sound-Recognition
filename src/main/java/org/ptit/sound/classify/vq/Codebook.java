package org.ptit.sound.classify.vq;

import org.ptit.sound.classify.CodeBookDictionary;
import org.ptit.sound.db.DataBase;
import org.ptit.sound.db.ObjectIODataBase;


public class Codebook {
    protected final double SPLIT = 0.01;
    protected final double MIN_DISTORTION = 0.1;
    protected int codebook_size = 256;
    protected Centroid centroids[];
    protected Points pt[];
    protected int dimension;

    public Codebook(Points tmpPt[], int size) {
        this.pt = tmpPt;
        this.codebook_size = size;

        if (pt.length >= codebook_size) {
            dimension = pt[0].getDimension();
            initialize();
        } else {
            System.out.println("err: not enough training points");
        }
    }

    public Codebook(Points tmpPt[]) {
        this.pt = tmpPt;

        if (pt.length >= codebook_size) {
            dimension = pt[0].getDimension();
            initialize();
        } else {
            System.out.println("err: not enough training points");
        }
    }

    public Codebook() throws Exception {
        DataBase db = new ObjectIODataBase();
        db.setType("cbk");
        CodeBookDictionary cbd = new CodeBookDictionary();
        cbd = (CodeBookDictionary) db.readModel(null);
        dimension = cbd.getDimension();
        centroids = cbd.getCent();
    }

    private void showParameters() {
        for (int c = 0; c < centroids.length; c++) {
            for (int k = 0; k < dimension; k++) {
                System.out.print(centroids[c].getCo(k) + "\t");
            }
            System.out.println();
        }
    }

    protected void initialize() {
        double distortion_before_update = 0;
        double distortion_after_update = 0;
        centroids = new Centroid[1];

        double origin[] = new double[dimension];
        centroids[0] = new Centroid(origin);

        for (int i = 0; i < pt.length; i++) {
            centroids[0].add(pt[i], 0);
        }

        centroids[0].update();

        while (centroids.length < codebook_size) {
            split();

            groupPtoC();

            do {
                for (int i = 0; i < centroids.length; i++) {
                    distortion_before_update += centroids[i].getDistortion();
                    centroids[i].update();
                }

                // regroup
                groupPtoC();

                for (int i = 0; i < centroids.length; i++) {
                    distortion_after_update += centroids[i].getDistortion();
                }

            } while (Math.abs(distortion_after_update - distortion_before_update) < MIN_DISTORTION);
        }
    }

    public void saveToFile() throws Exception {
        DataBase db = new ObjectIODataBase();
        db.setType("cbk");
        CodeBookDictionary cbd = new CodeBookDictionary();

        for (int i = 0; i < centroids.length; i++) {
            centroids[i].pts.removeAllElements();
        }
        cbd.setDimension(dimension);
        cbd.setCent(centroids);
        db.saveModel(cbd, null);// filepath is not used
        // System.out.println("Showing parameters");
        // showParameters();
    }

    protected void split() {
        System.out.println("Centroids length now becomes " + centroids.length + 2);
        Centroid temp[] = new Centroid[centroids.length * 2];
        double tCo[][];
        for (int i = 0; i < temp.length; i += 2) {
            tCo = new double[2][dimension];
            for (int j = 0; j < dimension; j++) {
                tCo[0][j] = centroids[i / 2].getCo(j) * (1 + SPLIT);
            }
            temp[i] = new Centroid(tCo[0]);
            for (int j = 0; j < dimension; j++) {
                tCo[1][j] = centroids[i / 2].getCo(j) * (1 - SPLIT);
            }
            temp[i + 1] = new Centroid(tCo[1]);
        }

        centroids = new Centroid[temp.length];
        centroids = temp;
    }

    public int[] quantize(Points pts[]) {
        int output[] = new int[pts.length];
        for (int i = 0; i < pts.length; i++) {
            output[i] = closestCentroidToPoint(pts[i]);
        }
        return output;
    }

    public double getDistortion(Points pts[]) {
        double dist = 0;
        for (int i = 0; i < pts.length; i++) {
            int index = closestCentroidToPoint(pts[i]);
            double d = getDistance(pts[i], centroids[index]);
            dist += d;
        }
        return dist;
    }

    private int closestCentroidToPoint(Points pt) {
        double tmp_dist = 0;
        double lowest_dist = 0; // = getDistance(pt, centroids[0]);
        int lowest_index = 0;

        for (int i = 0; i < centroids.length; i++) {
            tmp_dist = getDistance(pt, centroids[i]);
            if (tmp_dist < lowest_dist || i == 0) {
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        return lowest_index;
    }

    private int closestCentroidToCentroid(Centroid c) {
        double tmp_dist = 0;
        double lowest_dist = Double.MAX_VALUE;
        int lowest_index = 0;
        for (int i = 0; i < centroids.length; i++) {
            tmp_dist = getDistance(c, centroids[i]);
            if (tmp_dist < lowest_dist && centroids[i].getNumPts() > 1) {
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        return lowest_index;
    }

    private int closestPoint(Centroid c1, Centroid c2) {
        double tmp_dist = 0;
        double lowest_dist = getDistance(c2.getPoint(0), c1);
        int lowest_index = 0;
        for (int i = 1; i < c2.getNumPts(); i++) {
            tmp_dist = getDistance(c2.getPoint(i), c1);
            if (tmp_dist < lowest_dist) {
                lowest_dist = tmp_dist;
                lowest_index = i;
            }
        }
        return lowest_index;
    }

    private void groupPtoC() {
        for (int i = 0; i < pt.length; i++) {
            int index = closestCentroidToPoint(pt[i]);
            centroids[index].add(pt[i], getDistance(pt[i], centroids[index]));
        }

        for (int i = 0; i < centroids.length; i++) {
            if (centroids[i].getNumPts() == 0) {
                int index = closestCentroidToCentroid(centroids[i]);

                int closestIndex = closestPoint(centroids[i], centroids[index]);
                Points closestPt = centroids[index].getPoint(closestIndex);
                centroids[index].remove(closestPt, getDistance(closestPt, centroids[index]));
                centroids[i].add(closestPt, getDistance(closestPt, centroids[i]));
            }
        }
    }

    private double getDistance(Points tPt, Centroid tC) {
        double distance = 0;
        double temp = 0;
        for (int i = 0; i < dimension; i++) {
            temp = tPt.getCo(i) - tC.getCo(i);
            distance += temp * temp;
        }
        distance = Math.sqrt(distance);
        return distance;
    }
}