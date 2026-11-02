package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.file.FileContentClass;
import java.nio.file.Path;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "CaptureImportsFromJavaSourceFilesIntoCsvFile",
                     description = "Get import inventory from all Java source files within a given folder")
class CaptureImportsFromJavaSourceFilesIntoCsvFile implements Runnable {

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
    private final InteractiveInputClass.OutFileNameOptionMixinClass optionOut =
            new InteractiveInputClass.OutFileNameOptionMixinClass();

    @Override
    public void run() {
        final String[] inFolders = optFolderNames.getFolderNames();
        final String outCsvFile = optionOut.getOutFileName();
        for (final String strFolder : inFolders) {
            FileContentClass.ContentReadingSubClass.extractImportStatementsFromJavaSourceFilesIntoCsvFile(
                    Path.of(strFolder),
                    Path.of(outCsvFile));
        }
    }

    /**
     * Constructor
     */
    protected CaptureImportsFromJavaSourceFilesIntoCsvFile() {
        // intentionally left blank
    }

}
