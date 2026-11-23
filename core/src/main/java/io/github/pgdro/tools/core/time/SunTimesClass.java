package io.github.pgdro.tools.core.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import io.github.pgdro.tools.core.BasicStructuresClass;

/**
 * Timing calculation for Sun positions
 */
public final class SunTimesClass {
    /**
     * Standard apparent sunrise/sunset:
     * 90° + ~0.833° for refraction + solar radius.
     */
    private static final double SUNRISE_SUNSET_ZENITH = 90.833;
    /**
     * Record for SunRise and SunSet
     */
    public record SunRiseAndSunSet(
            Optional<Instant> sunrise,
            Optional<Instant> sunset
    ) {}

    /**
     * Constructor
     */
    private SunTimesClass() {}

    /**
     * Computes sunrise and sunset for the UTC calendar date containing utcTimestamp.
     *
     * latitude:  [-90, +90], north positive
     * longitude: [-180, +180], east positive
     */
    public static SunRiseAndSunSet calculate(
            final double latitude,
            final double longitude,
            final Instant utcTimestamp) {
        LocalDate utcDate = utcTimestamp.atZone(ZoneOffset.UTC).toLocalDate();
        return new SunRiseAndSunSet(
                calculateEvent(utcDate, latitude, longitude, true),
                calculateEvent(utcDate, latitude, longitude, false)
        );
    }

    /**
     * Convenience method for epoch milliseconds.
     */
    public static SunRiseAndSunSet calculate(
            final double latitude,
            final double longitude,
            final long utcEpochMillis) {
        return calculate(
                latitude,
                longitude,
                Instant.ofEpochMilli(utcEpochMillis)
        );
    }

    /**
     * 
     * @param date
     * @param latitude input value for Latitude
     * @param longitude input value for Longitude
     * @param sunrise boolean true if Sunrise, false for Sunset
     * @return
     */
    private static Optional<Instant> calculateEvent(
            final LocalDate date,
            final double latitude,
            final double longitude,
            final boolean sunrise) {
        /*
         * First approximation: use solar parameters around noon UTC.
         */
        double julianDayValue = julianDay(date.atStartOfDay(ZoneOffset.UTC).toInstant());
        double minutes = eventMinutesUtc(julianDayValue, latitude, longitude, sunrise);
        if (!Double.isFinite(minutes)) {
            return Optional.empty();
        }
        /*
         * Refine the result using the approximate event time.
         * Two iterations are plenty for this algorithm.
         */
        for (int i = 0; i < 2; i++) {
            double eventJulianDay = julianDayValue + minutes / 1440.0;
            minutes = eventMinutesUtc(eventJulianDay, latitude, longitude, sunrise);
            if (!Double.isFinite(minutes)) {
                return Optional.empty();
            }
        }
        Instant midnight = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        long nanos = Math.round(minutes * 60.0 * 1_000_000_000.0);
        return Optional.of(midnight.plusNanos(nanos));
    }

    /**
     * Longitude convention:
     *   east  = positive / west  = negative
     * Solar noon UTC:
     *   720 - 4*longitude - equationOfTime
     * Sunrise is before solar noon.
     * Sunset is after solar noon.
     */
    private static double eventMinutesUtc(
            final double julianDay,
            final double latitude,
            final double longitude,
            final boolean sunrise) {
        double t = julianCentury(julianDay);
        double eqTime = SolarPositionsCalculationsSubClass.equationOfTime(t);
        double solarDeclination = SolarPositionsCalculationsSubClass.solarDeclination(t);
        double hourAngle = hourAngle(latitude, solarDeclination);
        if (!Double.isFinite(hourAngle)) {
            return Double.NaN;
        }
        double hourAngleDeg = Math.toDegrees(hourAngle);
        if (sunrise) {
            return 720.0
                    - 4.0 * (longitude + hourAngleDeg)
                    - eqTime;
        } else {
            return 720.0
                    - 4.0 * (longitude - hourAngleDeg)
                    - eqTime;
        }
    }

