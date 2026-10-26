package io.github.pgdro.tools.dynamic.web.html;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.RegularExpressionsClass;
import io.github.pgdro.tools.core.time.TimingClass;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SequencedMap;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.NonNull;

/**
 * List and Maps management
 */
public final class TableClass {
    /** CSS to align text to right */
    private static final String CSS_TEXT_RIGHT_NW = "text-align:right;white-space:nowrap;font-family: sans-serif;";
    /** Minimum string length threshold for time-zone pattern replacement */
    private static final long LARGE_STRING = 20;
    /** Time Zone variable */
    private static String strInTimeZone;
    /** Time Zone variable */
    private static String strOutTimeZone;

    /**
     * constructor
     */
    private TableClass() {
        // intentionally left blank
    }

    /**
     * Generate HTML from a Map of values
     * @param inList values stored as a list
     * @return String
     */
    public static String getListOfSequencedMapIntoHtmlTable(
            final List<SequencedMap<Object, Object>> inList,
            final Properties objFeatures) {
        final TableBuildContextSubClass ctx = new TableBuildContextSubClass();
        if (strInTimeZone == null) {
            setInTimeZone(System.getProperty("user.timezone"));
        }
        if (strOutTimeZone == null) {
            setOutTimeZone(System.getProperty("user.timezone"));
        }
        ctx.strTableHeader = "";
        ctx.rememberKey = getRememberKey(objFeatures);
        ctx.counterColumnName = objFeatures.getOrDefault(ConfigurationClass.STR_COUNTER, "").toString();
        ctx.useCounter = !ctx.counterColumnName.isEmpty();
        for (final SequencedMap<Object, Object> recordMap : inList) {
            TableBuildContextSubClass.processRecord(recordMap, ctx);
        }
        ctx.finish(ctx);
        return String.join("", ctx.listTableLines);
    }

    /**
     * Map into HTML simple Table
     * @param inMap input Map
     * @return String as HTML table
     */
    public static String getMapIntoHtmlTable(final @NonNull Map<String, Object> inMap) {
        final StringBuilder sbReturn = new StringBuilder(100);
        sbReturn.append("<table>");
        inMap.forEach((crtKey, crtValue) ->
            sbReturn.append("<tr><th style=\"text-align:left;\">")
                   .append(crtKey)
                   .append("</th><td>")
                   .append(crtValue)
                   .append("</td></tr>")
        );
        sbReturn.append("</table>");
        return sbReturn.toString();
    }

    /**
     * establishing the Key to Remember if relevant
     * @param objFeatures optional HTML Table features
     * @return String
     */
    private static String getRememberKey(final Properties objFeatures) {
        String strRememberKey = "";
        if (objFeatures.containsKey(ConfigurationClass.STR_NEW_TAB)) {
            strRememberKey = objFeatures.get(ConfigurationClass.STR_NEW_TAB).toString();
        }
        return strRememberKey;
    }

    /**
     * Setter for strInTimeZone
     * @param inTimeZone input time zone
     */
    public static void setInTimeZone(final String inTimeZone) {
        strInTimeZone = inTimeZone;
        TimingClass.LocalizationSubClass.setInputTimeZone(inTimeZone);
    }

    /**
     * Setter for strInTimeZone
     * @param outTimeZone output time zone
     */
    public static void setOutTimeZone(final String outTimeZone) {
        strOutTimeZone = outTimeZone;
        TimingClass.LocalizationSubClass.setOutputTimeZone(outTimeZone);
    }

    /**
     * Rows logic
     */
    private static final class HeaderSubClass {

        /**
         * Table Body row logic
         * @param recordMap properties of the record to be transformed into HTML row
         * @return String
         */
        private static String buildTableHeader(
                final SequencedMap<Object, Object> recordMap,
                final @NonNull TableBuildContextSubClass tblContext) {
            final StringBuilder strBuilder = new StringBuilder(100);
            strBuilder.append("<table><thead>");
            if (tblContext.useCounter) {
                strBuilder.append("<th>")
                        .append(tblContext.counterColumnName)
                        .append("</th>");
            }
            recordMap.forEach((strKey, _) -> {
                if (!tblContext.rememberKey.equalsIgnoreCase(strKey.toString())
                        && !ConfigurationClass.STR_ROW_STYLE.equalsIgnoreCase(strKey.toString())) {
                    strBuilder.append("<th>")
                            .append(strKey)
                            .append("</th>");
                }
            });
            strBuilder.append("</thead><tbody>");
            return strBuilder.toString();
        }

