/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.dynamic.web;

import io.github.pgdro.tools.core.BasicStructuresClass;
import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.time.TimingClass;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Conversions logic
 */
public final class ConversionsClass {
    /** variable for dynamic commands */
    private final Map<String, Function<String, String>> converters = Map.ofEntries(
            Map.entry("decimal", this::convertDecimalWithThousandDecimalSeparator),
            Map.entry("integer", this::convertInteger),
            Map.entry("long", this::convertLong),
            Map.entry(ConfigurationClass.STR_TS_MSEC, this::convertTimestampWithMilliseconds),
            Map.entry(ConfigurationClass.STR_TIMESTAMP, this::convertTimestamp),
            Map.entry(ConfigurationClass.STR_DATE_HRMN, this::convertDateWithHourAndMinute),
            Map.entry(ConfigurationClass.STR_JUST_DATE, this::convertDate),
            Map.entry("byteSize", this::exposeRaw),
            Map.entry("fullAging", this::exposeRaw)
    );

    /**
     * constructor
     */
    public ConversionsClass() {
        super();
    }

    /**
     * Converting Decimal from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertDecimalWithThousandDecimalSeparator(final String inputString) {
        return BasicStructuresClass
                .StringTransformationSubClass
                .formatStringWithDecimalContentWithThousandDecimalSeparator(inputString);
    }

    /**
     * Converting Integer from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertInteger(final String inputString) {
        return String.format(
                Locale.US,
                "%,d",
                BasicStructuresClass.convertStringIntoInteger(inputString));
    }

    /**
     * Converting Long from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertLong(final String inputString) {
        return String.format(
                Locale.US,
                "%,d",
                BasicStructuresClass.convertStringIntoLong(inputString));
    }

    /**
     * Converting Date from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertDate(final String inputString) {
	return TimingClass.LocalizationSubClass.formatDateFriendly(
                inputString,
                TimingClass.ISO_DATE,
                TimingClass.ISO_DATE_ABRV);
    }

    /**
     * Converting Date with Hour and Minute from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertDateWithHourAndMinute(final String inputString) {
        return TimingClass.LocalizationSubClass.convertTimestampFriendly(
                inputString,
                TimingClass.DATE_HRMN,
                TimingClass.DATE_HRMN_ABRV);
    }

    /**
     * Converting Time-stamp from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertTimestamp(final String inputString) {
        return TimingClass.LocalizationSubClass.convertTimestampFriendly(
                inputString,
                TimingClass.DATE_TIME,
                TimingClass.DATE_TIME_ABRV);
    }

    /**
     * Converting Time-stamp with Milliseconds from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String convertTimestampWithMilliseconds(final String inputString) {
        return TimingClass.LocalizationSubClass.convertTimestampFriendly(
                inputString,
                TimingClass.DATE_TIME_MS,
                TimingClass.DATE_TIME_MS_ABRV);
    }

    /**
     * Dispatcher for dynamic commands
     * @param command given command ID
     * @param inputString relevant String to convert
     * @return Result
     */
    public String dispatch(final String command, final String inputString) {
        final Function<String, String> converter = converters.get(command);
        if (converter == null) {
            throw new IllegalArgumentException("Unknown command: " + command);
        }
        return converter.apply(inputString);
    }

    /**
     * Exposing Raw Value from String
     * @param inputString relevant String to convert
     * @return converted String
     */
    private String exposeRaw(final String inputString) {
        return inputString;
    }

}
