package com.yowyob.common.util.geo;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitaires pour les calculs géographiques
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Implémente les formules de Haversine et Vincenty
 * pour calculs de distance sur sphère (Terre)
 */
@Slf4j
public final class GeoUtils {

    private GeoUtils() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double EARTH_RADIUS_MILES = 3958.8;

    public static double calculateDistance(
            double lat1, double lon1,
            double lat2, double lon2) {
        return calculateDistanceInKm(lat1, lon1, lat2, lon2);
    }

    public static double calculateDistanceInKm(
            double lat1, double lon1,
            double lat2, double lon2) {
        return haversineDistance(lat1, lon1, lat2, lon2, EARTH_RADIUS_KM);
    }

    public static double calculateDistanceInMiles(
            double lat1, double lon1,
            double lat2, double lon2) {
        return haversineDistance(lat1, lon1, lat2, lon2, EARTH_RADIUS_MILES);
    }

    private static double haversineDistance(
            double lat1, double lon1,
            double lat2, double lon2,
            double radius) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) *
                        Math.cos(lat1Rad) * Math.cos(lat2Rad);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    public static double calculateBearing(
            double lat1, double lon1,
            double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double dLon = Math.toRadians(lon2 - lon1);

        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) -
                Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }

    public static double[] calculateMidpoint(
            double lat1, double lon1,
            double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double dLon = Math.toRadians(lon2 - lon1);

        double bx = Math.cos(lat2Rad) * Math.cos(dLon);
        double by = Math.cos(lat2Rad) * Math.sin(dLon);

        double lat3Rad = Math.atan2(
                Math.sin(lat1Rad) + Math.sin(lat2Rad),
                Math.sqrt((Math.cos(lat1Rad) + bx) * (Math.cos(lat1Rad) + bx) + by * by)
        );

        double lon3Rad = lon1Rad + Math.atan2(by, Math.cos(lat1Rad) + bx);

        return new double[]{
                Math.toDegrees(lat3Rad),
                Math.toDegrees(lon3Rad)
        };
    }

    public static boolean isPointInRadius(
            double centerLat, double centerLon,
            double pointLat, double pointLon,
            double radiusKm) {

        double distance = calculateDistanceInKm(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }

    public static double[] calculateBoundingBox(
            double centerLat, double centerLon,
            double radiusKm) {

        double latDelta = radiusKm / 111.0;

        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(centerLat)));

        return new double[]{
                centerLat - latDelta,
                centerLon - lonDelta,
                centerLat + latDelta,
                centerLon + lonDelta
        };
    }
}