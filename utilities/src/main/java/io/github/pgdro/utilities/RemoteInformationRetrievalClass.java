/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.utilities;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.RegularExpressionsClass;
import io.github.pgdro.tools.core.StringingClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import io.github.pgdro.tools.core.web.RequestClass;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jspecify.annotations.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * XML management
 */
public final class RemoteInformationRetrievalClass {
    /** Variable for Maven package in scope */
    private static String relevantPackage;

    // Private constructor to prevent instantiation
    private RemoteInformationRetrievalClass() {
        // intentionally blank
    }

    /**
     * build URL from String
     * @param strWebSite input URL as String
     * @return URL
     */
    public static URL buildUniformResourceLocatorFromString(@NonNull final String strWebSite) {
        URL urlReturn = null;
        try {
            urlReturn = URI.create(strWebSite).toURL();
        } catch (MalformedURLException e) {
            final String strFeedback = "Malformed Exception encountered on URL as "
                    + Arrays.toString(e.getStackTrace());
            LogExposureClass.LOGGER.error(strFeedback);
        }
        return urlReturn;
    }

    /**
     * Setter for relevantPackage
     * @param inPackage input Maven package
     */
    public static void setRelevantPackage(@NonNull final String inPackage) {
        if (!StringingClass.StringEvaluationSubClass.isStringActuallyMavenPackage(inPackage)) {
            final String strFeedback = "Given String is not a Maven package...";
            LogExposureClass.LOGGER.error(strFeedback);
            throw new IllegalStateException(strFeedback);
        }
        relevantPackage = inPackage;
    }

    /**
     * Formatting logic
     */
    public static final class MavenSubClass {

        /**
         * building Central Maven Repository as URL
         * @return URL to Central Maven Repository
         */
        private static URL buildMavenCentralRepositoryUniformResourceLocatorFromPackage() {
            final String strWebSite = RegularExpressionsClass
                    .buildCentralMavenRepositoryUniformResourceLocator(relevantPackage) + "maven-metadata.xml";
            final String strFeedback = String.format(
                    "Uniform Resource Locator from Central Maven Repository for %s package is: %s",
                    relevantPackage,
                    strWebSite);
            LogExposureClass.LOGGER.info(strFeedback);
            return buildUniformResourceLocatorFromString(strWebSite);
        }

        /**
         * get Document from inStream
         * @param inStream Input Stream
         * @return Document
         */
        private static Document getDocumentFromInputStream(final InputStream inStream) {
            Document doc = null;
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                docBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",
                        true);
                docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities",
                        false);
                docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities",
                        false);
                docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
                        false);
                docBuilderFactory.setExpandEntityReferences(false);
                // and these as well, per Timothy Morgan's 2014
                // paper: "XML Schema, DTD, and Entity Attacks"
                docBuilderFactory.setXIncludeAware(false);
                doc = parseDocumentFromInputStream(inStream, docBuilderFactory);
            } catch (ParserConfigurationException e) {
                final String strFeedback = "ParserConfigurationException while "
                        + "attempting to read remote XML from an URL as "
                        + Arrays.toString(e.getStackTrace());
                LogExposureClass.LOGGER.error(strFeedback);
            }
            return doc;
        }

        /**
         * get latest version if a Maven Package
         * @return String as version
         */
        public static String getLatestVersionFromMavenCentralRepository() {
            final URL url = buildMavenCentralRepositoryUniformResourceLocatorFromPackage();
            String strLatestVersion = "";
            assert url != null;
            try (InputStream inStream = url.openStream()) {
                final Document doc = getDocumentFromInputStream(inStream);
                if (doc != null) {
                    final Node latest = doc.getElementsByTagName("latest").item(0);
                    final Node release = doc.getElementsByTagName("release").item(0);
                    strLatestVersion = latest != null ? latest.getTextContent() : "";
                    if (strLatestVersion.isBlank()) {
                        strLatestVersion = release != null ? release.getTextContent() : "";
                    }
                }
            } catch (IOException e) {
                final String strFeedback = "IO Exception while attempting to read remote XML from an URL as "
                        + Arrays.toString(e.getStackTrace());
                LogExposureClass.LOGGER.error(strFeedback);
            }
            return strLatestVersion;
        }

        /**
         * get latest version statistics of a Maven Package
         * @return String as version
         */
        public static Properties getLatestVersionStatisticsFromMavenCentralRepository() {
            final Properties packageProperties = new Properties();
            final String[] packageParts = relevantPackage.split(":");
            packageProperties.setProperty("groupId", packageParts[0]);
            packageProperties.setProperty("artifactId", packageParts[1]);
            final String latestVersion = getLatestVersionFromMavenCentralRepository();
            packageProperties.setProperty("Latest Version", latestVersion);
            final String strWebSite = RegularExpressionsClass
                    .buildCentralMavenRepositoryUniformResourceLocator(relevantPackage);
            final String strRemoteFileUrl = strWebSite + latestVersion
                    + "/" + packageParts[1] + "-" + latestVersion + ".jar";
            final String strChecksumUrl = strRemoteFileUrl + ".sha256";
            final String checksumValue = RequestClass
                    .requestHttpFile(strChecksumUrl, ConfigurationClass.STR_CONTENT)
                    .getOrDefault(ConfigurationClass.STR_CONTENT, strChecksumUrl
                            + " is " + ConfigurationClass.STR_MISSING)
                    .toString()
                    .trim()
                    .toLowerCase(Locale.ENGLISH);
            if (checksumValue.endsWith(ConfigurationClass.STR_MISSING)) {
                final String strChecksumUrl1 = strRemoteFileUrl + ".sha1";
                final String checksumValue1 = RequestClass
                        .requestHttpFile(strChecksumUrl1, ConfigurationClass.STR_CONTENT)
                        .getOrDefault(ConfigurationClass.STR_CONTENT, strChecksumUrl1
                                + " is " + ConfigurationClass.STR_MISSING)
                        .toString()
                        .trim()
                        .toLowerCase(Locale.ENGLISH);
                packageProperties.setProperty("SHA-1", checksumValue1);
            } else {
                packageProperties.setProperty("SHA-256", checksumValue);
            }
            final Properties urlAttributes = RequestClass
                    .requestHttpFile(strRemoteFileUrl, "AttributesFromHeader");
            packageProperties.putAll(urlAttributes);
            return packageProperties;
        }

        /**
         * parse Doc from Input Stream
         * @param inStream Input Stream
         * @param docBuilderFactory DocumentBuilderFactory
         * @return Document
         */
        private static Document parseDocumentFromInputStream(
                final InputStream inStream,
                final @NonNull DocumentBuilderFactory docBuilderFactory) {
            Document doc = null;
            try {
                final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                doc = docBuilder.parse(inStream);
            } catch (ParserConfigurationException e) {
                final String strFeedback = "ParserConfigurationException thrown "
                        + "while attempting to read remote XML from an URL... "
                        + Arrays.toString(e.getStackTrace());
                LogExposureClass.LOGGER.error(strFeedback);
            } catch (SAXException e) {
                final String strFeedback = "SAXException thrown... DOCTYPE was passed into the XML document... "
                        + Arrays.toString(e.getStackTrace());
                LogExposureClass.LOGGER.error(strFeedback);
            } catch (IOException e) {
                final String strFeedback = "IOException occurred, XXE may still be possible... "
                        + Arrays.toString(e.getStackTrace());
                LogExposureClass.LOGGER.error(strFeedback);
            }
            return doc;
        }

        /**
         * Construct
         */
        private MavenSubClass() {
            // intentionally blank
        }

    }

}
