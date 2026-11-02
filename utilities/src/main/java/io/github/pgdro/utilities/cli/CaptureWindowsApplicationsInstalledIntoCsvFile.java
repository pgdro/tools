package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.ShellingClass;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * clean files older than a given number of days
 */
@CommandLine.Command(name = "CaptureWindowsApplicationsInstalledIntoCsvFile",
                     description = "Run the experimental new feature")
class CaptureWindowsApplicationsInstalledIntoCsvFile implements Runnable {
    /**
     * adds the options defined in
     * InteractiveInputClass.OutFileNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.OutFileNameOptionMixinClass optionOut
            = new InteractiveInputClass.OutFileNameOptionMixinClass();

    @Override
    public void run() {
        final String outCsvFile = optionOut.getOutFileName();
        ShellingClass.PowerShellExecutionSubClass.captureWindowsApplicationsIntoCsvFile(outCsvFile);
    }

    /**
     * Constructor
     */
    protected CaptureWindowsApplicationsInstalledIntoCsvFile() {
        // intentionally left blank
    }

}
