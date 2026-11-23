package io.github.pgdro.utilities.cli.commands;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.RegularExpressionsClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import io.github.pgdro.utilities.RemoteInformationRetrievalClass;
import java.util.Locale;
import java.util.Properties;
import picocli.CommandLine;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "GetRemoteMavenPackageDetails",
                     description = "Read Maven package details from central Maven repository")
public class GetRemoteMavenPackageDetails implements Runnable {

    /**
     * Main logic of the command
     */
    @Override
    public void run() {
        // no-op
        final String strPackage = "com.github.oshi:oshi-core-ffm";
        final String strVersion = RemoteInformationRetrievalClass
                .MavenSubClass
                .getLatestVersionFromMavenCentralRepository(strPackage);
        final String strFeedback = "For package " + strPackage + " latest version is: " + strVersion;
        LogExposureClass.LOGGER.info(strFeedback);
        final String strWebSite = RegularExpressionsClass
                .buildCentralMavenRepositoryUniformResourceLocator(strPackage);
        final String[] packageParts = strPackage.split(":");
        final String strRemoteFileUrl = strWebSite + strVersion + "/" + packageParts[1] + "-" + strVersion + ".jar";
        final String strFeedback2 = "Remote file is: " + strRemoteFileUrl;
        LogExposureClass.LOGGER.info(strFeedback2);
        final Properties urlAttributes = RemoteInformationRetrievalClass
                .RequestSubClass
                .requestHttpFile(strRemoteFileUrl, "AttributesFromHeader");
        final String strFeedback3 = "Retrieved attributes from header are: " + urlAttributes.toString();
        LogExposureClass.LOGGER.info(strFeedback3);
        final String strChecksumUrl = strRemoteFileUrl + ".sha256";
        final String checksumValue = RemoteInformationRetrievalClass
                .RequestSubClass
                .requestHttpFile(strChecksumUrl, ConfigurationClass.STR_CONTENT)
                .getOrDefault(ConfigurationClass.STR_CONTENT, "MISSING")
                .toString()
                .trim()
                .toLowerCase(Locale.ENGLISH);
        final String strFeedback4 = "SHA-256 from " + strChecksumUrl + " has content: " + checksumValue;
        LogExposureClass.LOGGER.info(strFeedback4);
    }

    /**
     * Constructor
     */
    protected GetRemoteMavenPackageDetails() {
        // intentionally left blank
    }

}
