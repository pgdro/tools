package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.file.FileOperationsClass;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "CleanOlderFilesFromFolder",
                     description = "Clean files older than a given number of days")
class CleanOlderFilesFromFolder implements Runnable {

    /**
     * adds the options defined in
     * InteractiveInputClass.FolderNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.FolderNameOptionMixinClass optFolderNames
            = new InteractiveInputClass.FolderNameOptionMixinClass();
    /**
     * String for FileName
     */
    @CommandLine.Option(
            names = {"-dLmt", "--daysOlderLimit"},
            description = "Limit number of days to remove files from",
            arity = "1",
            required = true)
    private int intDaysOlderLimit;

    @Override
    public void run() {
        FileOperationsClass.DeletingSubClass.OlderSubSubClass.setCleanedFolderStatistics(true);
        final String[] inFolders = optFolderNames.getFolderNames();
        for (final String strFolder : inFolders) {
            FileOperationsClass.DeletingSubClass.OlderSubSubClass.setOrResetCleanedFolderStatistics();
            FileOperationsClass.DeletingSubClass.OlderSubSubClass.deleteFilesOlderThanGivenDays(
                    strFolder,
                    intDaysOlderLimit);
            final Map<String, Long> statsClndFldr
                    = FileOperationsClass.DeletingSubClass.OlderSubSubClass.getCleanedFolderStatistics();
            final String strFeedback = String.format(
                    "Folder %s has been cleaned eliminating %s files and freeing %s bytes in terms of disk space...",
                    strFolder,
                    statsClndFldr.get("Files"),
                    statsClndFldr.get(ConfigurationClass.STR_SIZE));
            LogExposureClass.LOGGER.info(strFeedback);
        }
    }

    /**
     * Constructor
     */
    protected CleanOlderFilesFromFolder() {
        // intentionally left blank
    }

}
