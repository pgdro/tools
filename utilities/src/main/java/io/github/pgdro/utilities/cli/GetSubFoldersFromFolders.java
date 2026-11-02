package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.file.FileOperationsClass;
import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * Captures sub-folder from a Given Folder into Log file
 */
@CommandLine.Command(name = "GetSubFoldersFromFolders",
                     description = "Captures sub-folders from a Given Folder into Log file")
class GetSubFoldersFromFolders implements Runnable {

    /**
     * adds the options defined in
     * InteractiveInputClass.FolderNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.FolderNameOptionMixinClass optFolderNames
            = new InteractiveInputClass.FolderNameOptionMixinClass();

    @Override
    public void run() {
        final String[] inFolders = optFolderNames.getFolderNames();
        for (final String strFolder : inFolders) {
            final List<String> arraySubFolders
                    = FileOperationsClass.RetrievingSubClass.getSubFoldersFromFolder(strFolder);
            final String strFeedback = String.format("Considering folder %s following sub-folders were found: %s",
                    strFolder,
                    arraySubFolders);
            LogExposureClass.LOGGER.info(strFeedback);
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    protected GetSubFoldersFromFolders() {
        // intentionally left blank
    }

}
