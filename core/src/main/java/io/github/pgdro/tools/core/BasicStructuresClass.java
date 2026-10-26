/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core;

import io.github.pgdro.tools.core.file.FileOperationsClass;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.SequencedMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Handling basic structures: numbers, lists, maps, strings
 */
public final class BasicStructuresClass {
    /** null constant */
    private static final String STR_NULL_LOWER = "null";

    /**
     * Safely computes percentage
     * @param numerator top number
     * @param denominator dividing number
     * @return float value
     */
    public static BigDecimal computePercentageSafely(final long numerator, final long denominator) {
        BigDecimal percentageExact = new BigDecimal(numerator);
        if (denominator == 0) {
            final String strFeedback = String.format("Denominator is %s"
                    + " hence Percentage calculation with Numerator %s is not possible"
                    + " and will return same numerator... %s",
                    denominator,
                    numerator,
                    StackWalker.getInstance()
                            .walk(frames -> frames.findFirst()
                            .map(frame -> frame.getClassName() + "." + frame.getMethodName())
                            .orElse(LogExposureClass.STR_I18N_UNKN)));
            LogExposureClass.LOGGER.error(strFeedback);
        } else {
            percentageExact = percentageExact
                    .divide(new BigDecimal(denominator), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100L));
        }
        return percentageExact.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convert String to BigDecimal
     * @param strNumber string to evaluate
     * @return BigDecimal
     */
    public static @Nullable BigDecimal convertStringIntoBigDecimal(final String strNumber) {
        BigDecimal noToReturn = null;
        if (StringingClass.StringEvaluationSubClass.isStringActuallyNumeric(strNumber)) {
            noToReturn = new BigDecimal(strNumber).stripTrailingZeros();
        }
        return noToReturn;
    }

    /**
     * Convert String to Double
     * @param strNumber string to evaluate
     * @return double
     */
    public static double convertStringIntoDouble(final String strNumber) {
        double noToReturn = 0.0;
        try {
            noToReturn = Double.parseDouble(strNumber);
        } catch (NumberFormatException noFormatException) {
            final String strFeedback = String.format("Could not convert value %s into Double... %s",
                    strNumber,
                    Arrays.toString(noFormatException.getStackTrace()));
            LogExposureClass.LOGGER.error(strFeedback);
        }
        return noToReturn;
    }

    /**
     * Convert String to Integer
     * @param strNumber string to evaluate
     * @return integer
     */
    public static int convertStringIntoInteger(final String strNumber) {
        int noToReturn = 0;
        final boolean isNumeric = StringingClass.StringEvaluationSubClass.isStringActuallyInteger(strNumber);
        if (isNumeric) {
            try {
                noToReturn = Integer.parseInt(strNumber);
            } catch (NumberFormatException noFormatException) {
                final String strFeedback = String.format(ConfigurationClass.CONVERT_INT_NA, strNumber,
                        Arrays.toString(noFormatException.getStackTrace()));
                LogExposureClass.LOGGER.error(strFeedback);
            }
        }
        return noToReturn;
    }

    /**
     * Convert String to Integer
     * @param strNumber string to evaluate
     * @return long
     */
    public static long convertStringIntoLong(final String strNumber) {
        long noToReturn = 0;
        final boolean isNumeric = StringingClass.StringEvaluationSubClass.isStringActuallyLong(strNumber);
        if (isNumeric) {
            try {
                noToReturn = Long.parseLong(strNumber);
            } catch (NumberFormatException noFormatException) {
                final String strFeedback = String.format(ConfigurationClass.CONVERT_INT_NA, strNumber,
                        Arrays.toString(noFormatException.getStackTrace()));
                LogExposureClass.LOGGER.error(strFeedback);
            }
        }
        return noToReturn;
    }

    /**
     * detects if current execution is from JAR or not
     * @return boolean
     */
    public static boolean isRunningFromJar() {
        // Get the URL of the current class's byte-code
        final URL classUrl = BasicStructuresClass.class.getResource("BasicStructuresClass.class");
        if (classUrl == null) {
            throw new IllegalStateException("Class resource not found");
        }
        // Check if the protocol is "jar" (JAR execution) or "file" (IDE execution)
        final String protocol = classUrl.getProtocol();
        return "jar".equals(protocol);
    }

    /**
     * List and Maps management
     */
    public static final class ListAndMapSubClass {

