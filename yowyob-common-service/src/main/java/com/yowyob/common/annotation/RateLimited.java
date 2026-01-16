package com.yowyob.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour limiter le taux d'appels d'une méthode
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Implémente le rate limiting avec algorithme token bucket
 * Protège les APIs contre les abus et surcharges
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimited {

    int value() default 100;

    long timeWindowSeconds() default 60;

    String key() default "";
}