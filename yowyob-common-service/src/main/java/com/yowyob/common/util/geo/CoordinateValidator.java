package com.yowyob.common.util.geo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validateur de coordonnées géographiques
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Valide les coordonnées GPS (latitude/longitude)
 *          Vérifie les plages valides selon WGS84
 */
@Slf4j
@Component
public class CoordinateValidator {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;

    public boolean isValidLatitude(double latitude) {
        return latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE;
    }

    public boolean isValidLongitude(double longitude) {
        return longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE;
    }

    public boolean isValidCoordinate(double latitude, double longitude) {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    public boolean isValidCoordinateString(String coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return false;
        }

        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            return false;
        }

        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());
            return isValidCoordinate(lat, lon);
        } catch (NumberFormatException e) {
            log.warn("Format de coordonnées invalide: {}", coordinates);
            return false;
        }
    }

    public double[] parseCoordinates(String coordinates) {
        if (!isValidCoordinateString(coordinates)) {
            return null;
        }

        String[] parts = coordinates.split(",");
        return new double[] {
                Double.parseDouble(parts[0].trim()),
                Double.parseDouble(parts[1].trim())
        };
    }

    public String formatCoordinates(double latitude, double longitude) {
        if (!isValidCoordinate(latitude, longitude)) {
            throw new IllegalArgumentException("Coordonnées invalides");
        }
        return String.format(java.util.Locale.ROOT, "%.6f,%.6f", latitude, longitude);
    }

    public double normalizeLatitude(double latitude) {
        while (latitude > MAX_LATITUDE) {
            latitude -= 180.0;
        }
        while (latitude < MIN_LATITUDE) {
            latitude += 180.0;
        }
        return latitude;
    }

    public double normalizeLongitude(double longitude) {
        while (longitude > MAX_LONGITUDE) {
            longitude -= 360.0;
        }
        while (longitude < MIN_LONGITUDE) {
            longitude += 360.0;
        }
        return longitude;
    }
}