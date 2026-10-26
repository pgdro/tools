package io.github.pgdro.tools.core;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import org.jspecify.annotations.NonNull;

/**
 * String related logic
 */
public final class StringingClass {

    // Private constructor to prevent instantiation
    private StringingClass() {
        // intentionally blank
    }

    /**
     * Cleaning things
     */
    public static final class StringCleaningSubClass {

        /**
         * Clean String From CurlyBraces
         * @param strOriginal Original string
         * @return String
         */
        public static String cleanStringFromCurlyBraces(final String strOriginal) {
            final StringBuilder strBuilder = new StringBuilder();
            for (final char c : strOriginal.toCharArray()) {
                if (c != '{' && c != '}') {
                    strBuilder.append(c);
                }
            }
            return strBuilder.toString();
        }

        /**
         * Cleaning string to be used as database object
         * @param strObject input String
         * @return String cleaned
         */
        public static String cleanStringAsDatabaseObject(final String strObject) {
            return strObject.replaceAll("[^A-Za-z0-9_/.|()]", "");
        }

        /**
         * Cleaning string from unwanted characters
         * @param strObject input String
         * @return String cleaned
         */
        public static String cleanStringFromUnwantedCharacters(final String strObject) {
            return strObject.replaceAll("[^A-Za-z0-9 _\\-–/.():'`]", "");
        }

        /**
         * Ensuring proper escaping for valid JSON
         * @param inString input String
         * @return String with proper escaped characters
         */
        public static String ensureEscapingOnEndOfLineAndTabs(final String inString) {
            return inString.replace("\n", "\\n").replace("\r", "\\r").replace("\t", " ");
        }

        /**
         * Helper to remove surrounding double quotes safely
         * @param strInput initial String
         * @return String without double quotes enclosing
         */
        public static String stripQuotes(final String strInput) {
            return (strInput.length() >= 2
                    && strInput.startsWith("\"")
                    && strInput.endsWith("\""))
                    ? strInput.substring(1, strInput.length() - 1)
                    : strInput;
        }

        // Private constructor to prevent instantiation
        private StringCleaningSubClass() {
            // intentionally blank
        }

    }

    /**
     * Conversion things
     */
    public static final class StringConversionSubClass {
        /**
         * Single Question Mark Character
         */
        private static final String Q_MARK_PARAM = "SingleQuestionMarkCharacterParameter";

        /**
         * Convert Prompt Parameters into Named Parameters
         * @param strOriginalQ query with prompt parameter
         * @return query with named parameters
         */
        private static String convertPromptParameters(final String strOriginalQ, final String type) {
            final String strFeedbackStrt = String.format("Original query is: \"%s\"", strOriginalQ);
            LogExposureClass.LOGGER.debug(strFeedbackStrt);
            final List<String> listMatches = RegularExpressionsClass.extractMatches(
                    strOriginalQ,
                    RegularExpressionsClass.REGEXP_PRMTR_RGX);
            String strFinalQ = strOriginalQ;
            if (Q_MARK_PARAM.equalsIgnoreCase(type)) {
                for (final String currentPrmtName : listMatches) {
                    strFinalQ = strFinalQ.replace(currentPrmtName, Character.toString(63));
                }
            } else if (ConfigurationClass.STR_NAMED_PARAM.equalsIgnoreCase(type)) {
                for (final String currentPrmtName : listMatches) {
                    strFinalQ = strFinalQ.replace(currentPrmtName,
                            convertSinglePromptParameterIntoNamedParameter(currentPrmtName));
                }
            }
            final String strFeedbackEnd = String.format("Final query is: \"%s\"", strFinalQ);
            LogExposureClass.LOGGER.debug(strFeedbackEnd);
            return strFinalQ;
        }

        /**
         * Convert Prompt Parameters into Named Parameters
         * @param strOriginalQ query with prompt parameter
         * @return query with named parameters
         */
        public static String convertPromptParametersIntoNamedParameters(final String strOriginalQ) {
            return convertPromptParameters(strOriginalQ, ConfigurationClass.STR_NAMED_PARAM);
        }