        /**
         * Map into List
         * @param strCategory value for Category Property
         * @param inMap values as Map
         * @return List of Properties
         */
        public static @NonNull List<Properties> convertMapOfStringsIntoListOfProperties(
                @NonNull final String strCategory,
                @NonNull final Map<String, Object> inMap) {
            final List<Properties> resultReleases = new ArrayList<>();
            inMap.forEach((strKey, strValue) -> {
                final Properties mProperties = new Properties();
                mProperties.put(ConfigurationClass.STR_CATEGORY, strCategory);
                mProperties.put("Element", strKey);
                mProperties.put("Value", strValue);
                resultReleases.add(mProperties);
            });
            resultReleases.sort(Comparator.comparing(p -> p.getProperty("Element")));
            return resultReleases;
        }

        /**
         * Map conversions
         * @param inMap values as Map
         * @return Map String with String
         */
        public static Map<String, String> convertMapStringObjectIntoMapStringString(final Map<String, Object> inMap) {
            return inMap
                    .entrySet()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    entry -> entry.getKey(),
                                    entry -> entry.getValue().toString(),
                                    (oldValue, _) -> oldValue,
                                    LinkedHashMap::new
                    ));
        }

        /**
         * Build a pair of Key and Value for JSON
         * @param strKey Key to be used
         * @param objValue Value to be used
         * @return String with a pair of key and value
         */
        public static String getJsonKeyAndValue(final String strKey, final Object objValue) {
            final List<String> unquotedValues = Arrays.asList(STR_NULL_LOWER, "true", "false");
            final boolean needsQuotesAround =
                (objValue instanceof Integer)
                || (objValue instanceof Double)
                || (objValue.toString().startsWith("[") && objValue.toString().endsWith("]"))
                || (objValue.toString().startsWith("{") && objValue.toString().endsWith("}"))
                || StringingClass.StringEvaluationSubClass.isStringActuallyNumeric(objValue.toString())
                || StringingClass.StringEvaluationSubClass.hasMatchingSubstring(objValue.toString(),
                        unquotedValues);
            final String strRaw = needsQuotesAround ? "\"%s\":%s" : "\"%s\":\"%s\"";
            return String.format(strRaw, strKey, objValue);
        }

        /**
         * Cycle inside Map and build a JSON string out of it
         *
         * @param arrayAttrib array with attribute values
         * @return String
         */
        @SuppressWarnings(STR_NULL_LOWER)
        public static String getMapIntoJsonString(final Map<String, Object> arrayAttrib) {
            final StringBuilder strJsonSubString = new StringBuilder(100);
            final Map<String, String> arrayMapString = convertMapStringObjectIntoMapStringString(arrayAttrib);
            final SequencedMap<String, String> sortedMap = sortMap(arrayMapString, true, true);
            sortedMap.forEach((strKey, objValue) -> {
                if (!strJsonSubString.isEmpty()) {
                    strJsonSubString.append(',');
                }
                strJsonSubString.append(getJsonKeyAndValue(strKey, objValue));
            });
            return String.format("{%s}", strJsonSubString);
        }

        /**
         * convert Property into JSON
         * @param inProps input Properties
         * @return JSON string
         */
        public static String getPropertiesIntoJsonString(final Properties inProps) {
            final StringBuilder strJsonSubString = new StringBuilder(100);
            inProps.forEach((objKey, objValue) -> {
                if (!strJsonSubString.isEmpty()) {
                    strJsonSubString.append(',');
                }
                strJsonSubString.append(
                        getJsonKeyAndValue(
                                objKey.toString(),
                                StringingClass.StringCleaningSubClass.stripQuotes(objValue.toString())
                        )
                );
            });
            return String.format("{%s}", strJsonSubString);
        }

        /**
         * Get all words from a list of Strings with merged words glued by _
         * @param valList List of String with words glues by regexSep
         * @param regexSep separators for words detection
         * @return LinkedHashMap of Strings with counted occurrences
         */
        @SuppressWarnings(STR_NULL_LOWER)
        public static SequencedMap<String, Long> getWordCounts(final List<String> valList, final String regexSep) {
            final Map<String, Long> wordCounts = valList.stream()
                    .flatMap(s -> Arrays.stream(s.split(regexSep)))
                    .collect(Collectors.groupingBy(
                            word -> word,
                            Collectors.counting()
                    ));
            return wordCounts.entrySet().stream()
                    .sorted(
                            Comparator.comparing(Map.Entry<String, Long>::getValue).reversed()
                                    .thenComparing(Map.Entry::getKey)
                    )
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, _) -> e1, // merge function (not used here)
                            LinkedHashMap::new // preserve sorted order
                    ));
        }

        /**
         * Merging keys based on list of rules
         * @param inputMap original map
         * @param mergeRules merging rules
         * @return Map of string list
         */
        public static @NonNull Map<String, List<String>> mergeKeys(
                final @NonNull Map<String, List<String>> inputMap,
                final @NonNull Map<List<String>, String> mergeRules) {
            final Map<String, List<String>> result = new ConcurrentHashMap<>();
            // Keep track of all keys that will be merged (to exclude later)
            final Set<String> mergedKeys = new HashSet<>();
            final List<String> mergedValues = new ArrayList<>();
            // Apply each merge rule
            for (final Map.Entry<List<String>, String> entry : mergeRules.entrySet()) {
                final List<String> keysToMerge = entry.getKey();
                for (final String key : keysToMerge) {
                    mergedValues.addAll(inputMap.getOrDefault(key, List.of()));
                }
                if (!mergedValues.isEmpty()) {
                    result.put(entry.getValue(), mergedValues);
                }
                mergedKeys.addAll(keysToMerge);
                mergedValues.clear();
            }
            // Copy all keys that were not merged
            for (final Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
                if (!mergedKeys.contains(entry.getKey())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
            return result;
        }

        /**
         * produce a Sequenced Map from simple Map
         * @param inMap input Map
         * @return SequencedMap with sorted values by Value
         */
        @SuppressWarnings(STR_NULL_LOWER)
        public static <K extends Comparable<? super K>, V extends Comparable<? super V>> SequencedMap<K, V> sortMap(
                @NonNull final Map<K, V> inMap,
                final boolean sortByKey,
                final boolean directionAsending) {
            final Comparator<K> keyComparator = Comparator.nullsLast(Comparator.naturalOrder());
            final Comparator<V> valueComparator = Comparator.nullsLast(Comparator.naturalOrder());
            Comparator<Map.Entry<K, V>> comparator = sortByKey
                ? Map.Entry.comparingByKey(keyComparator)
                : Map.Entry.comparingByValue(valueComparator);
            if (!directionAsending) {
                /* Reverse only the non-null comparison while keeping nulls last. */
                comparator = sortByKey
                    ? Map.Entry.comparingByKey(
                            Comparator.nullsLast(Comparator.reverseOrder())
                        )
                    : Map.Entry.comparingByValue(
                            Comparator.nullsLast(Comparator.reverseOrder())
                        );
            }
            return inMap.entrySet()
                    .stream()
                    .sorted(comparator)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, _) -> oldValue,
                            LinkedHashMap::new
                    ));
        }

        /**
         * produce a Sequenced Map from Properties
         * @param prop Properties
         * @param order order as List of String
         * @return SequencedMap with sorted properties
         */
        @SuppressWarnings(STR_NULL_LOWER)
        public static SequencedMap<Object, Object> sortProperties(final Properties prop, final List<String> order) {
            return prop.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> {
                    final int index = order.indexOf(e.getKey().toString());
                    // If a key isn't in our list, put it at the end
                    return index == -1 ? Integer.MAX_VALUE : index;
                }))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, _) -> oldValue,
                    LinkedHashMap::new // Maintains the sorted insertion order
                ));
        }

        // Private constructor to prevent instantiation
        private ListAndMapSubClass() {
            // intentional empty
        }

    }

    /**
     * Conversion things
     */
    public static final class NumberConversionSubClass {
        /** array with Decimal values */
        private static final long[] ARRAY_DEC_NO = {1L, 1_000L, 1_000_000L, 1_000_000_000L,
                1_000_000_000_000L, 1_000_000_000_000_000L, 1_000_000_000_000_000_000L};
        /** array with Decimal units */
        private static final String[] ARRAY_DEC_UNITS = {"byte", "bytes", "KB", "MB", "GB", "TB", "PB", "EB"};
        /** array with Decimal values */
        private static final long[] ARRAY_BIN_NO = {1L, 1L << 10, 1L << 20, 1L << 30, 1L << 40, 1L << 50, 1L << 60};
        /** array with Decimal units */
        private static final String[] ARRAY_BIN_UNITS = {"byte", "bytes", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB"};

        /**
         * Format bytes into a rounded string representation using IEC standard
         * @param inBytes input Bytes value
         * @param strStyle conversion style
         * @return Rounded string representation of the byte size
         */
        public static String convertUnits(final long inBytes, final String strStyle) {
            String outString = "";
            final List<String> knownStyles = Arrays.asList(
                    ConfigurationClass.STR_BINARY,
                    ConfigurationClass.STR_DECIMAL);
            if (knownStyles.contains(strStyle)) {
                long[] arrayNumbers = {1L};
                String[] arraySymbols = {"byte"};
                if (ConfigurationClass.STR_DECIMAL.equalsIgnoreCase(strStyle)) {
                    arrayNumbers = ARRAY_DEC_NO;
                    arraySymbols = ARRAY_DEC_UNITS;
                } else if (ConfigurationClass.STR_BINARY.equalsIgnoreCase(strStyle)) {
                    arrayNumbers = ARRAY_BIN_NO;
                    arraySymbols = ARRAY_BIN_UNITS;
                }
                if (inBytes == arrayNumbers[0]) { // bytes
                    outString = formatValue(inBytes, arrayNumbers[0], arraySymbols[0]);
                } else {
                    outString = convertHigherThatSingleUnitNumber(inBytes, arrayNumbers, arraySymbols);
                }
                if (outString.isBlank()) {
                    outString = formatValue(inBytes, arrayNumbers[6], arraySymbols[7]);
                }
            }
            return outString;
        }

        /**
         * Format bytes into a rounded string representation using IEC standard, part 2
         * @param inBytes input Bytes value
         * @param arrayNumbers array with Decimal values
         * @param arraySymbols array with Decimal units
         * @return String
         */
        private static String convertHigherThatSingleUnitNumber(
                final long inBytes,
                final long[] arrayNumbers,
                final String... arraySymbols) {
            String outString = "";
            final int symbolsLength = Math.toIntExact(arraySymbols.length - 1L);
            int iCounter = 1;
            boolean needsToContinue = true;
            while (iCounter < symbolsLength
                    && needsToContinue) {
                if (inBytes < arrayNumbers[iCounter]) {
                    outString = formatValue(inBytes, arrayNumbers[iCounter - 1], arraySymbols[iCounter]);
                    needsToContinue = false;
                }
                iCounter++;
            }
            return outString;
        }

        /**
         * Converting String into Long number
         * @param inString input String to convert
         * @return converted long number
         */
        public static long convertStringToLongNumberSafely(final String inString) {
            final String cleanedInString = inString.replaceAll(RegularExpressionsClass.REGEXP_CLEAN_NDGT, "");
            if (cleanedInString.isEmpty()) {
                throw new IllegalStateException("Input String only contains non-digit characters, "
                        + "hence cannot be converted into Long number");
            }
            return Long.parseLong(cleanedInString);
        }

        /**
         * Format units as exact integer or fractional decimal based on the prefix,
         * appending the appropriate units
         * @param inValue input value to format
         * @param inDivider divisor of the unit multiplier
         * @param outSymbol String representing the units
         * @return string with formatted value
         */
        private static String formatValue(final long inValue, final long inDivider, final String outSymbol) {
            return inValue % inDivider == 0
                    ? String.format(Locale.ROOT, "%d %s", inValue / inDivider, outSymbol)
                            : String.format(Locale.ROOT, "%.1f %s", (double) inValue / inDivider, outSymbol);
        }

        // Private constructor to prevent instantiation
        private NumberConversionSubClass() {
            // intentional empty
        }

    }

    /**
     * Project Properties Reader
     */
    public static final class PropertiesReaderSubClass {

        /**
         * get variable
         * @param strVariables variables to pick
         * @return Properties
         */
        public static @NonNull Properties getVariableFromProjectProperties(
                final String propertyFileName,
                final String... strVariables) {
            final Properties svProperties = new Properties();
            try(InputStream inputStream = PropertiesReaderSubClass.class.getResourceAsStream(propertyFileName)) {
                final Properties inProperties = new Properties();
                inProperties.load(inputStream);
                final List<String> arrayVariables = Arrays.asList(strVariables);
                arrayVariables.forEach(crtVariable
                        -> svProperties.put(crtVariable, inProperties.getProperty(crtVariable)));
                if (!propertyFileName.startsWith("/META-INF/maven/")) {
                    final String strFeedback = String.format("Entire content from %s file loaded into stream...",
                            svProperties);
                    LogExposureClass.LOGGER.debug(strFeedback);
                }
            } catch (IOException ei) {
                final Path ptPrjProps = Path.of(propertyFileName);
                final String strFeedback = String.format(FileOperationsClass.FILE_FIND_ERR,
                        ptPrjProps.getParent(),
                        ptPrjProps.getFileName());
                LogExposureClass.exposeInputOutputException(strFeedback, Arrays.toString(ei.getStackTrace()));
            }
            return svProperties;
        }

        private PropertiesReaderSubClass() {
            // intentionally left open
        }

    }

    /**
     * Constructor
     */
    private BasicStructuresClass() {
        // intentionally blank
    }
}
