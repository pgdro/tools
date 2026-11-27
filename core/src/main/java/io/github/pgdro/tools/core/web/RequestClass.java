/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.core.web;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import io.github.pgdro.tools.core.time.TimeLocalizationClass;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpOption;
import java.net.http.HttpOption.Http3DiscoveryMode;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Properties;
import org.jspecify.annotations.NonNull;

/**
 * Web request logic
 */
public final class RequestClass {
    /** Variable for Time Out connectivity */
    private static Long connectionTimeOut = 5L;
    /** Constant for HTTP Error code 200 */
    private static final Long HTTP_ERROR_OK = 200L;
    /** HTTP client constant */
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_3)
            .connectTimeout(Duration.ofSeconds(connectionTimeOut))
            .build();

    /**
     * Construct
     */
    private RequestClass() {
        // intentionally blank
    }

    /**
     * expose to Logs HTTP response version
     * @param response version of used HTTP protocol
     */
    private static void exposeHttpResponseVersion(final @NonNull HttpResponse<?> response) {
        final String strFeedbackErr = "Response protocol version was " + response.version().toString();
        LogExposureClass.LOGGER.info(strFeedbackErr);
    }

    /**
     * gets remote file content
     * @param inBuilder input Builder for HttpRequest
     * @return String with file content
     * @throws IOException error management for I/O
     * @throws InterruptedException error management for Interruption
     */
    private static String getRemoteFileContent(
            final @NonNull String strRemoteFileUrl,
            final Builder inBuilder) throws IOException, InterruptedException {
        final HttpRequest requestContent = inBuilder
                .GET()
                .build();
        final String strFeedback = "I have prepared a GET request for " + strRemoteFileUrl;
        LogExposureClass.LOGGER.debug(strFeedback);
        final HttpResponse<String> responseFull = CLIENT
                .send(requestContent, HttpResponse.BodyHandlers.ofString());
        String fileContent = null;
        final long responseCode = responseFull.statusCode();
        if (responseCode == HTTP_ERROR_OK) {
            exposeHttpResponseVersion(responseFull);
            fileContent = responseFull.body();
        } else {
            logImproperStatusCode(responseCode);
        }
        return fileContent;
    }

    /**
     * gets remote file attributes from header request
     * @param inBuilder input Builder for HttpRequest
     * @return Properties with file attributes
     * @throws IOException error management for I/O
     * @throws InterruptedException error management for Interruption
     */
    private static @NonNull Properties getRemoteFileHeaderAttributes(
            final @NonNull String strRemoteFileUrl,
            final Builder inBuilder) throws IOException, InterruptedException {
        final Properties outProperties = new Properties();
        final HttpRequest requestHeader = inBuilder
                .HEAD()
                .build();
        final String strFeedback = "I have prepared a HEAD request for " + strRemoteFileUrl;
        LogExposureClass.LOGGER.debug(strFeedback);
        final HttpResponse<Void> responseHeader = CLIENT
                .send(requestHeader, HttpResponse.BodyHandlers.discarding());
        final long responseCode = responseHeader.statusCode();
        if (responseCode == HTTP_ERROR_OK) {
            exposeHttpResponseVersion(responseHeader);
            final String lastModified = responseHeader.headers()
                    .firstValue("Last-Modified")
                    .orElse("");
            outProperties.put("Last Modified", lastModified);
            if (!lastModified.isBlank() ) {
                final String lastModifiedTs = TimeLocalizationClass
                        .convertDateOrTimestampFormats(lastModified,
                            DateTimeFormatter.RFC_1123_DATE_TIME,
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                outProperties.put("Standardized Last Modified", lastModifiedTs);
            }
            final long fileSize = responseHeader.headers()
                    .firstValueAsLong("Content-Length")
                    .orElse(-1L);
            outProperties.put(ConfigurationClass.STR_SIZE, fileSize);
        } else {
            logImproperStatusCode(responseCode);
        }
        return outProperties;
    }

    private static void logImproperStatusCode(final long inResponseCode) {
        final String strFeedback = "An improper response has been received with code " + inResponseCode;
        LogExposureClass.LOGGER.error(strFeedback);
    }

    /**
     * Unified method to handle different remote file requests
     * @param strRemoteFileUrl input remote file URL
     * @param inWhat input Method
     * @return Properties with one or multiple values
     */
    public static @NonNull Properties requestHttpFile(
            final @NonNull String strRemoteFileUrl,
            final String inWhat) {
        final Properties fileProperties = new Properties();
        final URI inputUri = URI.create(strRemoteFileUrl);
        try {
            final Builder builder = HttpRequest.newBuilder(inputUri)
                    .setOption(HttpOption.H3_DISCOVERY, Http3DiscoveryMode.ANY)
                    .timeout(Duration.ofSeconds(connectionTimeOut));
            switch(inWhat) {
                case "AttributesFromHeader":
                    final Properties headerAttributes = getRemoteFileHeaderAttributes(strRemoteFileUrl, builder);
                    if (!headerAttributes.isEmpty()) {
                        fileProperties.putAll(headerAttributes);
                    }
                    break;
                case ConfigurationClass.STR_CONTENT:
                    final String fileContent = getRemoteFileContent(strRemoteFileUrl, builder);
                    if (fileContent != null
                            && !fileContent.isBlank()) {
                        fileProperties.put(ConfigurationClass.STR_CONTENT, fileContent);
                    }
                    break;
                default:
                    final String strFeedbackErr = LogExposureClass.getUnsupportedFeatures(
                            inWhat,
                            StackWalker.getInstance().walk(frames
                                    -> frames.findFirst().map(frame
                                    -> frame.getClassName() + "." + frame.getMethodName())
                                    .orElse(LogExposureClass.STR_I18N_UNKN)));
                    throw new UnsupportedOperationException(strFeedbackErr);
            }
        } catch (InterruptedException e) {
            final String strFeedback = "Execution was interrupted... " + Arrays.toString(e.getStackTrace());
            LogExposureClass.LOGGER.warn(strFeedback);
            Thread.currentThread().interrupt(); // NOPMD by Daniel Popiniuc on 26.09.2026, 16:47
        } catch (IOException e) {
            final String strFeedback = "Input/Output Exception while attempting to read remote XML from an URL as "
                    + Arrays.toString(e.getStackTrace());
            LogExposureClass.LOGGER.error(strFeedback);
        }
        return fileProperties;
    }

    /**
     * Setter for connectionTimeOut
     * @param inConnTimeOut input Long value Connection Time Out
     */
    public static void setConnectionTimeOut(final Long inConnTimeOut) {
        connectionTimeOut = inConnTimeOut;
    }

}