        /**
         * initiating Table Header
         * @param recordMap records to parse
         */
        private static void ensureHeaderExists(
                final SequencedMap<Object, Object> recordMap,
                final TableBuildContextSubClass tblContext) {
            if (tblContext.strTableHeader.isEmpty()) {
                tblContext.strTableHeader = buildTableHeader(recordMap, tblContext);
            }
        }

    }

    /** Per-call mutable context to avoid shared static state. */
    private static final class TableBuildContextSubClass {
        /** Variable for current Tab */
        private String currentTabValue;
        /** Variable for lines within Table */
        private final List<String> listTableLines = new ArrayList<>();
        /** Variable for last used TabName */
        private String rememberKey;
        /** Variable for counter of Rows */
        private int rowCounter;
        /** Variable for Table Header */
        private String strTableHeader = "";
        /** Variable for counter column name */
        private String counterColumnName;
        /** Variable for counter inclusion (true/false) */
        private boolean useCounter;

        /**
         * ensuring Table Header is appended
         */
        private static void ensureHeaderAppended(final @NonNull TableBuildContextSubClass tblContext) {
            if (tblContext.listTableLines.isEmpty()) {
                tblContext.listTableLines.add(tblContext.strTableHeader);
                tblContext.rowCounter = 0;
            }
        }

        /**
         * final
         */
        private void finish(final @NonNull TableBuildContextSubClass tblContext) {
            if (!tblContext.strTableHeader.isEmpty()) {
                tblContext.listTableLines.add("</tbody></table>");
                if (!tblContext.rememberKey.isEmpty()) {
                    tblContext.listTableLines.add(
                            String.format("</div><!-- %s --></div><!-- tabStandard -->",
                                    tblContext.currentTabValue));
                }
            }
        }

        /**
         * handle Tab switch
         * @param recordMap properties of the record to be transformed into HTML row
         */
        private static void handleTabSwitch(
                final SequencedMap<Object, Object> recordMap,
                final TableBuildContextSubClass tblContext) {
            final Object valObj = recordMap.get(tblContext.rememberKey);
            final String valueForTab = valObj == null ? ConfigurationClass.STR_NULL : valObj.toString();
            final String prev = tblContext.currentTabValue == null ? "" : tblContext.currentTabValue;
            if (!valueForTab.equalsIgnoreCase(prev)) {
                if (tblContext.listTableLines.isEmpty()) {
                    // first tab: open tab container
                    tblContext.listTableLines.add("<div id=\"tabStandard\" class=\"tabber\">");
                } else if (tblContext.currentTabValue != null) {
                    // close previous tab's table
                    tblContext.listTableLines.add(
                            String.format("</tbody></table></div><!-- %s -->",
                                    tblContext.currentTabValue));
                }
                // open new tab with header
                tblContext.listTableLines.add(
                        String.format("<div class=\"tabbertab\" title=\"%s\">%s",
                                valueForTab,
                                tblContext.strTableHeader));
                tblContext.currentTabValue = valueForTab;
                tblContext.rowCounter = 0;
            }
        }

        /**
         * process each record
         * @param recordMap map with record content
         */
        private static void processRecord(
                final SequencedMap<Object, Object> recordMap,
                final TableBuildContextSubClass tblContext) {
            HeaderSubClass.ensureHeaderExists(recordMap, tblContext);
            if (tblContext.rememberKey.isEmpty()) {
                ensureHeaderAppended(tblContext);
            } else {
                handleTabSwitch(recordMap, tblContext);
            }
            if (tblContext.useCounter) {
                tblContext.rowCounter++;
                recordMap.putFirst(tblContext.counterColumnName, String.valueOf(tblContext.rowCounter));
            }
            final String crtRow = RowSubSubClass.buildTableBodyRow(recordMap, tblContext);
            tblContext.listTableLines.add(crtRow);
        }

        /**
         * Rows logic
         */
        private static final class RowSubSubClass {
            /** Variable for specialValues */
            private static final SequencedMap<String, SpValuesRecord> MAP_SPEC_VALS = new LinkedHashMap<>();
            /** Variable for conversion patterns */
            private static final List<String> LIST_PATTERNS = List.of(
                    "decimal",
                    "integer",
                    "long",
                    ConfigurationClass.STR_TS_MSEC,
                    ConfigurationClass.STR_TIMESTAMP,
                    ConfigurationClass.STR_DATE_HRMN,
                    ConfigurationClass.STR_JUST_DATE,
                    "byteSize",
                    "fullAging");

