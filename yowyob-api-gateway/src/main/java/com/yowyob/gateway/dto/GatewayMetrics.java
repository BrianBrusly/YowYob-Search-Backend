package com.yowyob.gateway.dto;

import java.util.List;
import java.util.Map;
import java.time.Instant;

/**
 * DTO représentant les métriques du Gateway
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class GatewayMetrics {

    private Overview overview;
    private List<RouteMetrics> routes;
    private List<CircuitBreakerMetrics> circuitBreakers;
    private CacheMetrics cache;
    private SystemMetrics system;

    public GatewayMetrics() {
    }

    public GatewayMetrics(Overview overview, List<RouteMetrics> routes, List<CircuitBreakerMetrics> circuitBreakers,
            CacheMetrics cache, SystemMetrics system) {
        this.overview = overview;
        this.routes = routes;
        this.circuitBreakers = circuitBreakers;
        this.cache = cache;
        this.system = system;
    }

    public static GatewayMetricsBuilder builder() {
        return new GatewayMetricsBuilder();
    }

    public static class GatewayMetricsBuilder {
        private Overview overview;
        private List<RouteMetrics> routes;
        private List<CircuitBreakerMetrics> circuitBreakers;
        private CacheMetrics cache;
        private SystemMetrics system;

        public GatewayMetricsBuilder overview(Overview overview) {
            this.overview = overview;
            return this;
        }

        public GatewayMetricsBuilder routes(List<RouteMetrics> routes) {
            this.routes = routes;
            return this;
        }

        public GatewayMetricsBuilder circuitBreakers(List<CircuitBreakerMetrics> circuitBreakers) {
            this.circuitBreakers = circuitBreakers;
            return this;
        }

        public GatewayMetricsBuilder cache(CacheMetrics cache) {
            this.cache = cache;
            return this;
        }

        public GatewayMetricsBuilder system(SystemMetrics system) {
            this.system = system;
            return this;
        }

        public GatewayMetrics build() {
            return new GatewayMetrics(overview, routes, circuitBreakers, cache, system);
        }
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public List<RouteMetrics> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteMetrics> routes) {
        this.routes = routes;
    }

    public List<CircuitBreakerMetrics> getCircuitBreakers() {
        return circuitBreakers;
    }

    public void setCircuitBreakers(List<CircuitBreakerMetrics> circuitBreakers) {
        this.circuitBreakers = circuitBreakers;
    }

    public CacheMetrics getCache() {
        return cache;
    }

    public void setCache(CacheMetrics cache) {
        this.cache = cache;
    }

    public SystemMetrics getSystem() {
        return system;
    }

    public void setSystem(SystemMetrics system) {
        this.system = system;
    }

    public static class Overview {
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private double averageResponseTime;
        private double p95ResponseTime;
        private double p99ResponseTime;
        private double errorRate;
        private long activeConnections;
        private long requestsPerSecond;

        // Manual implementation omitted for brevity in thought, but must be full in
        // call
        // I will implement full code in the tool call
        public Overview() {
        }

        public Overview(long totalRequests, long successfulRequests, long failedRequests, double averageResponseTime,
                double p95ResponseTime, double p99ResponseTime, double errorRate, long activeConnections,
                long requestsPerSecond) {
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.averageResponseTime = averageResponseTime;
            this.p95ResponseTime = p95ResponseTime;
            this.p99ResponseTime = p99ResponseTime;
            this.errorRate = errorRate;
            this.activeConnections = activeConnections;
            this.requestsPerSecond = requestsPerSecond;
        }

        public static OverviewBuilder builder() {
            return new OverviewBuilder();
        }

        public static class OverviewBuilder {
            private long totalRequests;
            private long successfulRequests;
            private long failedRequests;
            private double averageResponseTime;
            private double p95ResponseTime;
            private double p99ResponseTime;
            private double errorRate;
            private long activeConnections;
            private long requestsPerSecond;

            public OverviewBuilder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public OverviewBuilder successfulRequests(long successfulRequests) {
                this.successfulRequests = successfulRequests;
                return this;
            }

            public OverviewBuilder failedRequests(long failedRequests) {
                this.failedRequests = failedRequests;
                return this;
            }

            public OverviewBuilder averageResponseTime(double averageResponseTime) {
                this.averageResponseTime = averageResponseTime;
                return this;
            }

            public OverviewBuilder p95ResponseTime(double p95ResponseTime) {
                this.p95ResponseTime = p95ResponseTime;
                return this;
            }

            public OverviewBuilder p99ResponseTime(double p99ResponseTime) {
                this.p99ResponseTime = p99ResponseTime;
                return this;
            }

            public OverviewBuilder errorRate(double errorRate) {
                this.errorRate = errorRate;
                return this;
            }

            public OverviewBuilder activeConnections(long activeConnections) {
                this.activeConnections = activeConnections;
                return this;
            }

            public OverviewBuilder requestsPerSecond(long requestsPerSecond) {
                this.requestsPerSecond = requestsPerSecond;
                return this;
            }

            public Overview build() {
                return new Overview(totalRequests, successfulRequests, failedRequests, averageResponseTime,
                        p95ResponseTime, p99ResponseTime, errorRate, activeConnections, requestsPerSecond);
            }
        }

        public long getTotalRequests() {
            return totalRequests;
        }

        public void setTotalRequests(long totalRequests) {
            this.totalRequests = totalRequests;
        }

        public long getSuccessfulRequests() {
            return successfulRequests;
        }

        public void setSuccessfulRequests(long successfulRequests) {
            this.successfulRequests = successfulRequests;
        }

        public long getFailedRequests() {
            return failedRequests;
        }

        public void setFailedRequests(long failedRequests) {
            this.failedRequests = failedRequests;
        }

        public double getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }

        public double getP95ResponseTime() {
            return p95ResponseTime;
        }

        public void setP95ResponseTime(double p95ResponseTime) {
            this.p95ResponseTime = p95ResponseTime;
        }

        public double getP99ResponseTime() {
            return p99ResponseTime;
        }

        public void setP99ResponseTime(double p99ResponseTime) {
            this.p99ResponseTime = p99ResponseTime;
        }

        public double getErrorRate() {
            return errorRate;
        }

        public void setErrorRate(double errorRate) {
            this.errorRate = errorRate;
        }

        public long getActiveConnections() {
            return activeConnections;
        }

        public void setActiveConnections(long activeConnections) {
            this.activeConnections = activeConnections;
        }

        public long getRequestsPerSecond() {
            return requestsPerSecond;
        }

        public void setRequestsPerSecond(long requestsPerSecond) {
            this.requestsPerSecond = requestsPerSecond;
        }
    }

    public static class RouteMetrics {
        private String routeId;
        private String uri;
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private double averageResponseTime;
        private Map<String, Long> statusCodes;
        private long cacheHits;
        private long cacheMisses;

        public RouteMetrics() {
        }

        public RouteMetrics(String routeId, String uri, long totalRequests, long successfulRequests,
                long failedRequests, double averageResponseTime, Map<String, Long> statusCodes, long cacheHits,
                long cacheMisses) {
            this.routeId = routeId;
            this.uri = uri;
            this.totalRequests = totalRequests;
            this.successfulRequests = successfulRequests;
            this.failedRequests = failedRequests;
            this.averageResponseTime = averageResponseTime;
            this.statusCodes = statusCodes;
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String routeId;
            private String uri;
            private long totalRequests;
            private long successfulRequests;
            private long failedRequests;
            private double averageResponseTime;
            private Map<String, Long> statusCodes;
            private long cacheHits;
            private long cacheMisses;

            public Builder routeId(String routeId) {
                this.routeId = routeId;
                return this;
            }

            public Builder uri(String uri) {
                this.uri = uri;
                return this;
            }

            public Builder totalRequests(long totalRequests) {
                this.totalRequests = totalRequests;
                return this;
            }

            public Builder successfulRequests(long successfulRequests) {
                this.successfulRequests = successfulRequests;
                return this;
            }

            public Builder failedRequests(long failedRequests) {
                this.failedRequests = failedRequests;
                return this;
            }

            public Builder averageResponseTime(double averageResponseTime) {
                this.averageResponseTime = averageResponseTime;
                return this;
            }

            public Builder statusCodes(Map<String, Long> statusCodes) {
                this.statusCodes = statusCodes;
                return this;
            }

            public Builder cacheHits(long cacheHits) {
                this.cacheHits = cacheHits;
                return this;
            }

            public Builder cacheMisses(long cacheMisses) {
                this.cacheMisses = cacheMisses;
                return this;
            }

            public RouteMetrics build() {
                return new RouteMetrics(routeId, uri, totalRequests, successfulRequests, failedRequests,
                        averageResponseTime, statusCodes, cacheHits, cacheMisses);
            }
        }

        public String getRouteId() {
            return routeId;
        }

        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public long getTotalRequests() {
            return totalRequests;
        }

        public void setTotalRequests(long totalRequests) {
            this.totalRequests = totalRequests;
        }

        public long getSuccessfulRequests() {
            return successfulRequests;
        }

        public void setSuccessfulRequests(long successfulRequests) {
            this.successfulRequests = successfulRequests;
        }

        public long getFailedRequests() {
            return failedRequests;
        }

        public void setFailedRequests(long failedRequests) {
            this.failedRequests = failedRequests;
        }

        public double getAverageResponseTime() {
            return averageResponseTime;
        }

        public void setAverageResponseTime(double averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
        }

        public Map<String, Long> getStatusCodes() {
            return statusCodes;
        }

        public void setStatusCodes(Map<String, Long> statusCodes) {
            this.statusCodes = statusCodes;
        }

        public long getCacheHits() {
            return cacheHits;
        }

        public void setCacheHits(long cacheHits) {
            this.cacheHits = cacheHits;
        }

        public long getCacheMisses() {
            return cacheMisses;
        }

        public void setCacheMisses(long cacheMisses) {
            this.cacheMisses = cacheMisses;
        }
    }

    public static class CircuitBreakerMetrics {
        private String name;
        private String state;
        private long totalCalls;
        private float failureRate;
        private long notPermittedCalls;
        private Instant lastStateChange;

        public CircuitBreakerMetrics() {
        }

        public CircuitBreakerMetrics(String name, String state, long totalCalls, float failureRate,
                long notPermittedCalls, Instant lastStateChange) {
            this.name = name;
            this.state = state;
            this.totalCalls = totalCalls;
            this.failureRate = failureRate;
            this.notPermittedCalls = notPermittedCalls;
            this.lastStateChange = lastStateChange;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String name;
            private String state;
            private long totalCalls;
            private float failureRate;
            private long notPermittedCalls;
            private Instant lastStateChange;

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder state(String state) {
                this.state = state;
                return this;
            }

            public Builder totalCalls(long totalCalls) {
                this.totalCalls = totalCalls;
                return this;
            }

            public Builder failureRate(float failureRate) {
                this.failureRate = failureRate;
                return this;
            }

            public Builder notPermittedCalls(long notPermittedCalls) {
                this.notPermittedCalls = notPermittedCalls;
                return this;
            }

            public Builder lastStateChange(Instant lastStateChange) {
                this.lastStateChange = lastStateChange;
                return this;
            }

            public CircuitBreakerMetrics build() {
                return new CircuitBreakerMetrics(name, state, totalCalls, failureRate, notPermittedCalls,
                        lastStateChange);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public long getTotalCalls() {
            return totalCalls;
        }

        public void setTotalCalls(long totalCalls) {
            this.totalCalls = totalCalls;
        }

        public float getFailureRate() {
            return failureRate;
        }

        public void setFailureRate(float failureRate) {
            this.failureRate = failureRate;
        }

        public long getNotPermittedCalls() {
            return notPermittedCalls;
        }

        public void setNotPermittedCalls(long notPermittedCalls) {
            this.notPermittedCalls = notPermittedCalls;
        }

        public Instant getLastStateChange() {
            return lastStateChange;
        }

        public void setLastStateChange(Instant lastStateChange) {
            this.lastStateChange = lastStateChange;
        }
    }

    public static class CacheMetrics {
        private long totalEntries;
        private long hitCount;
        private long missCount;
        private double hitRate;
        private long evictionCount;
        private Map<String, Long> entriesByType;

        public CacheMetrics() {
        }

        public CacheMetrics(long totalEntries, long hitCount, long missCount, double hitRate, long evictionCount,
                Map<String, Long> entriesByType) {
            this.totalEntries = totalEntries;
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.hitRate = hitRate;
            this.evictionCount = evictionCount;
            this.entriesByType = entriesByType;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private long totalEntries;
            private long hitCount;
            private long missCount;
            private double hitRate;
            private long evictionCount;
            private Map<String, Long> entriesByType;

            public Builder totalEntries(long totalEntries) {
                this.totalEntries = totalEntries;
                return this;
            }

            public Builder hitCount(long hitCount) {
                this.hitCount = hitCount;
                return this;
            }

            public Builder missCount(long missCount) {
                this.missCount = missCount;
                return this;
            }

            public Builder hitRate(double hitRate) {
                this.hitRate = hitRate;
                return this;
            }

            public Builder evictionCount(long evictionCount) {
                this.evictionCount = evictionCount;
                return this;
            }

            public Builder entriesByType(Map<String, Long> entriesByType) {
                this.entriesByType = entriesByType;
                return this;
            }

            public CacheMetrics build() {
                return new CacheMetrics(totalEntries, hitCount, missCount, hitRate, evictionCount, entriesByType);
            }
        }

        public long getTotalEntries() {
            return totalEntries;
        }

        public void setTotalEntries(long totalEntries) {
            this.totalEntries = totalEntries;
        }

        public long getHitCount() {
            return hitCount;
        }

        public void setHitCount(long hitCount) {
            this.hitCount = hitCount;
        }

        public long getMissCount() {
            return missCount;
        }

        public void setMissCount(long missCount) {
            this.missCount = missCount;
        }

        public double getHitRate() {
            return hitRate;
        }

        public void setHitRate(double hitRate) {
            this.hitRate = hitRate;
        }

        public long getEvictionCount() {
            return evictionCount;
        }

        public void setEvictionCount(long evictionCount) {
            this.evictionCount = evictionCount;
        }

        public Map<String, Long> getEntriesByType() {
            return entriesByType;
        }

        public void setEntriesByType(Map<String, Long> entriesByType) {
            this.entriesByType = entriesByType;
        }
    }

    public static class SystemMetrics {
        private double cpuUsage;
        private long usedMemory;
        private long totalMemory;
        private long maxMemory;
        private int activeThreads;
        private long uptime;

        public SystemMetrics() {
        }

        public SystemMetrics(double cpuUsage, long usedMemory, long totalMemory, long maxMemory, int activeThreads,
                long uptime) {
            this.cpuUsage = cpuUsage;
            this.usedMemory = usedMemory;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.activeThreads = activeThreads;
            this.uptime = uptime;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private double cpuUsage;
            private long usedMemory;
            private long totalMemory;
            private long maxMemory;
            private int activeThreads;
            private long uptime;

            public Builder cpuUsage(double cpuUsage) {
                this.cpuUsage = cpuUsage;
                return this;
            }

            public Builder usedMemory(long usedMemory) {
                this.usedMemory = usedMemory;
                return this;
            }

            public Builder totalMemory(long totalMemory) {
                this.totalMemory = totalMemory;
                return this;
            }

            public Builder maxMemory(long maxMemory) {
                this.maxMemory = maxMemory;
                return this;
            }

            public Builder activeThreads(int activeThreads) {
                this.activeThreads = activeThreads;
                return this;
            }

            public Builder uptime(long uptime) {
                this.uptime = uptime;
                return this;
            }

            public SystemMetrics build() {
                return new SystemMetrics(cpuUsage, usedMemory, totalMemory, maxMemory, activeThreads, uptime);
            }
        }

        public double getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(double cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public long getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(long usedMemory) {
            this.usedMemory = usedMemory;
        }

        public long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(long maxMemory) {
            this.maxMemory = maxMemory;
        }

        public int getActiveThreads() {
            return activeThreads;
        }

        public void setActiveThreads(int activeThreads) {
            this.activeThreads = activeThreads;
        }

        public long getUptime() {
            return uptime;
        }

        public void setUptime(long uptime) {
            this.uptime = uptime;
        }
    }
}