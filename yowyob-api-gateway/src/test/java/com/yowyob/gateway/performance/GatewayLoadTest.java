package com.yowyob.gateway.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Test de charge pour le Gateway avec Gatling
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 * 
 *          Simulation de charge pour valider les performances du Gateway
 */
public class GatewayLoadTest extends Simulation {

        // Configuration HTTP
        HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .authorizationHeader("Bearer test-token")
                        .userAgentHeader("Gatling Load Test")
                        .shareConnections();

        // Headers communs
        java.util.Map<CharSequence, String> headers = java.util.Map.of(
                        "Content-Type", "application/json",
                        "X-User-Id", "load-test-user",
                        "X-User-Roles", "USER");

        // Scénario: Test de recherche
        ScenarioBuilder searchScenario = scenario("Search Load Test")
                        .exec(http("Search Request")
                                        .get("/api/search")
                                        .queryParam("q", "restaurant Yaoundé")
                                        .headers(headers)
                                        .check(status().is(200))
                                        .check(responseTimeInMillis().lte(500)) // p95 < 500ms
                        )
                        .pause(1); // Pause de 1 seconde entre les requêtes

        // Scénario: Test de profil utilisateur
        ScenarioBuilder userProfileScenario = scenario("User Profile Load Test")
                        .exec(http("User Profile Request")
                                        .get("/api/users/profile")
                                        .headers(headers)
                                        .check(status().is(200))
                                        .check(responseTimeInMillis().lte(300)) // p95 < 300ms
                        )
                        .pause(2);

        // Scénario: Test de géolocalisation
        ScenarioBuilder geoScenario = scenario("Geo Load Test")
                        .exec(http("Geo Request")
                                        .get("/api/geo/reverse")
                                        .queryParam("lat", "4.05")
                                        .queryParam("lon", "9.7")
                                        .headers(headers)
                                        .check(status().is(200))
                                        .check(responseTimeInMillis().lte(400)) // p95 < 400ms
                        )
                        .pause(1);

        // Scénario mixte (simulation d'usage réel)
        ScenarioBuilder mixedScenario = scenario("Mixed Load Test")
                        .exec(
                                        http("Search Request")
                                                        .get("/api/search?q=test")
                                                        .headers(headers)
                                                        .check(status().is(200)),
                                        pause(1),
                                        http("User Profile")
                                                        .get("/api/users/profile")
                                                        .headers(headers)
                                                        .check(status().is(200)),
                                        pause(2),
                                        http("Geo Request")
                                                        .get("/api/geo/reverse?lat=4.05&lon=9.7")
                                                        .headers(headers)
                                                        .check(status().is(200)));

        {
                // Configuration de la simulation
                setUp(
                                // Test de recherche: 1000 utilisateurs sur 5 minutes
                                searchScenario.injectOpen(
                                                rampUsersPerSec(1).to(50).during(Duration.ofMinutes(1)),
                                                constantUsersPerSec(50).during(Duration.ofMinutes(3)),
                                                rampUsersPerSec(50).to(1).during(Duration.ofMinutes(1))),

                                // Test de profil: 500 utilisateurs sur 5 minutes
                                userProfileScenario.injectOpen(
                                                rampUsersPerSec(1).to(25).during(Duration.ofMinutes(1)),
                                                constantUsersPerSec(25).during(Duration.ofMinutes(3)),
                                                rampUsersPerSec(25).to(1).during(Duration.ofMinutes(1))),

                                // Test mixte: 200 utilisateurs sur 10 minutes
                                mixedScenario.injectOpen(
                                                rampUsersPerSec(1).to(10).during(Duration.ofMinutes(2)),
                                                constantUsersPerSec(10).during(Duration.ofMinutes(6)),
                                                rampUsersPerSec(10).to(1).during(Duration.ofMinutes(2))))
                                .protocols(httpProtocol)
                                .assertions(
                                                // Assertions globales
                                                global().responseTime().percentile3().lte(500), // p95 < 500ms
                                                global().responseTime().percentile4().lte(1000), // p99 < 1s
                                                global().responseTime().max().lte(5000), // Max < 5s
                                                global().successfulRequests().percent().gte(99.5), // Success rate >
                                                                                                   // 99.5%

                                                // Assertions par scénario
                                                details("Search Request").responseTime().percentile3().lte(500),
                                                details("Search Request").successfulRequests().percent().gte(99.0),

                                                details("User Profile Request").responseTime().percentile3().lte(300),
                                                details("User Profile Request").successfulRequests().percent()
                                                                .gte(99.5),

                                                details("Geo Request").responseTime().percentile3().lte(400),
                                                details("Geo Request").successfulRequests().percent().gte(99.0))
                                .maxDuration(Duration.ofMinutes(15));
        }

        // Méthode pour exécuter le test en mode dégradé (avec services partiellement
        // down)
        public static class DegradedLoadTest extends Simulation {

                HttpProtocolBuilder httpProtocol = http
                                .baseUrl("http://localhost:8080")
                                .acceptHeader("application/json");

                ScenarioBuilder degradedScenario = scenario("Degraded Load Test")
                                .exec(
                                                http("Search with Circuit Breaker")
                                                                .get("/api/search?q=test")
                                                                .check(status().in(200, 503)) // Accepte soit succès
                                                                                              // soit circuit breaker
                                                                                              // ouvert
                                );

                {
                        setUp(
                                        degradedScenario.injectOpen(
                                                        rampUsersPerSec(1).to(100).during(Duration.ofMinutes(2)),
                                                        constantUsersPerSec(100).during(Duration.ofMinutes(5))))
                                        .protocols(httpProtocol)
                                        .assertions(
                                                        global().responseTime().percentile3().lte(100), // Rapide quand
                                                                                                        // circuit
                                                                                                        // breaker
                                                                                                        // ouvert
                                                        global().failedRequests().percent().lte(10.0) // Max 10%
                                                                                                      // d'échecs
                                                                                                      // acceptables
                                        );
                }
        }

        // Méthode pour tester le rate limiting
        public static class RateLimitLoadTest extends Simulation {

                HttpProtocolBuilder httpProtocol = http
                                .baseUrl("http://localhost:8080")
                                .acceptHeader("application/json");

                ScenarioBuilder rateLimitScenario = scenario("Rate Limit Test")
                                .exec(
                                                http("Fast Requests")
                                                                .get("/api/search")
                                                                .check(status().in(200, 429)) // Accepte soit succès
                                                                                              // soit rate limit
                                );

                {
                        setUp(
                                        rateLimitScenario.injectOpen(
                                                        constantUsersPerSec(20).during(Duration.ofMinutes(2)) // 20
                                                                                                              // req/s >
                                                                                                              // limite
                                                                                                              // de
                                                                                                              // 10/min
                                        ))
                                        .protocols(httpProtocol)
                                        .assertions(
                                                        details("Fast Requests").requestsPerSec().gte(10.0),
                                                        details("Fast Requests").failedRequests().percent().gte(50.0) // Au
                                                                                                                      // moins
                                                                                                                      // 50%
                                                                                                                      // rate
                                                                                                                      // limited
                                        );
                }
        }
}