    /**
     * logic for angle hour
     * @param latitude input Latitude
     * @param solarDeclination input solar declination
     * @return
     */
    private static double hourAngle(
            final double latitude,
            final double solarDeclination) {
        double latRad    = Math.toRadians(latitude);
        double declRad   = Math.toRadians(solarDeclination);
        double zenithRad = Math.toRadians(SUNRISE_SUNSET_ZENITH);
        double cosH      =
                (Math.cos(zenithRad)
                        - Math.sin(latRad) * Math.sin(declRad))
                / (Math.cos(latRad) * Math.cos(declRad));
        /*
         * No crossing of the requested zenith:
         * polar day or polar night.
         */
        if (cosH < -1.0 || cosH > 1.0) {
            return Double.NaN;
        }
        return Math.acos(cosH);
    }

    /**
     * Julian calendar date calculation from time Instant value
     * @param instant
     * @return
     */
    private static double julianDay(final Instant instant) {
        final double unixDays = instant.getEpochSecond() / 86_400.0
                + instant.getNano() / 86_400_000_000_000.0;
        return 2_440_587.5 + unixDays;
    }

    /**
     * compute Julian century 
     * @param inJulianDay input Julian day
     * @return Julian century
     */
    private static double julianCentury(final double inJulianDay) {
        return (inJulianDay - 2_451_545.0) / 36_525.0;
    }

    /**
     * Solar position calculations
     */
    public static final class SolarPositionsCalculationsSubClass {

        private static double geomMeanLongSun(double t) {
            double l = 280.46646
                    + t * (36_000.76983
                    + t * 0.0003032);
            return BasicStructuresClass.normalizeDegrees(l);
        }

        private static double geomMeanAnomalySun(double t) {
            return 357.52911
                    + t * (35_999.05029
                    - 0.0001537 * t);
        }

        private static double eccentricityEarthOrbit(double t) {
            return 0.016708634
                    - t * (0.000042037 + 0.0000001267 * t);
        }

        private static double sunEquationOfCenter(double t) {
            double m =
                    Math.toRadians(geomMeanAnomalySun(t));
            return Math.sin(m)
                    * (1.914602 - t * (0.004817 + 0.000014 * t))
                    + Math.sin(2.0 * m)
                    * (0.019993 - 0.000101 * t)
                    + Math.sin(3.0 * m)
                    * 0.000289;
        }

        private static double sunTrueLongitude(double t) {
            return geomMeanLongSun(t)
                    + sunEquationOfCenter(t);
        }

        private static double sunApparentLongitude(double t) {
            double omega = 125.04 - 1934.136 * t;
            return sunTrueLongitude(t)
                    - 0.00569
                    - 0.00478
                    * Math.sin(Math.toRadians(omega));
        }

        private static double meanObliquityOfEcliptic(double t) {
            double seconds =
                    21.448
                            - t * (46.815
                            + t * (0.00059
                            - t * 0.001813));
            return 23.0
                    + (26.0 + seconds / 60.0) / 60.0;
        }

        private static double obliquityCorrection(double t) {
            double e0 = meanObliquityOfEcliptic(t);
            double omega = 125.04 - 1934.136 * t;
            return e0
                    + 0.00256
                    * Math.cos(Math.toRadians(omega));
        }

        private static double solarDeclination(double t) {
            double e = Math.toRadians(obliquityCorrection(t));
            double lambda = Math.toRadians(sunApparentLongitude(t));
            return Math.toDegrees(
                    Math.asin(
                            Math.sin(e) * Math.sin(lambda)
                    )
            );
        }

        /**
         * Equation of time, in minutes.
         */
        private static double equationOfTime(double t) {
            double epsilon = Math.toRadians(obliquityCorrection(t));
            double l0 = Math.toRadians(geomMeanLongSun(t));
            double e = eccentricityEarthOrbit(t);
            double m = Math.toRadians(geomMeanAnomalySun(t));
            double y = Math.tan(epsilon / 2.0);
            y *= y;
            double value =
                    y * Math.sin(2.0 * l0)
                            - 2.0 * e * Math.sin(m)
                            + 4.0 * e * y
                                * Math.sin(m)
                                * Math.cos(2.0 * l0)
                            - 0.5 * y * y
                                * Math.sin(4.0 * l0)
                            - 1.25 * e * e
                                * Math.sin(2.0 * m);
            return 4.0 * Math.toDegrees(value);
        }

    }

}
