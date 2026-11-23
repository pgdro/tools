/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.time;

import io.github.pgdro.tools.core.log.LogExposureClass;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
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
    private static double dblLatitude;
    /** Longitude variable */
    private static double dblLongitude;
    /** ZoneId variable */
    private static ZoneId internalZoneId;
    /** Properties for output */
    private static final Map<String, String> MAP_SUN = new ConcurrentHashMap<>();
    /** formatter Variable */
    /* default */ private static final DateTimeFormatter APPLIED_FORMATER
            = DateTimeFormatter.ofPattern(TimingClass.DATE_TIME_MS_LONG, Locale.US);

    /**
     * Calculates Sunrise and Sunset for a given location
     * @param crtLocationDetail location detail as String
     * @return Properties
     */
    public static Map<String, String> getSunRiseAndSet(final String crtLocationDetail) {
        final ZonedDateTime nowZ = ZonedDateTime.now(internalZoneId);
        MAP_SUN.put("Location [Street, City, Division, Country]", crtLocationDetail);
        MAP_SUN.put("Current Timestamp", nowZ.format(APPLIED_FORMATER));
        final SunTimesClass.SunRiseAndSunSet todaySun = SunTimesClass.calculate(dblLatitude, dblLongitude, nowZ.toInstant());
        final ZonedDateTime sunrise = todaySun.sunrise()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        if (sunrise != null) {
            MAP_SUN.put("Today Sunrise", sunrise.format(APPLIED_FORMATER));
        } else {
            MAP_SUN.put("Today Sunrise", "Sun does not rise on this date at this location");
        }
        final ZonedDateTime sunset = todaySun.sunset()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        if (sunset != null) {
            MAP_SUN.put("Today Sunset", sunset.format(APPLIED_FORMATER));
        } else {
            MAP_SUN.put("Today Sunset", "Sun does not set on this date at this location");
        }
        if ((sunrise != null)
            && (sunset != null)) {
            MAP_SUN.put("Today Light duration",
                    TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunrise, sunset));
            enhanceSunStatistics(nowZ, sunrise, sunset);
        }
        return MAP_SUN;
    }

    /**
     * Enhances sun position details
     * @param nowZ now with Time Zone
     * @param sunrise sun rise time
     * @param sunset sun set time
     */
    private static void enhanceSunStatistics(
            final @NonNull ZonedDateTime nowZ,
            final @NonNull ZonedDateTime sunrise,
            final @NonNull ZonedDateTime sunset) {
        final ZonedDateTime yesterdayZ = nowZ.minusDays(1);
        final SunTimesClass.SunRiseAndSunSet yesterdaySun = SunTimesClass.calculate(
                dblLatitude, dblLongitude, yesterdayZ.toInstant()
        );
        final ZonedDateTime sunrisePrior = yesterdaySun.sunrise()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put("Yesterday Sunrise", sunrisePrior.format(APPLIED_FORMATER));
        final ZonedDateTime sunsetPrior = yesterdaySun.sunset()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put("Yesterday Sunset", sunsetPrior.format(APPLIED_FORMATER));
        MAP_SUN.put("Yesterday Light duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunrisePrior, sunsetPrior));
        MAP_SUN.put("Yesterday Night Duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunsetPrior, sunrise));
        final ZonedDateTime tomorrowZ = nowZ.plusDays(1);
        final SunTimesClass.SunRiseAndSunSet tomorrowSun = SunTimesClass.calculate(
                dblLatitude, dblLongitude, tomorrowZ.toInstant()
        );
        final ZonedDateTime sunriseNext = tomorrowSun.sunrise()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put("Tomorrow Sunrise", sunriseNext.format(APPLIED_FORMATER));
        final ZonedDateTime sunsetNext = tomorrowSun.sunset()
                .map(i -> i.atZone(internalZoneId))
                .orElseThrow();
        MAP_SUN.put("Tomorrow Sunset", sunsetNext.format(APPLIED_FORMATER));
        MAP_SUN.put("Tomorrow Light duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunriseNext, sunsetNext));
        MAP_SUN.put("Today Night duration",
                TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunset, sunriseNext));
        String strSunSituation = "DOWN";
        String strCrtSituation = "After sunset";
        if (nowZ.isBefore(sunrise)) {
            strCrtSituation = "Before sunrise";
            MAP_SUN.put(PRIOR_EVENT, "Sunset since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunsetPrior, nowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunrise in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(nowZ, sunrise));
        } else if (nowZ.isBefore(sunset)) {
            strSunSituation = "UP";
            strCrtSituation = "In between sunrise and sunset";
            MAP_SUN.put(PRIOR_EVENT, "Sunrise since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunrise, nowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunset in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(nowZ, sunset));
        } else {
            MAP_SUN.put(PRIOR_EVENT, "Sunset since "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(sunset, nowZ));
            MAP_SUN.put(NEXT_EVENT, "Sunrise in "
                    + TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(nowZ, sunriseNext));
        }
        MAP_SUN.put("Sun situation", strSunSituation);
        MAP_SUN.put("Current Situation", strCrtSituation);
    }

    /**
     * Setter for dblLatitude
     * @param inLatitude input Latitude
     */
    public static void setLatitude(final double inLatitude) {
        if (!Double.isFinite(inLatitude)
                || inLatitude < -90.0
                || inLatitude > 90.0) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90 degrees");
        }
        dblLatitude = inLatitude;
        MAP_SUN.put("Latitude", String.valueOf(dblLatitude));
    }

    /**
     * Setter for dblLongitude
     * @param inLongitude input Longitude
     */
    public static void setLongitude(final double inLongitude) {
        if (!Double.isFinite(inLongitude)
                || inLongitude < -180.0
                || inLongitude > 180.0) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180 degrees");
        }
        dblLongitude = inLongitude;
        MAP_SUN.put("Longitude", String.valueOf(dblLongitude));
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

    private SunClass() {
        // intentionally blank
    }

}
