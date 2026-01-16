package com.yowyob.gateway.exception;

import com.yowyob.common.exception.AppException;
import org.springframework.http.HttpStatus;

/**
 * Exception de base pour le Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class GatewayException extends AppException {

    private final String route;
    private final String service;

    public GatewayException(
            HttpStatus status,
            String errorCode,
            String message,
            String route,
            String service) {

        super(message, errorCode, status);
        this.route = route;
        this.service = service;
    }

    public GatewayException(
            HttpStatus status,
            String errorCode,
            String message,
            String route,
            String service,
            Throwable cause) {

        super(message, cause, errorCode, status);
        this.route = route;
        this.service = service;
    }

    public String getRoute() {
        return route;
    }

    public String getService() {
        return service;
    }

    @Override
    public String toString() {
        return String.format("GatewayException{status=%s, errorCode='%s', message='%s', route='%s', service='%s'}",
                getHttpStatus(), getErrorCode(), getMessage(), route, service);
    }
}