/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.time;

import io.github.pgdro.tools.core.BasicStructuresClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.NonNull;

/**
 * Sun position class
 */
public final class SunClass {
    /** Constant for Next Event */
    private static final String NEXT_EVENT = "Next event";
    /** Constant for a Prior Event */
    private static final String PRIOR_EVENT = "Prior event";
    /** Latitude variable */
    private static double doubleLatitude;
    /** Longitude variable */
    private static double doubleLongitude;
    /** ZoneId variable */
    private static ZoneId internalZoneId;
    /** Now as ZonedDateTime variable */
    private static ZonedDateTime internalNowZ;
    /** SunRise value for Today */
    private static ZonedDateTime sunRiseToday;
    /** SunRise value for Tomorrow */
    private static ZonedDateTime sunRiseTomorrow;
    /** SunSet value for Today */
    private static ZonedDateTime sunSetToday;
    /** SunSet value for Tomorrow */
    private static ZonedDateTime sunSetTomorrow;
    /** SunSet value for Yesterday */
    private static ZonedDateTime sunSetYesterday;
    /** formatter Variable */
    /* default */ private static final DateTimeFormatter APPLIED_FORMATER
            = DateTimeFormatter.ofPattern(TimingClass.DATE_TIME_MS_LONG, Locale.US);
    /** Properties for output */
    private static final Map<String, String> MAP_SUN = new ConcurrentHashMap<>();

    /**
     * Constructor
     */
    private SunClass() {
        // intentionally blank
    }

