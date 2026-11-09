/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.log;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * exposing things to Log
 */
public final class LogExposureClass {
    /** standard Unknown feature constant */
    public static final String STR_I18N_UNKN_FTS = "Feature %s is NOT known in %s...";
    /** standard Unknown constant */
    public static final String STR_I18N_UNKN = "Unknown";
    /** Logger variable */
    public static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Constructor
     */
    private LogExposureClass() {
        // intentionally blank
    }

    /**
     * Build message for I/O exception
     * @param inStackTrace tracking back the Stack Trace
     */
    public static void exposeInputOutputException(final String inStackTrace) {
        final String strFeedbackErr = String.format(
            "Input/Output exception on... %s",
            inStackTrace
        );
        LOGGER.error(strFeedbackErr);
    }

    /**
     * Build message for I/O exception
     * @param customMsg custom message
     * @param inStackTrace tracking back the Stack Trace
     */
    public static void exposeInputOutputException(
        final String customMsg,
        final String inStackTrace
    ) {
        final String strFeedbackErr = customMsg + "... " + inStackTrace;
        LOGGER.error(strFeedbackErr);
    }

    /**
     * Log Process Builder command conditionally
     * @param strCommand command to execute
     */
    public static void exposeProcessBuilder(final String strCommand) {
        final String strFeedback = String.format(
            "I intend to execute following shell command %s",
            strCommand
        );
        LOGGER.debug(strFeedback);
    }

    /**
     * Log Process Builder command conditionally
     * @param inStackTrace tracking back the Stack Trace
     */
    public static void exposeProjectModel(final String inStackTrace) {
        final String strFeedback = String.format(
            "Error on getting project model... \"%s\"",
            inStackTrace
        );
        LOGGER.error(strFeedback);
    }

    /**
     * Build message for file operation error
     * @param strFileName file name
     * @param strStagTrace stag trace
     * @return message for file operation error
     */
    public static String getFileErrorMessage(
        final String strFileName,
        final String strStagTrace
    ) {
        return String.format(
            "Error encountered when attempting to write to %s file... %s",
            strFileName,
            strStagTrace
        );
    }

    /**
     * get Unsupported Feature
     * @param strDecision decision evaluated
     * @param strWhere which function this is called from
     * @return String with localized feedback
     */
    public static String getUnsupportedFeatures(
        final String strDecision,
        final String strWhere
    ) {
        final String strFeedbackErr = String.format(
            STR_I18N_UNKN_FTS,
            strDecision,
            strWhere
        );
        LOGGER.error(strFeedbackErr);
        return strFeedbackErr;
    }

}
