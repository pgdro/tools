package io.github.pgdro.utilities.cli;

import io.github.pgdro.tools.core.BasicStructuresClass;
import io.github.pgdro.tools.core.InteractiveInputClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.core.file.FileContentClass;
import java.util.List;
import java.util.Map;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;

/**
 * Captures execution environment details into Log file
 */
@CommandLine.Command(name = "AnalyzeColumnsFromCsvFiles",
                     description = "Analyze columns from CSV file")
class AnalyzeColumnsFromCsvFiles implements Runnable {
    /**
     * adds the options defined in
     * InteractiveInputClass.FileNameOptionMixinClass to this command
     */
    @Mixin
    private final InteractiveInputClass.InFileNameOptionMixinClass optFileNames
            = new InteractiveInputClass.InFileNameOptionMixinClass();

    /**
     *
     * @param strFileName input File
     * @param intColToEval number of column to evaluate (starting from 0)
     * @param intColToGrpBy number of column to group by (starting from 0)
     */
    private static void storeWordFrequencyIntoCsvFile(final String strFileName,
                                                      final Integer intColToEval,
                                                      final Integer intColToGrpBy) {
        // Group values by category
        final Map<String, List<String>> groupedColumns = FileContentClass
                .ContentReadingSubClass
                .getListOfValuesFromColumnGroupedByAnotherColumnValuesFromCsvFile(
                        strFileName,
                        intColToEval,
                        intColToGrpBy);
        // Define merge rules
        final Map<List<String>, String> mergeRules = Map.of(
                List.of("ARRAY", "OBJECT", "VARIANT"), "COMPOSITE__STRUCTURED",
                List.of("FLOAT", "NUMBER"), "COMPOSITE__NUMERIC",
                List.of("DATETIME", "TIMESTAMP", "TIMESTAMP_LTZ", "TIMESTAMP_NTZ", "TIMESTAMP_TZ"),
                    "COMPOSITE__TIMESTAMP",
                List.of("BINARY", "TEXT", "VARCHAR"), "COMPOSITE__TEXT"
        );
        final Map<String, List<String>> grpCols
                = BasicStructuresClass.ListAndMapSubClass.mergeKeys(groupedColumns, mergeRules);
        final String strFeedback = "=".repeat(20) + strFileName + "=".repeat(20);
        LogExposureClass.LOGGER.info(strFeedback);
        FileContentClass.setCsvColumnSeparator(',');
        grpCols.forEach((keyDataType, valList) -> {
            final String strColFileName = strFileName.replace(".csv", "__columns.csv");
            final String strFeedbackFile = "Writing file " + strColFileName;
            LogExposureClass.LOGGER.info(strFeedbackFile);
            FileContentClass.ContentWritingSubClass.setCsvLinePrefix(keyDataType);
            FileContentClass.ContentWritingSubClass.writeStringListToCsvFile(
                    strColFileName,
                    "DataType,Column",
                    valList);
            final String strFeedbackWrt = String.format("Writing file for %s which has %s values",
                    keyDataType,
                    valList.size());
            LogExposureClass.LOGGER.info(strFeedbackWrt);
            final Map<String, Long> sorted = BasicStructuresClass.ListAndMapSubClass.getWordCounts(
                    valList, "(_| )");
            FileContentClass.ContentWritingSubClass.writeLinkedHashMapToCsvFile(
                    strFileName.replace(".csv", "__words.csv"),
                    "DataType,Word,Occurrences",
                    sorted);
        });
    }

    @Override
    public void run() {
        final String[] inFiles = optFileNames.getInFileNames();
        for (final String strFileName : inFiles) {
            storeWordFrequencyIntoCsvFile(strFileName, 3, 4);
        }
    }

    /**
     * Constructor
     */
    protected AnalyzeColumnsFromCsvFiles() {
        // intentionally blank
    }

}
