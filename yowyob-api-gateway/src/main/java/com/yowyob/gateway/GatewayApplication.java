package com.yowyob.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Classe principale de l'API Gateway YowYob Search
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Point d'entrée unique pour tous les microservices YowYob Search.
 * Fournit le routage, l'authentification, le rate limiting, le circuit breaker,
 * et d'autres fonctionnalités de gateway.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	/**
	 * Méthode principale de lancement de l'API Gateway
	 *
	 * @param args Arguments de ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	/**
	 * Message de bienvenue affiché au démarrage
	 */
	private static void printWelcomeMessage() {
		System.out.println("""
            ============================================
                   YOWYOB SEARCH API GATEWAY
            ============================================
            Version: 1.0.0
            Port: 8080
            Profile: %s
            Team: YowYob Team 4GI-ENSPY Promo 2027
            Author: HEUDEP DJANDJA BRIAN B 22P405
            ============================================
            """.formatted(System.getProperty("spring.profiles.active", "default")));
	}
}