            /** Record for ZoneInfo */
            /* default */ public record SpValuesRecord(
                String newValue,
                String newStyle) {}

            static {
                loadPredefinedValuesAndTheirStyles();
            }

            /**
             * Table Body row logic
             * @param recordMap properties of the record to be transformed into HTML row
             * @return String
             */
            private static String buildTableBodyRow(
                    final SequencedMap<Object, Object> recordMap,
                    final TableBuildContextSubClass tblContext) {
                final StringBuilder strTableRow = new StringBuilder(1000);
                strTableRow.append("<tr>");
                recordMap.forEach((strKey, objValue) -> {
                    if (!tblContext.rememberKey.equalsIgnoreCase(strKey.toString())
                            && !ConfigurationClass.STR_ROW_STYLE.equalsIgnoreCase(strKey.toString())) {
                        final StringBuilder cellStyle = new StringBuilder(100);
                        if (recordMap.containsKey(ConfigurationClass.STR_ROW_STYLE)) {
                            cellStyle.append(recordMap.get(ConfigurationClass.STR_ROW_STYLE).toString());
                        }
                        final Map<String, String> mapSmartLogic = manageCellStyleAndValue(objValue);
                        final String strValue = mapSmartLogic.get("value");
                        if (!mapSmartLogic.get(ConfigurationClass.STR_STYLE).isEmpty()) {
                            cellStyle.append(mapSmartLogic.get(ConfigurationClass.STR_STYLE));
                        }
                        if (cellStyle.isEmpty()) {
                            strTableRow.append("<td>");
                        } else {
                            strTableRow.append("<td style=\"")
                                    .append(cellStyle)
                                    .append("\">");
                        }
                        strTableRow.append(strValue)
                                .append("</td>");
                    }
                });
                strTableRow.append("</tr>");
                return strTableRow.toString();
            }

            /**
             * loading special Values and CSS style
             */
            private static void loadPredefinedValuesAndTheirStyles() {
                MAP_SPEC_VALS.put(ConfigurationClass.STR_NULL,
                        new SpValuesRecord("&lt;NULL&gt;", "color:LightGrey;font-style:italic;"));
                MAP_SPEC_VALS.put("",
                        new SpValuesRecord("&lt;blank&gt;", "color:Grey;font-style:italic;"));
            }

            /**
             * Manage Cell Style and Value
             * @param inValue input value
             * @return Map
             */
            private static Map<String, String> manageCellStyleAndValue(final Object inValue) {
                String cellStyle = "";
                String strValue = inValue.toString();
                final int strLength = strValue.length();
                if (MAP_SPEC_VALS.containsKey(strValue)) {
                    cellStyle = MAP_SPEC_VALS.get(strValue).newStyle;
                    strValue  = MAP_SPEC_VALS.get(strValue).newValue;
                } else {
                    final String tempValue = transformValueByPatternMatch(strValue);
                    if (tempValue != null) {
                        cellStyle = CSS_TEXT_RIGHT_NW;
                        strValue  = tempValue;
                    } else if (strLength >= LARGE_STRING) {
                        strValue  = RegularExpressionsClass.replacePatternsWithTimeZones(strValue);
                        cellStyle = strValue.equals(inValue.toString()) ? cellStyle : CSS_TEXT_RIGHT_NW;
                    }
                }
                return Map.of(ConfigurationClass.STR_STYLE, cellStyle, "value", strValue);
            }

            /**
             * Transform 1 value by Given Pattern
             * @param inString input value
             * @return String transformed
             */
            private static String transformValueByPatternMatch(@NonNull final String inString) {
                final AtomicReference<String> strTransformed = new AtomicReference<>();
                LIST_PATTERNS.forEach(crtPattern -> {
                    if (RegularExpressionsClass
                            .ValidationSubClass
                            .isStringActuallySomething(inString, crtPattern)) {
                        final ConversionsClass cLogic = new ConversionsClass();
                        strTransformed.set(cLogic.dispatch(crtPattern, inString));
                    }
                });
                return strTransformed.get();
            }

        }

    }

}
