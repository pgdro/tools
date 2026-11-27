/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.time;

import io.github.pgdro.tools.core.StringingClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import io.github.pgdro.tools.core.time.TimingClass.AgingSubClass;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.Locale;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;


/**
 * Time Zones and associated coordinates handler
 */
public final class TimeLocalizationClass {
    /** Input time zone variable */
    private static String inputTimeZone;
    /** Output time zone variable */
    private static String outputTimeZone;
    /** Variable used as reference ZoneDateTime for Aging calculation */
    private static ZonedDateTime refAgingTimeStamp;

    static {
        loadTimeZones();
    }

    /**
     * File time related logic
     */
    public static final class FileSubSubClass {

        /**
         * Constructor
         */
        private FileSubSubClass() {
            // intentionally blank
        }

        /**
         * File Last Modified Aging
         * @param file given file
         * @return String with file Last Modified aging
         */
        public static String getFileLastModifiedAging(@NonNull final Path file) {
            final ZonedDateTime zStartTimeStamp = getFileLastModifiedZonedDateTime(file);
            String strReturn = "";
            if (zStartTimeStamp != null) {
                strReturn = AgingSubClass.computeAgingIntoHumanReadableWords(refAgingTimeStamp, zStartTimeStamp);
            }
            return strReturn;
        }

        /**
         * get file last modified date time as human-readable format
         * @param file given file
         * @return String
         */
        @Nullable
        public static String getFileLastModifiedTimeAsHumanReadableFormat(@NonNull final Path file) {
            return getFileLastModifiedTimeAsHumanReadableFormat(file, TimingClass.DATE_TIME_MS_ABRV);
        }

        /**
         * get file last modified date time as human-readable format
         * @param file given file
         * @param outFormat format pattern
         * @return String
         */
        @Nullable
        public static String getFileLastModifiedTimeAsHumanReadableFormat(
                @NonNull final Path file,
                @NonNull final String outFormat) {
            final ZonedDateTime dateTime = getFileLastModifiedZonedDateTime(file);
            final DateTimeFormatter fixedFormatter = DateTimeFormatter.ofPattern(outFormat, Locale.US);
            String returnString = null;
            if (dateTime != null) {
                returnString = dateTime.format(fixedFormatter);
            }
            return returnString;
        }

    }

    /**
     * converts a Date from one format to another
     *
     * @param inDate input Date
     * @param inTimeFormat input Time Format
     * @param outTimeFormat output Time Format
     * @return String
     */
    public static String convertDateOrTimestampFormats(
            final String inDate,
            final DateTimeFormatter inTimeFormat,
            final DateTimeFormatter outTimeFormat) {
        String outDate = "";
        try {
            ZonedDateTime zonedDateTime = null;
            if (StringingClass.StringEvaluationSubClass.isStringActuallyTimestampMultiple(inDate)) {
                zonedDateTime = LocalDateTime.parse(inDate, inTimeFormat).atZone(ZoneId.of(inputTimeZone));
            } else if (StringingClass.StringEvaluationSubClass.isStringActuallyDate(inDate)) {
                zonedDateTime = LocalDate.parse(inDate, inTimeFormat).atStartOfDay(ZoneId.of(inputTimeZone));
            }
            if (zonedDateTime == null) {
                final String strFeedback = String.format("Error parsing %s with following details: %s",
                        inDate,
                        "ZonedDateTime is null");
                LogExposureClass.LOGGER.error(strFeedback);
            } else {
                ZonedDateTime outTime = zonedDateTime;
                if (!inputTimeZone.equalsIgnoreCase(outputTimeZone)) {
                    outTime = zonedDateTime.withZoneSameInstant(ZoneId.of(outputTimeZone));
                }
                outDate = outTime.format(outTimeFormat);
            }
        } catch (DateTimeParseException e) {
            final String strFeedback = String.format("Error parsing %s with following details: %s",
                    inDate,
                    Arrays.toString(e.getStackTrace()));
            LogExposureClass.LOGGER.error(strFeedback);
        } catch (UnsupportedTemporalTypeException e) {
            final String strFeedback = String.format("Unsupported Temporal time for %s with following details: %s",
                    inDate,
                    Arrays.toString(e.getStackTrace()));
            LogExposureClass.LOGGER.error(strFeedback);
        }
        return outDate;
    }

