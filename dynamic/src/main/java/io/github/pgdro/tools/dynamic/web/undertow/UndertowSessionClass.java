/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.dynamic.web.undertow;

import io.github.pgdro.tools.core.time.ZoneDataServiceClass;
import io.github.pgdro.tools.core.web.SelectInputClass;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.session.InMemorySessionManager;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionAttachmentHandler;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.util.Sessions;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

/**
 * Template management
 */
public final class UndertowSessionClass {
    /** Session Manager handle */
    private static final InMemorySessionManager SESSION_MANAGER = new InMemorySessionManager("SESSION_MANAGER");
    /** Session Config handle */
    private static final SessionCookieConfig SESSION_CONFIG = new SessionCookieConfig();
    /** Session variable */
    private static Session session;

    // Private constructor to prevent instantiation
    private UndertowSessionClass() {
        // intentionally blank
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
        final String multiValueGlue = SelectInputClass.getMultiValueGlue();
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
     * Getter for session
     * @return Session
     */
    public static Session getSession() {
        return session;
    }

    /**
     * Getter Session Handler
     * @param routesHandler routes
     * @return HttpHandler
     */
    public static HttpHandler getSessionHandler(final PathHandler routesHandler) {
        return new SessionAttachmentHandler(routesHandler, SESSION_MANAGER, SESSION_CONFIG);
    }

    /**
     * Time Zone set logic
     */
    public static void handleTimeZoneSession() {
        final Map<String, Deque<String>> queryParams = UndertowParametersClass.getQueryParameters();
        if (queryParams.get("TZ") != null) {
            session.setAttribute("TZ", queryParams.get("TZ").getFirst());
        }
        if (session.getAttribute("TZ") == null) {
            final SequencedMap<String, String> sortedTimeZones = ZoneDataServiceClass.loadSupportedTimeZones();
            final String crtUserTimeZone = System.getProperty("user.timezone");
            if (crtUserTimeZone != null
                    && !sortedTimeZones.getOrDefault(crtUserTimeZone, "").isEmpty()) {
                session.setAttribute("TZ", crtUserTimeZone);
            } else {
                session.setAttribute("TZ", "Asia/Kolkata");
            }
        }
    }

    /**
     * Session initializing
     * @param inExchange input Exchange
     */
    public static void initializeSession(final HttpServerExchange inExchange) {
        session = Sessions.getOrCreateSession(inExchange);
    }

    /**
     * Session Attribute setter
     * @param strAttribute attribute identifier
     * @param strValue value to be set
     */
    public static void setSessionAttribute(final String strAttribute, final String strValue) {
        session.setAttribute(strAttribute, strValue);
    }

}
