package com.example.charles.u_map.utils;

public class DirectionalVectorCalculator {


    private static double getAzimuth(double phi1, double lambda1, double phi2, double lambda2) {
        double azimuth;

        double deltaPhi = Math.toRadians(phi2 - phi1);
        double deltaLambda = Math.toRadians(lambda2 - lambda1);

        double sinDeltaLambda = Math.sin(deltaLambda);
        double cosDeltaLambda = Math.cos(deltaLambda);

        double sinPhi1 = Math.sin(Math.toRadians(phi1));
        double sinPhi2 = Math.sin(Math.toRadians(phi2));
        double cosPhi1 = Math.cos(Math.toRadians(phi1));
        double cosPhi2 = Math.cos(Math.toRadians(phi2));


        double x = sinDeltaLambda * cosPhi2;
        double y = cosPhi1 * sinPhi2 - sinPhi1 * cosPhi2 * cosDeltaLambda;

        azimuth = Math.atan2(x, y);

        System.out.println("Azimuth = " + Math.toDegrees(azimuth));
        return Math.toDegrees(azimuth);
    }
}
