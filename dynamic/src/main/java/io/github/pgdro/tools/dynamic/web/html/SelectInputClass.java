package io.github.pgdro.tools.dynamic.web.html;

import io.github.pgdro.tools.core.ConfigurationClass;
import io.github.pgdro.tools.core.LogExposureClass;
import io.github.pgdro.tools.dynamic.web.undertow.UndertowSessionClass;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
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
     * building list of values
     * @param filterIdentifier filter identifier
     * @param queryParams input Parameter
     * @param smapAllValues Sequenced Map with all values
     * @return concatenated String with list of values for given filter
     */
    public static List<String> computeListOfValuesFromSessionsOrPageParameter(
            final String filterIdentifier,
            final Map<String, Deque<String>> queryParams,
            final SequencedMap<String, String> smapAllValues) {
        final String strWebId             = filterIdentifier + "[]";
        final Deque<String> pageParameter = queryParams.get(strWebId);
        List<String> listReturn           = new ArrayList<>();
        if (pageParameter != null) {
            listReturn = new ArrayList<>(pageParameter);
            UndertowSessionClass.setSessionAttribute(strWebId, String.join(multiValueGlue, pageParameter));
        } else {
            final Object sessionValues = UndertowSessionClass.getSession().getAttribute(strWebId);
            if (sessionValues != null) {
                listReturn = List.of(sessionValues.toString().split(multiValueGlue));
            }
        }
        if (listReturn.isEmpty()) {
            listReturn = new ArrayList<>(smapAllValues.keySet());
        }
        return listReturn;
    }

    /**
     * computing single Value for PageParameter/Session
     * @param filterIdentifier filter identifier
     * @param queryParams input Parameter
     * @param defaultValue default Value
     * @return return value
     */
    public static String computeSingleValueFromPageParameterOrSession(
            final String filterIdentifier,
            final Map<String, Deque<String>> queryParams,
            final String defaultValue) {
        final Deque<String> pageParameter = queryParams.get(filterIdentifier);
        String strReturn = "";
        if (pageParameter != null) {
            strReturn = pageParameter.toString();
            UndertowSessionClass.setSessionAttribute(filterIdentifier, strReturn);
        } else {
            final Object sessionValues = UndertowSessionClass.getSession().getAttribute(filterIdentifier);
            if (sessionValues != null) {
                strReturn = sessionValues.toString();
            }
        }
        return strReturn.isBlank() ? defaultValue : strReturn.replaceAll("[\\[\\]]", "");
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
