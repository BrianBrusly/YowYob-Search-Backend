package com.yowyob.common.util.geo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests pour GeoUtils
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("GeoUtils Tests")
class GeoUtilsTest {

    @Test
    @DisplayName("Devrait calculer la distance entre deux points")
    void shouldCalculateDistanceBetweenTwoPoints() {
        double lat1 = 3.8480;
        double lon1 = 11.5021;
        double lat2 = 4.0511;
        double lon2 = 9.7679;

        double distance = GeoUtils.calculateDistanceInKm(lat1, lon1, lat2, lon2);

        assertThat(distance).isGreaterThan(0);
        assertThat(distance).isCloseTo(194.0, within(5.0));
    }

    @Test
    @DisplayName("Devrait calculer une distance de zéro pour le même point")
    void shouldCalculateZeroDistanceForSamePoint() {
        double lat = 3.8480;
        double lon = 11.5021;

        double distance = GeoUtils.calculateDistanceInKm(lat, lon, lat, lon);

        assertThat(distance).isCloseTo(0.0, within(0.001));
    }

    @Test
    @DisplayName("Devrait calculer la distance en miles")
    void shouldCalculateDistanceInMiles() {
        double lat1 = 3.8480;
        double lon1 = 11.5021;
        double lat2 = 4.0511;
        double lon2 = 9.7679;

        double distanceKm = GeoUtils.calculateDistanceInKm(lat1, lon1, lat2, lon2);
        double distanceMiles = GeoUtils.calculateDistanceInMiles(lat1, lon1, lat2, lon2);

        assertThat(distanceMiles).isCloseTo(distanceKm * 0.621371, within(1.0));
    }

    @Test
    @DisplayName("Devrait calculer le bearing entre deux points")
    void shouldCalculateBearing() {
        double lat1 = 3.8480;
        double lon1 = 11.5021;
        double lat2 = 4.0511;
        double lon2 = 9.7679;

        double bearing = GeoUtils.calculateBearing(lat1, lon1, lat2, lon2);

        assertThat(bearing).isBetween(0.0, 360.0);
    }

    @Test
    @DisplayName("Devrait calculer le point médian")
    void shouldCalculateMidpoint() {
        double lat1 = 3.8480;
        double lon1 = 11.5021;
        double lat2 = 4.0511;
        double lon2 = 9.7679;

        double[] midpoint = GeoUtils.calculateMidpoint(lat1, lon1, lat2, lon2);

        assertThat(midpoint).hasSize(2);
        assertThat(midpoint[0]).isBetween(Math.min(lat1, lat2), Math.max(lat1, lat2));
        assertThat(midpoint[1]).isBetween(Math.min(lon1, lon2), Math.max(lon1, lon2));
    }

    @Test
    @DisplayName("Devrait vérifier si un point est dans un rayon")
    void shouldCheckIfPointInRadius() {
        double centerLat = 3.8480;
        double centerLon = 11.5021;
        double pointLat = 3.8500;
        double pointLon = 11.5100;
        double radiusKm = 2.0;

        boolean isInRadius = GeoUtils.isPointInRadius(centerLat, centerLon, pointLat, pointLon, radiusKm);

        assertThat(isInRadius).isTrue();
    }

    @Test
    @DisplayName("Devrait vérifier qu'un point n'est pas dans un rayon")
    void shouldCheckIfPointNotInRadius() {
        double centerLat = 3.8480;
        double centerLon = 11.5021;
        double pointLat = 4.0511;
        double pointLon = 9.7679;
        double radiusKm = 10.0;

        boolean isInRadius = GeoUtils.isPointInRadius(centerLat, centerLon, pointLat, pointLon, radiusKm);

        assertThat(isInRadius).isFalse();
    }

    @Test
    @DisplayName("Devrait calculer une bounding box")
    void shouldCalculateBoundingBox() {
        double centerLat = 3.8480;
        double centerLon = 11.5021;
        double radiusKm = 10.0;

        double[] bbox = GeoUtils.calculateBoundingBox(centerLat, centerLon, radiusKm);

        assertThat(bbox).hasSize(4);
        assertThat(bbox[0]).isLessThan(centerLat);
        assertThat(bbox[1]).isLessThan(centerLon);
        assertThat(bbox[2]).isGreaterThan(centerLat);
        assertThat(bbox[3]).isGreaterThan(centerLon);
    }
}