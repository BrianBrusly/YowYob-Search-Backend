package com.yowyob.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration du Load Balancing
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Configure la répartition de charge entre les instances de services
 *          Intègre avec Spring Cloud LoadBalancer
 */
@Configuration
@LoadBalancerClient(name = "api-gateway")
public class LoadBalancerConfig {

    /**
     * Fournisseur de liste d'instances de service avec health checking
     *
     * @return ServiceInstanceListSupplier configuré
     */
    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        return ServiceInstanceListSupplier.builder()
                .withDiscoveryClient()
                .withHealthChecks()
                .build(context);
    }

    /**
     * WebClient avec load balancing intégré
     *
     * @param builder WebClient.Builder
     * @return WebClient configuré avec load balancing
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder(WebClient.Builder builder) {
        return builder
                .filter((request, next) -> {
                    // Ajouter des headers de tracing
                    return next.exchange(request);
                });
    }

    /**
     * Configuration pour le load balancing réactif
     *
     * @return ReactorLoadBalancerExchangeFilterFunction
     */
    @Bean
    public org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction(
            org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory clientFactory) {

        return new org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction(
                clientFactory, java.util.Collections.emptyList());
    }
}