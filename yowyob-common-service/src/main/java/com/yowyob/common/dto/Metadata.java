package com.yowyob.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Métadonnées des réponses API
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Informations contextuelles sur la réponse
 * pour monitoring, debugging et analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {

    private String version;
    private Long executionTimeMs;
    private String serverId;
    private String environment;
    private String requestId;
    private String traceId;

    public static Metadata withExecutionTime(long executionTimeMs) {
        return Metadata.builder()
                .executionTimeMs(executionTimeMs)
                .build();
    }
}