    /**
     * Enhances sun position details
     * @param referenceType reference type
     * @param referenceNowZ now with Time Zone
     */
    private static void computeAndStoreSunRiseAndSunSet(
            final @NonNull String referenceType,
            final ZonedDateTime referenceNowZ) {
        final SunTimesSubClass.SunRiseAndSunSet referenceSun = SunTimesSubClass.calculate(
                doubleLatitude,
                doubleLongitude,
                referenceNowZ.toInstant()
        );
        final ZonedDateTime sunRiseReference = referenceSun.sunRise()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put(referenceType + " Sunrise", sunRiseReference.format(APPLIED_FORMATER));
        final ZonedDateTime sunSetReference = referenceSun.sunSet()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put(referenceType + " Sunset", sunSetReference.format(APPLIED_FORMATER));
        MAP_SUN.put(referenceType + " Light duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunRiseReference, sunSetReference));
        switch(referenceType) {
            case "Today":
                sunRiseToday = sunRiseReference;
                sunSetToday = sunSetReference;
                break;
            case "Tomorrow":
                sunRiseTomorrow = sunRiseReference;
                sunSetTomorrow = sunSetReference;
                break;
            case "Yesterday":
                sunSetYesterday = sunSetReference;
                break;
            default:
                // intentionally blank
                break;
        }
    }

    /**
     * computes sun position details
     */
    private static void getSunCurrentSituation() {
        String strSunSituation = "DOWN";
        String strCrtSituation = "After sunset";
        if (internalNowZ.isBefore(sunRiseToday)) {
            strCrtSituation = "Before sunrise";
            MAP_SUN.put(PRIOR_EVENT, "Sunset since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunSetYesterday, internalNowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunrise in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(internalNowZ, sunRiseToday));
        } else if (internalNowZ.isBefore(sunSetToday)) {
            strSunSituation = "UP";
            strCrtSituation = "In between sunrise and sunset";
            MAP_SUN.put(PRIOR_EVENT, "Sunrise since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunRiseToday, internalNowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunset in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(internalNowZ, sunSetToday));
        } else {
            MAP_SUN.put(PRIOR_EVENT, "Sunset since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunSetToday, internalNowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunrise in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(internalNowZ, sunRiseTomorrow));
        }
        MAP_SUN.put("Sun situation", strSunSituation);
        MAP_SUN.put("Current Situation", strCrtSituation);
    }

    /**
     * Calculates Sunrise and Sunset for a given location
     * @param crtLocationDetail location detail as String
     * @return Properties
     */
    public static Map<String, String> getSunRiseAndSet(final String crtLocationDetail) {
        MAP_SUN.put("Location [Street, City, Division, Country]", crtLocationDetail);
        internalNowZ = ZonedDateTime.now(internalZoneId);
        MAP_SUN.put("Current Timestamp", internalNowZ.format(APPLIED_FORMATER));
        computeAndStoreSunRiseAndSunSet("Today", internalNowZ);
        computeAndStoreSunRiseAndSunSet("Yesterday", internalNowZ.minusDays(1));
        computeAndStoreSunRiseAndSunSet("Tomorrow", internalNowZ.plusDays(1));
        MAP_SUN.put("Yesterday Night duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunSetYesterday, sunRiseToday));
        MAP_SUN.put("Today Night duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunSetToday, sunRiseTomorrow));
        final SunTimesSubClass.SunRiseAndSunSet sunInTwoDays = SunTimesSubClass.calculate(
                doubleLatitude,
                doubleLongitude,
                internalNowZ.plusDays(2).toInstant()
        );
        final ZonedDateTime sunRiseInTwoDays = sunInTwoDays.sunRise()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put("Tomorrow Night duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunSetTomorrow, sunRiseInTwoDays));
        getSunCurrentSituation();
        return MAP_SUN;
    }

    /**
     * Setter for dblLatitude
     * @param inLatitude input Latitude
     */
    public static void setLatitude(final double inLatitude) {
        if (!Double.isFinite(inLatitude)
                || inLatitude < -90.0
                || inLatitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        doubleLatitude = inLatitude;
        MAP_SUN.put("Latitude", String.valueOf(doubleLatitude));
    }

    /**
     * Setter for dblLongitude
     * @param inLongitude input Longitude
     */
    public static void setLongitude(final double inLongitude) {
        if (!Double.isFinite(inLongitude)
                || inLongitude < -180.0
                || inLongitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }
        doubleLongitude = inLongitude;
        MAP_SUN.put("Longitude", String.valueOf(doubleLongitude));
    }

    /**
     * Setter for strZoneName
     * @param inZoneName input Zone Name
     */
    public static void setZoneId(final String inZoneName) {
        try {
            MAP_SUN.put("Zone Name", inZoneName);
            // Pre-cache available IDs for high-performance lookup
            final ZoneId zoneId = ZoneId.of(inZoneName);
            final String strFeedback = String.format("Given zone name %s has the corresponding ZoneId %s",
                    inZoneName,
                    zoneId);
            LogExposureClass.LOGGER.debug(strFeedback);
            internalZoneId = zoneId;
        } catch (DateTimeException e) {
            final String strFeedback = String.format("Given zone name %s does not seem to be a valid one... %s",
                    inZoneName,
                    e.getMessage());
            LogExposureClass.LOGGER.debug(strFeedback);
        }
    }

    /**
     * Julian calculations
     */
    public static final class JulianSubClass {

        /**
         * Constructor
         */
        private JulianSubClass() {}

        /**
         * compute Julian century
         * @param inJulianDay input Julian day
         * @return double
         */
        private static double toJulianCentury(final double inJulianDay) {
            return (inJulianDay - 2_451_545.0) / 36_525.0;
        }

        /**
         * Julian calendar date calculation from time Instant value
         * @param instant input time Instant
         * @return double
         */
        private static double toJulianDay(final Instant instant) {
            final double unixDays = instant.getEpochSecond() / 86_400.0
                    + instant.getNano() / 86_400_000_000_000.0;
            return 2_440_587.5 + unixDays;
        }

    }

    /**
     * Solar position calculations
     */
    public static final class SolarPositionsCalculationsSubClass {

        /**
         * Constructor
         */
        private SolarPositionsCalculationsSubClass() {}

        /**
         * Eccentricity earth orbit logic
         * @param inJulianCentury input Julian century
         * @return double
         */
        private static double eccentricityEarthOrbit(final double inJulianCentury) {
            return 0.016708634 - inJulianCentury * (0.000042037 + 0.0000001267 * inJulianCentury);
        }

        /**
         * Equation of time, in minutes
         * @param inJulianCentury input Julian century
         * @return double
         */
        private static double equationOfTime(final double inJulianCentury) {
            final double epsilon           = Math.toRadians(obliquityCorrection(inJulianCentury));
            final double meanSunLongitude  = Math.toRadians(geometricMeanSunLongitude(inJulianCentury));
            final double eccentricityEarth = eccentricityEarthOrbit(inJulianCentury);
            final double meanSunAnomaly    = Math.toRadians(geometricMeanSunAnomaly(inJulianCentury));
            double iGreek = Math.tan(epsilon / 2.0);
            iGreek *= iGreek;
            final double doubleValue =
                    iGreek * Math.sin(2.0 * meanSunLongitude)
                            - 2.0 * eccentricityEarth * Math.sin(meanSunAnomaly)
                            + 4.0 * eccentricityEarth * iGreek
                                * Math.sin(meanSunAnomaly)
                                * Math.cos(2.0 * meanSunLongitude)
                            - 0.5 * iGreek * iGreek
                                * Math.sin(4.0 * meanSunLongitude)
                            - 1.25 * eccentricityEarth * eccentricityEarth
                                * Math.sin(2.0 * meanSunAnomaly);
            return 4.0 * Math.toDegrees(doubleValue);
        }

        /**
         * Geometric mean sun anomaly
         * @param inJulianCentury input Julian century
         * @return double
         */
        private static double geometricMeanSunAnomaly(final double inJulianCentury) {
            return 357.52911
                    + inJulianCentury * (35_999.05029 - 0.0001537 * inJulianCentury);
        }

        /**
         * Geometric mean sun longitude
         * @param inJulianCentury input Julian century
         * @return double
         */
        private static double geometricMeanSunLongitude(double inJulianCentury) {
            final double longitude = 280.46646 + inJulianCentury * (36_000.76983 + inJulianCentury * 0.0003032);
            return BasicStructuresClass.normalizeDegrees(longitude);
        }

        /**
         * Mean obliquity of ecliptic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double meanObliquityOfEcliptic(final double inLongitude) {
            final double seconds = 21.448 - inLongitude * (46.815 + inLongitude * (0.00059 - inLongitude * 0.001813));
            return 23.0 + (26.0 + seconds / 60.0) / 60.0;
        }

        /**
         * Solar declination logic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double obliquityCorrection(final double inLongitude) {
            final double omega = 125.04 - 1934.136 * inLongitude;
            return meanObliquityOfEcliptic(inLongitude)
                    + 0.00256 * Math.cos(Math.toRadians(omega));
        }

        /**
         * Solar declination logic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double solarDeclination(final double inLongitude) {
            final double obliqCorrection = Math.toRadians(obliquityCorrection(inLongitude));
            final double lambda = Math.toRadians(sunApparentLongitude(inLongitude));
            return Math.toDegrees(Math.asin(Math.sin(obliqCorrection) * Math.sin(lambda)));
        }

        /**
         * Apparent Longitude logic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double sunApparentLongitude(final double inLongitude) {
            final double omega = 125.04 - 1934.136 * inLongitude;
            return sunTrueLongitude(inLongitude)
                    - 0.00569
                    - 0.00478 * Math.sin(Math.toRadians(omega));
        }

        /**
         * Equation of Center logic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double sunEquationOfCenter(final double inLongitude) {
            double meanAnomaly = Math.toRadians(geometricMeanSunAnomaly(inLongitude));
            return Math.sin(meanAnomaly)
                    * (1.914602 - inLongitude * (0.004817 + 0.000014 * inLongitude))
                    + Math.sin(2.0 * meanAnomaly)
                    * (0.019993 - 0.000101 * inLongitude)
                    + Math.sin(3.0 * meanAnomaly)
                    * 0.000289;
        }

        /**
         * True Longitude for Sun logic
         * @param inLongitude input Longitude
         * @return double
         */
        private static double sunTrueLongitude(final double inLongitude) {
            return geometricMeanSunLongitude(inLongitude)
                    + sunEquationOfCenter(inLongitude);
        }

    }

    /**
     * Sun Time calculations
     */
    public static final class SunTimesSubClass {
        /**
         * Standard apparent sunrise/sunset:
         * 90° + ~0.833° for refraction + solar radius.
         */
        private static final double SUNRISE_SUNSET_ZENITH = 90.833;

        /**
         * Record for SunRise and SunSet
         */
        public record SunRiseAndSunSet(
                Optional<Instant> sunRise,
                Optional<Instant> sunSet
        ) {}

        /**
         * Constructor
         */
        private SunTimesSubClass() {
            // intentionally blank
        }

        /**
         * Computes sunrise and sunset for the UTC calendar date containing utcTimestamp.
         * latitude:  [-90, +90], north positive / longitude: [-180, +180], east positive
         * @param inLatitude input Latitude
         * @param inLongitude input Longitude
         * @param utcTimestamp input Time-Stamp
         * @return SunRiseAndSunSet record
         */
        public static SunRiseAndSunSet calculate(
                final double inLatitude,
                final double inLongitude,
                final Instant utcTimestamp) {
            final LocalDate utcDate = utcTimestamp.atZone(ZoneOffset.UTC).toLocalDate();
            return new SunRiseAndSunSet(
                    calculateEvent(utcDate, inLatitude, inLongitude, true),
                    calculateEvent(utcDate, inLatitude, inLongitude, false)
            );
        }

        /**
         * calculate Event
         * @param date input Local Date
         * @param latitude input value for Latitude
         * @param longitude input value for Longitude
         * @param sunrise boolean true if Sunrise, false for Sunset
         * @return Optional Instant sunrise or sunset
         */
        private static Optional<Instant> calculateEvent(
                final LocalDate date,
                final double latitude,
                final double longitude,
                final boolean sunrise) {
            // First approximation: use solar parameters around noon UTC.
            final double julianDayValue = JulianSubClass.toJulianDay(date.atStartOfDay(ZoneOffset.UTC).toInstant());
            double minutes = eventMinutesUtc(julianDayValue, latitude, longitude, sunrise);
            if (!Double.isFinite(minutes)) {
                return Optional.empty();
            }
            /*
             * Refine the result using the approximate event time.
             * Two iterations are plenty for this algorithm.
             */
            for (int iCounter = 0; iCounter < 2; iCounter++) {
                double eventJulianDay = julianDayValue + minutes / 1440.0;
                minutes = eventMinutesUtc(eventJulianDay, latitude, longitude, sunrise);
                if (!Double.isFinite(minutes)) {
                    return Optional.empty();
                }
            }
            final Instant midnight = date.atStartOfDay(ZoneOffset.UTC).toInstant();
            final long nanos = Math.round(minutes * 60.0 * 1_000_000_000.0);
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
            double julianCentury = JulianSubClass.toJulianCentury(julianDay);
            double eqTime = SolarPositionsCalculationsSubClass.equationOfTime(julianCentury);
            double solarDeclination = SolarPositionsCalculationsSubClass.solarDeclination(julianCentury);
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
         * @return double
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

    }

}