        /**
         * Convert Prompt Parameters into Named Parameters
         * @param strOriginalQ query with prompt parameter
         * @return query with named parameters
         */
        public static String convertPromptParametersIntoParameters(final String strOriginalQ) {
            return convertPromptParameters(strOriginalQ, Q_MARK_PARAM);
        }

        /**
         * get Named Parameter From Prompt One
         * @param strOriginal Original string
         * @return String
         */
        private static String convertSinglePromptParameterIntoNamedParameter(final String strOriginal) {
            return ":"
                    + StringCleaningSubClass.cleanStringFromCurlyBraces(strOriginal).replace(" ", "_");
        }

        // Private constructor to prevent instantiation
        private StringConversionSubClass() {
            // intentionally blank
        }

    }

    /**
     * Evaluating things
     */
    public static final class StringEvaluationSubClass {
        /** Maximum Length for Integer */
        /* default */ private static final int MAX_LENGTH_INT = 10;
        /** Maximum Integer */
        /* default */ private static final int MAX_INT = 2_147_483_647;

        /**
         * Checks if given string is included in a given List of Strings
         * @param str String to search into
         * @param substrings Strings to search for
         * @return boolean true if found, false otherwise
         */
        public static boolean hasMatchingSubstring(final String str, final List<String> substrings) {
            return substrings.stream().anyMatch(str::contains);
        }

        /**
         * Check if String is actually Date
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Date
         */
        public static boolean isStringActuallyDate(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    ConfigurationClass.STR_JUST_DATE);
        }

