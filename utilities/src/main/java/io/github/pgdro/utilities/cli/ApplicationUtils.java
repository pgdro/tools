/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import picocli.CommandLine;

/**
 * Main Command Line
 */
@CommandLine.Command(
    name = "top",
    subcommands = {
            AnalyzeColumnsFromCsvFiles.class,
            AnalyzePomFiles.class,
            ArchiveFolders.class,
            CaptureChecksumsOfFilesFromFoldersIntoCsvFile.class,
            CaptureImportsFromJavaSourceFilesIntoCsvFile.class,
            CaptureWindowsApplicationsInstalledIntoCsvFile.class,
            CleanOlderFilesFromFolder.class,
            GetSubFoldersFromFolders.class
    }
)
public class ApplicationUtils {

    /**
     * Application logic
     * @param args input arguments
     */
    public static void main( final String[] args ) {
        InteractiveInputClass.startMeUpWithParameters("logs/Utilities", "/utilities-pom.xml");
        final int intUtilsExitCode = new CommandLine(new ApplicationUtils()).execute(args);
        InteractiveInputClass.shutMeDownWithParameters(intUtilsExitCode, args[0]);
    }

    /**
     * Private constructor to prevent instantiation
     */
    protected ApplicationUtils() {
        // intentionally left blank
    }

}
