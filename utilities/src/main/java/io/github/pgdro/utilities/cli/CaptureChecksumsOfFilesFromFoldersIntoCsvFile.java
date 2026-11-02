package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.file.FileStatisticsClass;
import io.github.pgdro.tools.core.time.TimingClass;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "CaptureChecksumsOfFilesFromFolderIntoCsvFile",
                     description = "Get statistics for all files within a given folder")
class CaptureChecksumsOfFilesFromFoldersIntoCsvFile implements Runnable {

    /**
     * adds the options defined in
     * InteractiveInputClass.FolderNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.FolderNameOptionMixinClass optFolderNames
            = new InteractiveInputClass.FolderNameOptionMixinClass();
    /**
     * adds the options defined in
     * InteractiveInputClass.OutFileNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.OutFileNameOptionMixinClass optOutFileName
            = new InteractiveInputClass.OutFileNameOptionMixinClass();

    @Override
    public void run() {
        final String[] inAlgorithms = {"SHA-256", "SHA3-256"};
        FileStatisticsClass.setChecksumAlgorithms(inAlgorithms);
        final String[] inFolders = optFolderNames.getFolderNames();
        final String outCsvFile = optOutFileName.getOutFileName();
        for (final String strFolder : inFolders) {
            final ZonedDateTime startComputeTime = ZonedDateTime.now(ZoneId.systemDefault());
            FileStatisticsClass.captureFileStatisticsFromFolder(strFolder, outCsvFile);
            final ZonedDateTime zStopTimeStamp = ZonedDateTime.now(ZoneId.systemDefault());
            final Duration objDuration = Duration.between(startComputeTime, zStopTimeStamp);
            final String strFeedback = String.format(
                    "For the folder %s calculated checksums are stored in the file %s, "
                            + "operation completed in %s (which means %s | %s)",
                    strFolder,
                    outCsvFile,
                    objDuration.toString(),
                    TimingClass.AgingSubClass.computeAgingIntoHumanReadableWords(startComputeTime, zStopTimeStamp),
                    TimingClass.AgingSubClass.computeAgingIntoTimeClock(startComputeTime, zStopTimeStamp));
            LogExposureClass.LOGGER.info(strFeedback);
        }
    }

    /**
     * Constructor
     */
    protected CaptureChecksumsOfFilesFromFoldersIntoCsvFile() {
        // intentionally left blank
    }

}