    /**
     * Convert time-stamp
     * @param strTimeStamp input Time-stamp
     * @param inputFormat input Time Format
     * @param outputFormat output Time Format as String
     * @return String converted time-stamp and formatted
     */
    public static String convertTimestampFriendly(
            final String strTimeStamp,
            final String inputFormat,
            final String outputFormat) {
        final ZonedDateTime inTimeStamp = convertStringIntoZonedDateTime(strTimeStamp, inputFormat);
        ZonedDateTime outTime = inTimeStamp;
        if (!inputTimeZone.equalsIgnoreCase(outputTimeZone)) {
            outTime = inTimeStamp.withZoneSameInstant(ZoneId.of(outputTimeZone));
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat, Locale.US);
        return outTime.format(formatter);
    }

    /**
     * Convert String into Zoned Date Time
     * @param strTimeStamp input time-stamp
     * @param inputFormat input Time Format as String
     * @return ZonedDateTime
     */
    public static ZonedDateTime convertStringIntoZonedDateTime(
            final String strTimeStamp,
            final String inputFormat) {
        final LocalDateTime localTime = LocalDateTime.parse(strTimeStamp,
                DateTimeFormatter.ofPattern(inputFormat, Locale.US));
        return localTime.atZone(ZoneId.of(inputTimeZone));
    }

    /**
     * Convert time-stamp
     * @param inTimeStamp input Time-stamp
     * @param outputFormat output Time Format as String
     * @return String converted time-stamp and formatted
     */
    public static String convertZonedTimestampFriendly(
            final ZonedDateTime inTimeStamp,
            final String outputFormat) {
        ZonedDateTime outTime = inTimeStamp;
        if (!inputTimeZone.equalsIgnoreCase(outputTimeZone)) {
            outTime = inTimeStamp.withZoneSameInstant(ZoneId.of(outputTimeZone));
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat, Locale.US);
        return outTime.format(formatter);
    }

    /**
     * format date
     * @param strDate input date
     * @param inputFormat input Time Format
     * @param outputFormat output Time Format as String
     * @return String formatted date
     */
    public static String formatDateFriendly(
            final String strDate,
            final String inputFormat,
            final String outputFormat) {
        final LocalDate outDate = LocalDate.parse(strDate, DateTimeFormatter.ofPattern(inputFormat, Locale.US));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputFormat, Locale.US);
        return outDate.format(formatter);
    }

    /**
     * get Last Modified date time
     * @param file given file
     * @return ZonedDateTime
     */
    @Nullable
    public static ZonedDateTime getFileLastModifiedZonedDateTime(@NonNull final Path file) {
        ZonedDateTime zDateTime = null;
        try {
            final Instant modifTime = Files.getLastModifiedTime(file).toInstant();
            zDateTime = ZonedDateTime.ofInstant(modifTime, ZoneId.of(outputTimeZone));
        } catch (IOException ei) {
            final String strFeedback = String.format(
                    "Error encountered when attempting to get %s file(s) from %s folder",
                    file.getParent(),
                    file.getFileName());
            LogExposureClass.exposeInputOutputException(strFeedback, Arrays.toString(ei.getStackTrace()));
        }
        return zDateTime;
    }

    /**
     * loading Input/Output time zones
     */
    private static void loadTimeZones() {
        inputTimeZone = System.getProperty("user.timezone", ZoneId.systemDefault().toString());
        outputTimeZone = System.getProperty("user.timezone", ZoneId.systemDefault().toString());
    }

    /**
     * Setter for inputTimeZone
     * @param strTimeZone desired time zone for input
     */
    public static void setInputTimeZone(final String strTimeZone) {
        inputTimeZone = strTimeZone;
    }

    /**
     * Setter for outputTimeZone
     * @param strTimeZone desired time zone for output
     */
    public static void setOutputTimeZone(final String strTimeZone) {
        outputTimeZone = strTimeZone;
    }

    /**
     * Setter for zAgingRefTimeStamp
     * @param inRefTimeStamp input Reference Time-stamp
     */
    public static void setReferenceTimeStampValueForAgingCalculation(final ZonedDateTime inRefTimeStamp) {
        refAgingTimeStamp = inRefTimeStamp;
    }

    /**
     * Constructor
     */
    private TimeLocalizationClass() {
        // intentionally blank
    }

}
