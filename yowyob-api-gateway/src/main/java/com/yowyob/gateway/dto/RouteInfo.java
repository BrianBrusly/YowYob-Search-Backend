package com.yowyob.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO représentant les informations d'une route du Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteInfo {

    private String id;
    private String uri;
    private List<String> predicates;
    private List<String> filters;
    private int order;
    private Map<String, Object> metadata;
    private String description;
    private String createdDate;
    private String lastModifiedDate;
}