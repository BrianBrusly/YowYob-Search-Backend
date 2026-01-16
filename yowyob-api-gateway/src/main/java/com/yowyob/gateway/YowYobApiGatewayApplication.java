package com.yowyob.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledGlobalFilter;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Point d'entrée principal de l'API Gateway
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configuration principale du Gateway réactif basé sur Spring Cloud
 *          Gateway
 *          avec WebFlux pour des performances optimales et une architecture
 *          non-bloquante
 */
@SpringBootApplication(scanBasePackages = { "com.yowyob.gateway", "com.yowyob.common.security.jwt" })
@EnableDiscoveryClient // Activation de la découverte de services (Eureka)
@EnableScheduling // Activation des tâches planifiées pour refresh routes, métriques, etc.
public class YowYobApiGatewayApplication {

	/**
	 * Méthode principale - démarre l'application Spring Boot
	 *
	 * @param args Arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(YowYobApiGatewayApplication.class, args);
	}
}