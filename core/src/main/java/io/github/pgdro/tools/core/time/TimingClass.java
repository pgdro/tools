/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.time;

import io.github.pgdro.tools.core.BasicStructuresClass;
import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.RegularExpressionsClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jspecify.annotations.NonNull;

/**
 * Time methods
 */
public final class TimingClass {
    /** constant for date with hour and minute */
    public static final String DATE_HRMN = "yyyy-MM-dd HH:mm";
    /** constant for abbreviated date with hour and minute */
    public static final String DATE_HRMN_ABRV = "EEE, dd MMM yyyy HH:mm";
    /** constant for long date with hour and minute */
    public static final String DATE_HRMN_LONG = "EEEE, dd MMM yyyyy HH:mm";
    /** constant for time-stamp SQL-style with seconds */
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    /** constant for abbreviated time-stamp with seconds */
    public static final String DATE_TIME_ABRV = "EEE, dd MMM yyyy HH:mm:ss";
    /** constant for long time-stamp with seconds */
    public static final String DATE_TIME_LONG = "EEEE, dd MMMM yyyy HH:mm:ss";
    /** constant for time-stamp SQL-style with milliseconds */
    public static final String DATE_TIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    /** constant for abbreviated time-stamp with milliseconds */
    public static final String DATE_TIME_MS_ABRV = "EEE, dd MMM yyyy HH:mm:ss.SSS";
    /** constant for long time-stamp with milliseconds */
    public static final String DATE_TIME_MS_LONG = "EEEE, dd MMMM yyyy HH:mm:ss.SSS";
    /** constant for long date */
    public static final String ISO_DATE = "yyyy-MM-dd";
    /** constant for abbreviated date */
    public static final String ISO_DATE_ABRV = "EEE, dd MMM yyyy";
    /** constant for long date */
    public static final String ISO_DATE_LONG = "EEEE, dd MMMM yyyy";
    /** constant for time SQL-style with milliseconds */
    public static final String TIME_MS = "HH:mm:ss.SSS";
    /** constant for time number style with milliseconds */
    public static final String TIME_NO = "HHmmss.SSS";
    /** String constant */
    public static final int DAY_MILLISECONDS = 24 * 60 * 60 * 1000;

    /** Record for Aging Components */
    /* default */ public record AgingInfoRecord(
        boolean isNegative,
        Integer intYears,
        Integer intMonths,
        Integer intWeeks,
        Integer intDays,
        Integer intHours,
        Integer intMinutes,
        Integer intSeconds,
        Integer intMilliseconds) {}

    /**
     * Current local DateTime w. TZ as String
     * @return String
     */
    public static String getCurrentDateTimeLocal() {
        return DateTimeFormatter.ofPattern(DATE_TIME_MS_ABRV, Locale.US)
                .format(getCurrentZonedDateTime());
    }

    /**
     * Current local DateTime w. TZ as String
     * @return String
     */
    public static String getCurrentDateTimeLocal(final String inTimeZone) {
        return DateTimeFormatter.ofPattern(DATE_TIME_MS_ABRV, Locale.US)
                .format(ZonedDateTime.now(ZoneId.of(inTimeZone)));
    }

    /**
     * Current DateTime
     * @return String
     */
    public static String getCurrentDateTimeLocalRaw() {
        return DateTimeFormatter.ofPattern(DATE_TIME_MS, Locale.US)
                .format(getCurrentZonedDateTime());
    }

    /**
     * Current DateTime UTC
     * @return String
     */
    public static String getCurrentDateTimeUniveralTimeCoordination() {
        return DateTimeFormatter.ofPattern(DATE_TIME_MS, Locale.US)
                .format(getCurrentZonedDateTimeUniveralTimeCoordination());
    }

