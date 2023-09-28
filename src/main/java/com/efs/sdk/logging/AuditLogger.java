/*
Copyright (C) 2023 e:fs TechHub GmbH (sdk@efs-techhub.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.efs.sdk.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuditLogger {
    /**
     * Marker for AUDIT Events
     */
    private static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

    /**
     * Empty Constructor
     */
    private AuditLogger() {
    }

    /**
     * Audit Info Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param user User information, preferable the User ID from eg. token.getToken().getSubject()
     * @param objects the additional information for the used format string
     */
    public static void info(Logger logger, String message, String user, Object... objects) {
        String m = generateMessage(message);
        logger.info(AUDIT, m, getParameters(user, objects));
    }

    /**
     * Audit Info Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param token the received JWTAuthenticationToken
     * @param objects the additional information for the used format string
     */
    public static void info(Logger logger, String message, JwtAuthenticationToken token, Object... objects) {
        info(logger, message, token.getToken().getSubject(), objects);
    }

    /**
     * Audit Warning Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param user User information, preferable the User ID from eg. token.getToken().getSubject()
     * @param objects the additional information for the used format string
     */
    public static void warning(Logger logger, String message, String user, Object... objects) {
        String m = generateMessage(message);
        logger.warn(AUDIT, m, getParameters(user, objects));
    }

    /**
     * Audit Warning Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param token the received JWTAuthenticationToken
     * @param objects the additional information for the used format string
     */
    public static void warning(Logger logger, String message, JwtAuthenticationToken token, Object... objects) {
        warning(logger, message, token.getToken().getSubject(), objects);
    }

    /**
     * Audit Error Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param user User information, preferable the User ID from eg. token.getToken().getSubject()
     * @param objects the additional information for the used format string
     */
    public static void error(Logger logger, String message, String user, Object... objects) {
        String m = generateMessage(message);
        logger.error(AUDIT, m, getParameters(user, objects));
    }

    /**
     * Audit Error Logger
     * @param logger the used logger for the executing class (LoggerFactory.getLogger(CLAZZ.class);)
     * @param message additional logging message, can contain formatting markers {}
     * @param token the received JWTAuthenticationToken
     * @param objects the additional information for the used format string
     */
    public static void error(Logger logger, String message, JwtAuthenticationToken token, Object... objects) {
        error(logger, message, token.getToken().getSubject(), objects);
    }

    /**
     * generate the audit log string
     * @param message additional log message
     * @return enriched audit log
     */
    private static String generateMessage(String message) {
        return "AUDIT: UserID '{}' - " + message;
    }

    /**
     * Prepend user to objects array
     * @param user UserId
     * @param objects Additional logging information
     * @return combined object array
     */
    private static Object[] getParameters(String user, Object[] objects) {
        Object[] combinedArray = new Object[objects.length + 1];
        combinedArray[0] = user;
        System.arraycopy(objects, 0, combinedArray, 1, objects.length);
        return combinedArray;
    }

}
