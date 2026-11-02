package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.file.FileStatisticsClass;
import io.github.pgdro.utilities.ArchivingClass;
import java.util.Properties;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "ArchiveFolders",
                     description = "Archive sub-folders from a given folder")
class ArchiveFolders implements Runnable {

    /**
     * String for FolderName
     */
    @CommandLine.Option(
            names = {"-aExe", "--archivingExecutable"},
            description = "Archiving executable (including full path, required, only one)",
            arity = InteractiveInputClass.ARITY_ONLY_ONE,
            required = true)
    private String strArchivingExec;

    /**
     * String for archive name prefix
     */
    @CommandLine.Option(
            names = {"-pwd", "--archivePassword"},
            description = "Password for archive encryption (optional, only one)",
            arity = InteractiveInputClass.ARITY_ONLY_ONE)
    private String strArchivePwd;

    /**
     * String for archive name prefix
     */
    @CommandLine.Option(
            names = {"-ap", "--archivePrefix"},
            description = "Prefix to apply to archive name (optional, only one)",
            arity = InteractiveInputClass.ARITY_ONLY_ONE)
    private String strArchivePrefix;

    /**
     * String for archive name prefix
     */
    @CommandLine.Option(
            names = {"-as", "--archiveSuffix"},
            description = "Suffix to apply to archive name (optional, only one)",
            arity = InteractiveInputClass.ARITY_ONLY_ONE)
    private String strArchiveSuffix;

    /**
     * adds the options defined in
     * InteractiveInputClass.FolderNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.FolderNameOptionMixinClass optFolderNames
            = new InteractiveInputClass.FolderNameOptionMixinClass();

    /**
     * adds the options defined in
     * InteractiveInputClass.FolderDestinationOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.FolderDestinationOptionMixinClass optFolderDest
            = new InteractiveInputClass.FolderDestinationOptionMixinClass();

    @Override
    public void run() {
        final Properties propFolder = new Properties();
        if (strArchivingExec != null) {
            final String strFeedback = String.format("Archiving executable has been set as %s", strArchivingExec);
            LogExposureClass.LOGGER.info(strFeedback);
            ArchivingClass.setArchivingExecutable(strArchivingExec);
        }
        ArchivingClass.setArchivePrefix(strArchivePrefix);
        ArchivingClass.setArchiveSuffix(strArchiveSuffix);
        if (strArchivePwd != null) {
            ArchivingClass.setArchivePwd(strArchivePwd);
        }
        final String[] inFolders = optFolderNames.getFolderNames();
        for (final String strFolder : inFolders) {
            final String strFeedback = String.format("Processing folder %s", strFolder);
            LogExposureClass.LOGGER.info(strFeedback);
            propFolder.clear();
            final Properties folderProps = FileStatisticsClass.getFolderStatisticsRecursive(strFolder, propFolder);
            final String strFeedback2 = String.format("Initial folder statistics are %s", folderProps);
            LogExposureClass.LOGGER.info(strFeedback2);
            ArchivingClass.setArchivingDir(strFolder);
            ArchivingClass.setArchiveNameWithinDestinationFolder(optFolderDest.getFolderDestination());
            ArchivingClass.archiveFolderAs7z();
            ArchivingClass.exposeArchivedStatistics(folderProps);
        }
    }

    /**
     * Constructor
     */
    protected ArchiveFolders() {
        // intentionally left blank
    }
}