    /**
     * Current local DateTime w. TZ as String
     * @return String
     */
    public static ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now(ZoneId.systemDefault());
    }

    /**
     * Current local DateTime w. TZ as String
     * @return String
     */
    public static ZonedDateTime getCurrentZonedDateTimeUniveralTimeCoordination() {
        return ZonedDateTime.now(Clock.systemUTC());
    }

    /**
     * Epoch Milliseconds as String
     * @param cutoff input reference TS as Long value
     * @return String
     */
    public static String getEpochMilliseconds(final long cutoff) {
        return Instant.ofEpochMilli(cutoff).toString().replaceAll("[TZ]", " ").trim();
    }

    /**
     * Converts a string with ISO 8601 date as input into String w. year
     * and week string + 2 digits week #
     * @param strDateIso8601 date as yyyy-MM-dd (a.k.a. ISO 8601 format type)
     * @return String as year, week string + 2 digits week #
     */
    @NonNull
    public static String getIsoYearWeek(@NonNull final String strDateIso8601) {
        final LocalDate inLocalDate = LocalDate.parse(strDateIso8601);
        return inLocalDate.get(WeekFields.ISO.weekBasedYear()) + "wk"
                + String.format("%02d", inLocalDate.get(WeekFields.ISO.weekOfWeekBasedYear()));
    }

    /**
     * build a LocalDateTime from Strings
     * @param strDateIso8601 input Date
     * @param timeContinuous input Time
     * @return LocalDateTime
     */
    public static LocalDateTime getLocalDateTimeFromStrings(
            final String strDateIso8601,
            final String timeContinuous) {
        return LocalDateTime.of(
                BasicStructuresClass.convertStringIntoInteger(strDateIso8601.substring(0, 4)),
                BasicStructuresClass.convertStringIntoInteger(strDateIso8601.substring(5, 7)),
                BasicStructuresClass.convertStringIntoInteger(strDateIso8601.substring(8, 10)),
                BasicStructuresClass.convertStringIntoInteger(timeContinuous.substring(0, 2)),
                BasicStructuresClass.convertStringIntoInteger(timeContinuous.substring(2, 4)),
                BasicStructuresClass.convertStringIntoInteger(timeContinuous.substring(4, 6)));
    }

    /**
     * Returns X days ago with milliseconds ago limit
     * @param intDaysLimit number of days in the past
     * @return milliseconds in the past
     */
    public static long getDaysAgoWithMillisecondsPrecision(final long intDaysLimit) {
        final Instant refTimeStamp = Instant.now();
        return getDaysAgoWithMillisecondsPrecision(refTimeStamp, intDaysLimit);
    }

    /**
     * Returns X days ago with milliseconds ago limit
     * @param intDaysLimit number of days in the past
     * @return milliseconds in the past
     */
    public static long getDaysAgoWithMillisecondsPrecision(
            final Instant refTimestamp,
            final long intDaysLimit) {
        return refTimestamp.minusMillis(intDaysLimit * DAY_MILLISECONDS).toEpochMilli();
    }

    /**
     * Converts a string with ISO 8601 date as input into String as yyyy-MM (MonthName)
     * @param strDateIso8601 date as yyyy-MM-dd (a.k.a. ISO 8601 format type)
     * @return String as yyyy-MM (MonthName)
     */
    @NonNull
    public static String getYearMonthWithFullName(@NonNull final String strDateIso8601) {
        final LocalDate inLocalDate = LocalDate.parse(strDateIso8601);
        return strDateIso8601.substring(0, 7)
                + " ("
                + inLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + ")";
    }

    /**
     * Time Zones and associated coordinates handler
     */
    public static final class AgingSubClass {

        /**
         * Constructor
         */
        private AgingSubClass() {
            // intentionally blank
        }

        /**
         * append w. prefix if value is not 0
         * @param parts input list to alter
         * @param value input value to evaluate
         * @param strPrefix prefix of the value
         */
        private static void appendValueWithPrefixIfNotZero(
                final List<String> parts,
                final long value,
                final String strPrefix,
                final String strMeaning) {
            appendValueWithPrefixAndSuffixIfNotZero(parts, value, strPrefix, "", strMeaning);
        }

        /**
         * append w. prefix & suffix if value is not 0
         * @param parts input list to alter
         * @param value input value to evaluate
         * @param strPrefix prefix of the value
         */
        private static void appendValueWithPrefixAndSuffixIfNotZero(
                final List<String> parts,
                final long value,
                final String strPrefix,
                final String strSuffix,
                final String strMeaning) {
            String zeroString = "00";
            String nonZeroFormat = "%02d";
            if (ConfigurationClass.STR_MILLISECONDS.equalsIgnoreCase(strMeaning)) {
                zeroString = "000";
                nonZeroFormat = "%03d";
            }
            if (value == 0) {
                parts.add(strPrefix + zeroString);
            } else {
                parts.add(strPrefix + String.format(nonZeroFormat, value) + strSuffix);
            }
        }

        /**
         * append w. units if value is not 0
         * @param parts input list to alter
         * @param value input value to evaluate
         * @param unit unit of the value
         */
        private static void appendValueWithUnitIfNotZero(
                final List<String> parts,
                final long value,
                final String unit) {
            if (value != 0) {
                parts.add(value + " " + unit + (Math.abs(value) == 1 ? "" : "s"));
            }
        }

        /**
         * append w. units if value is not 0
         * @param parts input list to alter
         * @param value input value to evaluate
         * @param unit unit of the value
         */
        private static void appendValueWithSimplifiedUnitIfNotZero(
                final List<String> parts,
                final long value,
                final String unit) {
            if (value != 0) {
                parts.add(value + unit);
            }
        }

        /**
         * composing Aging as words from Integer components
         * @param inAgeComponents age components as List of Integer values
         * @param strZeroValue return value if all parts are empty
         * @return String with words for aging
         */
        public static String composeAgingClockFromListOfIntegerComponents(
                final AgingInfoRecord inAgeComponents,
                final String strZeroValue) {
            final List<String> parts = new ArrayList<>();
            appendValueWithSimplifiedUnitIfNotZero(parts, inAgeComponents.intYears, "y");
            appendValueWithSimplifiedUnitIfNotZero(parts, inAgeComponents.intMonths, "M");
            appendValueWithSimplifiedUnitIfNotZero(parts, inAgeComponents.intWeeks, "w");
            appendValueWithSimplifiedUnitIfNotZero(parts, inAgeComponents.intDays, "d");
            appendValueWithPrefixIfNotZero(parts, inAgeComponents.intHours, "", "Hours");
            appendValueWithPrefixIfNotZero(parts, inAgeComponents.intMinutes, ":", "Minutes");
            appendValueWithPrefixIfNotZero(parts, inAgeComponents.intSeconds, ":", "Seconds");
            appendValueWithPrefixIfNotZero(parts, inAgeComponents.intMilliseconds, ".", "Milliseconds");
            String strReturn = strZeroValue;
            if (!parts.isEmpty()) {
                strReturn = (inAgeComponents.isNegative ? "-" : "") + String.join("", parts);
            }
            return strReturn;
        }

        /**
         * composing Aging as words from Integer components
         * @param inAgeComponents age components as List of Integer values
         * @param strZeroValue return value if all parts are empty
         * @return String with words for aging
         */
        public static String composeAgingInWordsFromListOfIntegerComponents(
                final AgingInfoRecord inAgeComponents,
                final String strZeroValue) {
            return composeAgingInWordsFromListOfIntegerComponents(inAgeComponents, strZeroValue, true);
        }

        /**
         * composing Aging as words/abbreviations from Integer components
         * @param inAgeComponents age components as List of Integer values
         * @param strZeroValue return value if all parts are empty
         * @return String with words for aging
         */
        public static String composeAgingInWordsFromListOfIntegerComponents(
                final AgingInfoRecord inAgeComponents,
                final String strZeroValue,
                final boolean bolFullWords) {
            List<String> allUnits = List.of("y", "M", "w", "d", "h", "m", "s", "ms");
            if (bolFullWords) {
                allUnits = List.of("year", "month", "week", "day", "hour", "minute", "second", "millisecond");
            }
            final List<String> parts = new ArrayList<>();
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intYears, allUnits.getFirst());
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intMonths, allUnits.get(1));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intWeeks, allUnits.get(2));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intDays, allUnits.get(3));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intHours, allUnits.get(4));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intMinutes, allUnits.get(5));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intSeconds, allUnits.get(6));
            appendValueWithUnitIfNotZero(parts, inAgeComponents.intMilliseconds, allUnits.get(7));
            String strReturn = strZeroValue;
            if (!parts.isEmpty()) {
                strReturn = (inAgeComponents.isNegative ? "-" : "") + String.join(" ", parts);
            }
            return strReturn;
        }

        /**
         * Aging logic
         * @param startTimestamp reference ZonedDatetime
         * @param finishTimestamp ending ZonedDateTime
         * @return Aging
         */
        public static String computeAgingIntoHumanReadableWords(
                final ZonedDateTime startTimestamp,
                final ZonedDateTime finishTimestamp) {
            final AgingInfoRecord ageComponents = computeAgingInfoRecord(startTimestamp, finishTimestamp);
            return composeAgingInWordsFromListOfIntegerComponents(ageComponents, "INSTANT (less than 1 millisecond)");
        }

        /**
         * Aging logic
         * @param startTimestamp reference ZonedDatetime
         * @param finishTimestamp ending ZonedDateTime
         * @return Aging
         */
        public static String computeAgingIntoTimeClock(
                final ZonedDateTime startTimestamp,
                final ZonedDateTime finishTimestamp) {
            final AgingInfoRecord ageComponents = computeAgingInfoRecord(startTimestamp, finishTimestamp);
            return composeAgingClockFromListOfIntegerComponents(ageComponents,
                    "INSTANT (less than 1 millisecond)");
        }

        /**
         * Aging Info Record logic
         * @param startTimestamp reference ZonedDatetime
         * @param finishTimestamp ending ZonedDateTime
         * @return Aging components
         */
        private static AgingInfoRecord computeAgingInfoRecord(
                final ZonedDateTime startTimestamp,
                final ZonedDateTime finishTimestamp) {
            final boolean isNegative = startTimestamp.isAfter(finishTimestamp);
            final Period period      = Period.between(startTimestamp.toLocalDate(), finishTimestamp.toLocalDate());
            final Duration duration  = Duration.between(startTimestamp.toInstant(), finishTimestamp.toInstant());
            final String strFeedback = String.format("Period is %s and Duration is %s", period, duration);
            LogExposureClass.LOGGER.debug(strFeedback);
            final int years          = Math.abs(period.getYears());
            final int months         = Math.abs(period.getMonths());
            int days                 = Math.abs(period.getDays());
            final DateTimeFormatter formatterNo = DateTimeFormatter.ofPattern(TIME_NO, Locale.US);
            final BigDecimal startTimeNumber    = new BigDecimal(startTimestamp.format(formatterNo));
            final BigDecimal finishTimeNumber   = new BigDecimal(finishTimestamp.format(formatterNo));
            if ("P1D".equalsIgnoreCase(period.toString())
                    && startTimeNumber.compareTo(finishTimeNumber) > 0) {
                final String strFeedback3 = String.format("Start Time as Number is %s and Finish Time as Number is %s"
                        + " within consecutive days, hence a correction of 1 day will take place",
                        startTimeNumber,
                        finishTimeNumber);
                LogExposureClass.LOGGER.debug(strFeedback3);
                days = days - 1;
            }
            final int intWeeks             = Math.floorDivExact(days, 7);
            if (intWeeks != 0) {
                days = days - 7 * intWeeks;
            }
            // duration components
            final int intHours             = Math.abs(duration.toHoursPart());
            final int intMinutes           = Math.abs(duration.toMinutesPart());
            final String strDuration       = duration.toString().replace("PT", "");
            final List<String> strSeconds  = RegularExpressionsClass.extractMatches(strDuration,
                    "([-+]?[0-9]{1,2}(|[.,][0-9]{0,9}))S");
            int intSeconds = 0;
            int intMilli   = 0;
            if (!strSeconds.isEmpty()) {
                final String firstSecond   = strSeconds.getFirst();
                if (firstSecond.contains(".")) {
                    final String[] firstSecondParts = firstSecond.split("[.,]");
                    intSeconds                      = Math.abs(Integer.parseInt(firstSecondParts[0]));
                    final String strMilliseconds    = firstSecondParts[1].replace("S", "");
                    final int roundingFactor        = strMilliseconds.length() - 3;
                    intMilli                        = new BigDecimal(strMilliseconds)
                            .scaleByPowerOfTen(-roundingFactor).intValue();
                } else {
                    intSeconds                      = Math.abs(Integer.parseInt(
                            firstSecond.replace("S", "")));
                }
            }
            return new AgingInfoRecord(isNegative,
                    years, months, intWeeks, days,
                    intHours, intMinutes, intSeconds, intMilli);
        }

    }

    /**
     * Time Zones and associated coordinates handler
     */
    public static final class LogSubClass {

        /**
         * Constructor
         */
        private LogSubClass() {
            // intentionally blank
        }

        /**
         * log a duration
         * @param startTimeStamp times-tamp value seen at start
         * @param finishTimeStamp times-tamp value seen at stop
         * @param strPartial prefix for feedback
         * @return String
         */
        public static String logDuration(
                final LocalDateTime startTimeStamp,
                final LocalDateTime finishTimeStamp,
                final String strPartial) {
            final ZonedDateTime zStartTimeStamp = ZonedDateTime.of(startTimeStamp, ZoneId.systemDefault());
            final ZonedDateTime zStopTimeStamp = ZonedDateTime.of(finishTimeStamp, ZoneId.systemDefault());
            return logDuration(zStartTimeStamp, zStopTimeStamp, strPartial);
        }

        /**
         * log a duration
         * @param startTimeStamp times-tamp value seen at start
         * @param finishTimeStamp times-tamp value seen at stop
         * @param strPartial prefix for feedback
         * @return String
         */
        public static String logDuration(
                final ZonedDateTime startTimeStamp,
                final ZonedDateTime finishTimeStamp,
                final String strPartial) {
            final Duration objDuration = Duration.between(startTimeStamp, finishTimeStamp);
            return String.format("%s within a duration of %s (which is %s | %s)",
                    strPartial,
                    objDuration.toString(),
                    AgingSubClass.computeAgingIntoHumanReadableWords(startTimeStamp, finishTimeStamp),
                    AgingSubClass.computeAgingIntoTimeClock(startTimeStamp, finishTimeStamp));
        }

    }

    /**
     * Constructor
     */
    private TimingClass() {
        // intentionally blank
    }
}