        /**
         * Check if String is actually Date w. Hours and Minutes
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Date
         */
        public static boolean isStringActuallyDateWithHourAndMinutes(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    ConfigurationClass.STR_DATE_HRMN);
        }

        /**
         * Check if String is actually Decimal
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Integer
         */
        public static boolean isStringActuallyDecimal(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    ConfigurationClass.STR_DECIMAL);
        }

        /**
         * Check if String is actually Numeric
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Integer
         */
        public static boolean isStringActuallyInteger(final String inputString) {
            boolean evaluation = RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    "long");
            if (evaluation
                    && inputString.length() >= MAX_LENGTH_INT) {
                try {
                    final long longValue = Long.parseLong(inputString);
                    if (longValue > MAX_INT) {
                        evaluation = false;
                    }
                } catch (NumberFormatException noFormatException) {
                    final String strFeedback = String.format(ConfigurationClass.CONVERT_INT_NA,
                            inputString,
                            Arrays.toString(noFormatException.getStackTrace()));
                    LogExposureClass.LOGGER.error(strFeedback);
                    evaluation = false;
                }
            }
            return evaluation;
        }

        /**
         * Check if String is actually Long
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Long
         */
        public static boolean isStringActuallyLong(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString, "long");
        }

        /**
         * Check if String is actually Long Time-stamp w. milliseconds
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Long Time-stamp w. milliseconds
         */
        public static boolean isStringActuallyLongTimestampWithMilliseconds(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    RegularExpressionsClass.REGEXP_LONG_TS_MS);
        }

        /**
         * Check if String is actually Numeric
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Numeric
         */
        public static boolean isStringActuallyNumeric(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString, "numeric");
        }

        /**
         * Check if String is actually Time-stamp
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Time-stamp
         */
        public static boolean isStringActuallyTimestamp(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    ConfigurationClass.STR_TIMESTAMP);
        }

        /**
         * Check if String is actually Time-stamp w. milliseconds
         *
         * @param inputString string to evaluate
         * @return True if given String is actually Time-stamp w. milliseconds
         */
        public static boolean isStringActuallyTimestampWithMilliseconds(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString,
                    ConfigurationClass.STR_TS_MSEC);
        }

        /**
         * Check if String is actually a Version
         * @param inputString string to evaluate
         * @return True if given String is actually Version
         */
        public static boolean isStringActuallyVersion(final String inputString) {
            return RegularExpressionsClass.ValidationSubClass.isStringActuallySomething(inputString, "version");
        }

        /**
         * check if string follows a pattern
         * @param inString input String
         * @return true/false
         */
        public static boolean isStringOneVariable(final String inString) {
            return inString.startsWith("${")
                    && inString.endsWith("}");
        }

        // Private constructor to prevent instantiation
        private StringEvaluationSubClass() {
            // intentionally blank
        }
    
    }

    /**
     * Transforming things
     */
    public static final class StringTransformationSubClass {

        /**
         * Compute String checksum
         * @param inString input String
         * @return String
         */
        public static String computeStringSignature(final @NonNull String inString) {
            String outString = "";
            final String algorithm = "SHA-256";
            try {
                // 1. Get the SHA-256 instance
                final MessageDigest digest = MessageDigest.getInstance(algorithm);
                // 2. Perform the hashing
                final byte[] encodedHash = digest.digest(inString.getBytes(StandardCharsets.UTF_8));
                // 3. Convert to Hex String using HexFormat (introduced in Java 17+)
                outString = HexFormat.of().formatHex(encodedHash);
            } catch (NoSuchAlgorithmException e) {
                final String strFeedbackErr = String.format("Checksum algorithm %s is not available.... %s",
                        algorithm,
                        Arrays.toString(e.getStackTrace()));
                LogExposureClass.LOGGER.error(strFeedbackErr);
            }
            return outString;
        }

        /**
         * get Named Parameter From Prompt One
         * @param inString Original string
         * @return String
         */
        private static String encloseStringWithCharacter(final String inString, final char inChar) {
            final StringBuilder strBuilder = new StringBuilder();
            final String strBothEnclosed =  "^" + inChar + ".*" + inChar + "$";
            final String strStartEnclosed =  "^" + inChar + ".*[^" + inChar + "]$";
            final String strEndEnclosed =  "^[^" + inChar + "].*" + inChar + "$";
            if (inString.matches(strBothEnclosed)) { // is already enclosed
                strBuilder.append(inString);
            } else if (inString.matches(strStartEnclosed)) { // has only start enclosed
                strBuilder.append(inString).append('\"');
            } else if (inString.matches(strEndEnclosed)) { // has only end enclosed
                strBuilder.append('\"').append(inString);
            } else { // does not have neither start nor end enclosed
                strBuilder.append('\"').append(inString).append('\"');
            }
            return strBuilder.toString();
        }

        /**
         * get Named Parameter From Prompt One
         * @param inString Original string
         * @return String
         */
        public static String encloseStringIfContainsSpace(final @NonNull String inString, final char inChar) {
            String strReturn = inString;
            if (inString.contains(" ")) {
                strReturn = encloseStringWithCharacter(inString, inChar);
            }
            return strReturn;
        }

        /**
         * Format Decimal String with a Thousand separator
         * @param strOriginal Original string
         * @return String
         */
        public static String formatStringWithDecimalContentWithThousandDecimalSeparator(final String strOriginal) {
            return String.format(Locale.US, "%,.2f", new BigDecimal(strOriginal));
        }

        /**
         * Ensures no password exposure
         * @param inProps input Properties
         * @return Properties with certain things obfuscated
         */
        public static @NonNull Properties obfuscateProperties(final Properties inProps) {
            final Properties outProps = new Properties();
            final String strKeyToObfuscate = "password";
            inProps.forEach((strKey, strValue) -> {
                if (strKey.equals(strKeyToObfuscate)) {
                    outProps.put(strKey, "*U*N*D*I*S*C*L*O*S*E*D*");
                } else {
                    outProps.put(strKey, strValue);
                }
            });
            return outProps;
        }

        // Private constructor to prevent instantiation
        private StringTransformationSubClass() {
            // intentionally blank
        }

    }

    /**
     * Extracts all occurrences of a given reg-ex pattern from a text
     * @param inputString The input string to search within.
     * @return A List of strings, where each string is a full match found
     */
    public static int countNamedParametersWithinQuery(final String inputString) {
        return RegularExpressionsClass.countOccurrences(inputString, "NamedParameters");
    }

    /**
     * Counts number of parameters with in a string
     * @param inputString string to evaluate
     * @return number of parameters within given string
     */
    public static int countPositionalTypeParametersWithinQuery(final String inputString) {
        return RegularExpressionsClass.countOccurrences(inputString, "PositionalTypeParameters");
    }

}
