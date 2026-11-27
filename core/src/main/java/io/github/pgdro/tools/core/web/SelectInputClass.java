package io.github.pgdro.tools.core.web;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.log.LogExposureClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.SequencedMap;
import org.jspecify.annotations.NonNull;

/**
 * List and Maps management
 */
public final class SelectInputClass {
    /** Variable for multiple value glue character */
    /* default */ private static String multiValueGlue = "¦";

    /**
     * build Label as HTML tag
     * @param objFeatures optional HTML Table features
     * @return String
     */
    private static @NonNull String buildLabelTag(final @NonNull Properties objFeatures) {
        final String strLabel = objFeatures.getOrDefault(ConfigurationClass.STR_LABEL, "").toString()
                + (objFeatures.getOrDefault(ConfigurationClass.STR_MULTIPLE, "").toString().isEmpty()
                    ? ""
                    : "<sup>(multiple values possible)</sup>");
        final String tagLabelRaw = "<label for=\"%s\"%s>%s:</label>";
        final String strLabelStyle
                = objFeatures.getOrDefault("Label Style", "").toString().isEmpty()
                    ? ""
                    : " style=\"" + objFeatures.get("Label Style").toString() + "\"";
        return String.format(tagLabelRaw, objFeatures.get("Id"), strLabelStyle, strLabel)
                + (objFeatures.getOrDefault("Label on Same Line", "").toString().isEmpty()
                    ? "<br/>"
                    : "");
    }

    /**
     * establishing the Key to Remember if relevant
     * @param objFeatures optional HTML Table features
     * @return String
     */
    public static String buildSelectInput(
            final SequencedMap<String, String> mapValues,
            final @NonNull Properties objFeatures) {
        final List<String> outHtml = new ArrayList<>();
        if (!objFeatures.getOrDefault(ConfigurationClass.STR_LABEL, "").toString().isEmpty()) {
            outHtml.add(buildLabelTag(objFeatures));
        }
        final Properties propsInput = manageAdditionalAttributesAndDefaults(objFeatures);
        final boolean isMultiple = !objFeatures
                .getOrDefault(ConfigurationClass.STR_MULTIPLE, "")
                .toString()
                .isEmpty();
        final String strName = objFeatures.get(ConfigurationClass.STR_NAME) + (isMultiple ? "[]" : "");
        final String strFeedback = String.format(
                "For Select Input named %s features are %s and computer Properties %s",
                strName,
                objFeatures,
                propsInput);
        LogExposureClass.LOGGER.debug(strFeedback);
        outHtml.add(String.format("<select name=\"%s\" id=\"%s\"%s>",
                strName,
                objFeatures.get("Id"),
                propsInput.get("Additional")));
        final List<String> defaults = getDefaultsFromProperties(propsInput, isMultiple);
        mapValues.forEach((strValue, strText) -> {
            final String strSelected = !defaults.isEmpty() && defaults.contains(strValue) ? " selected" : "";
            outHtml.add(String.format("<option value=\"%s\"%s>%s</option>",
                    strValue,
                    strSelected,
                    strText));
        });
        outHtml.add("</select>");
        return String.join("", outHtml);
    }

    /**
     * get Default as List of Strings
     * @param propsInput input Properties
     * @param isMultiple true if multiple values are allowed
     * @return List of Strings
     */
    private static List<String> getDefaultsFromProperties(final Properties propsInput, final boolean isMultiple) {
        final String rawDefaults = propsInput.get("Defaults").toString();
        List<String> defaults = Arrays.asList(rawDefaults);
        if (isMultiple) {
            defaults = Arrays.asList(rawDefaults.split(multiValueGlue));
        }
        return defaults;
    }

    /**
     * Additional attributes logic
     * @param objFeatures packed attributes
     */
    private static Properties manageAdditionalAttributesAndDefaults(final @NonNull Properties objFeatures) {
        final Properties propsInput = new Properties();
        String additionalAttrib = "";
        if (!objFeatures.getOrDefault(ConfigurationClass.STR_MULTIPLE, "").toString().isEmpty()) {
            additionalAttrib = String.format(" multiple size=\"%s\"",
                    objFeatures.get(ConfigurationClass.STR_MULTIPLE));
        }
        if (!objFeatures.getOrDefault(ConfigurationClass.STR_SIZE, "").toString().isEmpty()) {
            additionalAttrib = String.format(" size=\"%s\"", objFeatures.get(ConfigurationClass.STR_SIZE));
        }
        final String autoSubmit = objFeatures.getOrDefault("AutoSubmit", "").toString();
        if (!autoSubmit.isEmpty()) {
             additionalAttrib += " onChange=\"javascript:this.form.submit();\"";
        }
        propsInput.put("Additional", additionalAttrib);
        propsInput.put("Defaults", objFeatures.getOrDefault(ConfigurationClass.STR_DEFAULT, "").toString());
        return propsInput;
    }

    /**
     * Getter for multiValueGlue
     */
    public static String getMultiValueGlue() {
        return multiValueGlue;
    }

    /**
     * Setter for multiValueGlue
     * @param inMultiValueGlue customized value for multiValueGlue
     */
    public static void setMultiValueGlue(final String inMultiValueGlue) {
        multiValueGlue = inMultiValueGlue;
    }

    /**
     * constructor
     */
    private SelectInputClass() {
        // intentionally left blank
    }

}
