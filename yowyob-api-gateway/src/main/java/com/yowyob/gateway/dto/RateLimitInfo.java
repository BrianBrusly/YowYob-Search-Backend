package com.yowyob.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO représentant les informations de rate limiting
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitInfo {

    private String key;
    private int limit;
    private int remaining;
    private Instant resetAt;
    private Instant requestedAt;
    private boolean allowed;
    private String type; // USER, IP, API_KEY
    private String route;
}