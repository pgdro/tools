package io.github.pgdro.tools.dynamic.web.html;

import io.github.pgdro.tools.core.BasicStructuresClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.time.TimingClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Locale;

/**
 * List and Maps management
 */
public final class FileInfoClass {
    /** Variable for File Modified Time-stamp */
    /* default */ private static String fileModifiedTs = "unknown modified timestamp";
    /** Variable for File size (bytes) */
    /* default */ private static long fileSizeBytes;

    /**
     * constructor
     */
    private FileInfoClass() {
        // intentionally left blank
    }

    /**
     * Build Information Box legacy
     * @return String
     */
    public static String gatherFileStatistics(final Path fileName) {
        return gatherFileStatistics(fileName, false);
    }

    /**
     * Build Information Box
     * @return String
     */
    public static String gatherFileStatistics(final Path fileName, final boolean fileFullPath) {
        if (Files.exists(fileName)) {
            fileSizeBytes = fileName.toFile().length();
            fileModifiedTs = TimingClass
                    .LocalizationSubClass
                    .FileSubSubClass
                    .getFileLastModifiedTimeAsHumanReadableFormat(fileName);
        } else {
            final String strFeedback = "Given file "
                    + fileName
                    + " was not found on disk, hence will be looking for it within JAR";
            LogExposureClass.LOGGER.debug(strFeedback);
            final String internalFile = fileName.toString().replace("\\", "/");
            try (InputStream inStream = HtmlClass.class.getResourceAsStream(internalFile)) {
                if (inStream == null) {
                    final String strFeedback3 = "Input Stream is null for internal file: " + internalFile;
                    LogExposureClass.LOGGER.debug(strFeedback3);
                } else {
                    final String strFeedback2 = "Input Stream is: " + inStream;
                    LogExposureClass.LOGGER.debug(strFeedback2);
                    fileSizeBytes = inStream.transferTo(OutputStream.nullOutputStream());
                    final URL resourceUrl = HtmlClass.class.getResource(internalFile);
                    fileModifiedTs = getFileModifiedTimestamp(resourceUrl, internalFile);
                }
            } catch (IOException ex) {
                LogExposureClass.exposeProjectModel(Arrays.toString(ex.getStackTrace()));
            }
        }
        final String rawHtml = "File is <span class=\"importantText\">%s</span>, "
                + "has a size of <span class=\"importantText\">%s bytes (%s)</span>, "
                + "last modified time-stamp on <span class=\"importantText\">%s</span>";
        final String strThousandSep = "%,d";
        String exposedFile = fileName.getFileName().toString();
        if (fileFullPath) {
            exposedFile = fileName.toString();
        }
        return String.format(rawHtml,
                exposedFile,
                String.format(Locale.US, strThousandSep, fileSizeBytes),
                BasicStructuresClass.NumberConversionSubClass.convertUnits(fileSizeBytes, "binary"),
                fileModifiedTs);
    }

    /**
     * Get File Modified TS
     * @param resourceUrl corresponding URL for input file
     * @param internalFile input File as String
     * @return String w. file Time-stamp
     */
    private static String getFileModifiedTimestamp(final URL resourceUrl, final String internalFile) {
        String strFileModifiedTs = fileModifiedTs;
        if (resourceUrl == null) {
            final String strFeedback4 = "Resource URL is null for internal file: " + internalFile;
            LogExposureClass.LOGGER.debug(strFeedback4);
        } else {
            final String strFeedback3 = "URI is: " + resourceUrl;
            LogExposureClass.LOGGER.debug(strFeedback3);
            try {
                final long lastModified = resourceUrl.openConnection().getLastModified();
                final ZonedDateTime zonedLastModified
                        = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastModified), ZoneId.systemDefault());
                strFileModifiedTs = TimingClass.LocalizationSubClass.convertZonedTimestampFriendly(
                        zonedLastModified,
                        TimingClass.DATE_TIME_MS_ABRV).replaceAll(".000$", "");
            } catch (IOException e) {
                LogExposureClass.exposeProjectModel(Arrays.toString(e.getStackTrace()));
            }
        }
        return strFileModifiedTs;
    }

}
