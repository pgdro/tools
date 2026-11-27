package io.github.pgdro.utilities.cli.commands;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import io.github.pgdro.utilities.RemoteInformationRetrievalClass;
import java.util.Properties;
import picocli.CommandLine;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "GetRemoteMavenPackageDetails",
                     description = "Read Maven package details from central Maven repository")
public class GetRemoteMavenPackageDetails implements Runnable {
    /**
     * String for Maven package
     */
    @CommandLine.Option(
            names = {"-pk", "--package"},
            description = "Package name as groupId:artifactId",
            arity = InteractiveInputClass.ARITY_ONE_OR_MORE,
            required = true)
    private String[] strPackages;

    /**
     * Main logic of the command
     */
    @Override
    public void run() {
        for (String crtPackage : strPackages) {
            RemoteInformationRetrievalClass.setRelevantPackage(crtPackage);
            final Properties packageProps = RemoteInformationRetrievalClass
                    .MavenSubClass
                    .getLatestVersionStatisticsFromMavenCentralRepository();
            final String strFeedbackSha256 = "Package properties are: " + packageProps;
            LogExposureClass.LOGGER.info(strFeedbackSha256);
        }
    }

    /**
     * Constructor
     */
    protected GetRemoteMavenPackageDetails() {
        // intentionally left blank
    }

}
