/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.dynamic.web.html;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.ProjectClass;
import io.github.pgdro.tools.core.time.ZoneDataServiceClass;
import java.util.Map;
import java.util.Properties;
import java.util.SequencedMap;
import org.jspecify.annotations.NonNull;

/**
 * HTML generating logic
 */
public final class HtmlClass {

    /**
     * Application Details
     * @return Content
     */
    public static @NonNull String buildApplicationCopyright() {
        final String prjFirstDeveloper = ProjectClass.getFirstDeveloper();
        return "© by " + prjFirstDeveloper;
    }

    /**
     * Application Details
     * @return Content
     */
    public static @NonNull String buildApplicationDetail() {
        final String prjVersion = ProjectClass.getProjectVersion();
        return ProjectClass.getProjectName() + "™ v." + prjVersion;
    }

    /**
     * Geographical Coordinates from TZ
     * @return String
     */
    public static @NonNull String buildGeographicalCoordinatesFromTimeZone(final String sessionTimeZone) {
        final ZoneDataServiceClass.ZoneInfoRecord zInfo = ZoneDataServiceClass.get(sessionTimeZone);
        return zInfo == null ? "0,0" : zInfo.latitude() + "," + zInfo.longitude();
    }

    /**
     * Building Time-Zone select as String
     * @return String w. TZ select
     */
    public static String buildMenuString(final SequencedMap<String, Map<String, String>> inMapMenu) {
        final StringBuilder strMenuContent = new StringBuilder(1000);
        inMapMenu.forEach((strKey, mapValue) -> {
            if (!mapValue.getOrDefault(ConfigurationClass.STR_MENU, "").isEmpty()) {
                strMenuContent.append(String.format("<li><a href=\"?page=%s\"><i class=\"%s\"></i>%s</a></li>",
                        strKey,
                        mapValue.get(ConfigurationClass.STR_ICON),
                        mapValue.get(ConfigurationClass.STR_MENU)));
            }
        });
        return strMenuContent.toString();
    }

    /**
     * Building Time-Zone select
     * @return Content
     */
    public static String buildTimeZoneSelect(final String inTimeZone) {
        final SequencedMap<String, String> sortedTimeZones = ZoneDataServiceClass.loadSupportedTimeZones();
        final Properties selectProps = new Properties();
        selectProps.put(ConfigurationClass.STR_NAME, "TZ");
        selectProps.put("Id", "TZ");
        selectProps.put(ConfigurationClass.STR_DEFAULT, inTimeZone);
        selectProps.put(ConfigurationClass.STR_SIZE, 1);
        selectProps.put("AutoSubmit", 1);
        return SelectInputClass.buildSelectInput(sortedTimeZones, selectProps);
    }

    /**
     * constructor
     */
    private HtmlClass() {
        // intentionally left blank
    }

}
