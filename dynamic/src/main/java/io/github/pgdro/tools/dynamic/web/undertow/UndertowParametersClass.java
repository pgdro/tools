/** Copyright 2026 Daniel-Gheorghe Popiniuc */
package io.github.pgdro.tools.dynamic.web.undertow;

import io.github.pgdro.tools.core.RegularExpressionsClass;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.util.Deque;
import java.util.Map;

/**
 * Template management
 */
public final class UndertowParametersClass {
    /** Page variable */
    private static String parameterPage;
    /** page parameter variables */
    private static Map<String, Deque<String>> queryParams;

    /**
     * Getter for parameterPage
     * @return String
     */
    public static String getPageParameter() {
        return parameterPage;
    }

    /**
     * Getter for queryParams
     * @return Map
     */
    public static Map<String, Deque<String>> getQueryParameters() {
        return queryParams;
    }

    /**
     * Getting Response Header
     * @param exchange HttpServerExchange
     * @return HeaderMap
     */
    private static HeaderMap getResponseHeader(final HttpServerExchange exchange) {
        return exchange.getResponseHeaders();
    }

    /**
     * Redirecting page
     * @param exchange HttpServerExchange used to set redirect status,
     *  location header and end the exchange
     */
    public static void redirectPageIfNeeded(final HttpServerExchange exchange) {
        final Deque<String> redirectParameter = queryParams.get("redirectAction");
        if (redirectParameter != null) {
            exchange.setStatusCode(StatusCodes.SEE_OTHER); // 303 Redirect
            final HeaderMap responseHeader = getResponseHeader(exchange);
            final String redirectLocation = "/?"
                    + redirectParameter.getFirst().replaceAll(RegularExpressionsClass.REGEXP_CLEAN_CHRS, "");
            responseHeader.put(Headers.LOCATION, redirectLocation);
            exchange.endExchange();
        }
    }

    /**
     * Page parameter isolation
     */
    public static void setPageParameter() {
        final Deque<String> pageParams = queryParams.get("page");
        parameterPage = "home";
        if (pageParams != null) {
            parameterPage = pageParams.getFirst().replaceAll(RegularExpressionsClass.REGEXP_CLEAN_CHRS, "");
        }
    }

    /**
     * get query parameters into local variable
     * @param inExchange HttpServerExchange
     */
    public static void setQueryParameters(final HttpServerExchange inExchange) {
        // Get the 'page' query parameter (Deques are used for multi-value parameters)
        queryParams = inExchange.getQueryParameters();
    }

    // Private constructor to prevent instantiation
    private UndertowParametersClass() {
        // intentionally blank
    }

}
