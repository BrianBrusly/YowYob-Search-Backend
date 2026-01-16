package com.yowyob.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception pour les routes non trouvées
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class RouteNotFoundException extends GatewayException {

    public RouteNotFoundException(String path) {
        super(
                HttpStatus.NOT_FOUND,
                "ROUTE_NOT_FOUND",
                String.format("No route found for path: %s", path),
                path,
                "api-gateway");
    }

    public RouteNotFoundException(String path, Throwable cause) {
        super(
                HttpStatus.NOT_FOUND,
                "ROUTE_NOT_FOUND",
                String.format("No route found for path: %s", path),
                path,
                "api-gateway",
                cause);
    }
}