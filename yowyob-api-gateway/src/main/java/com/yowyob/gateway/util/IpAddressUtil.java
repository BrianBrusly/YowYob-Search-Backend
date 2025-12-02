package com.yowyob.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * Utilitaire pour extraire l'adresse IP réelle du client
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Gère correctement l'extraction de l'IP même derrière des proxies,
 * load balancers (Nginx, HAProxy), CDN (Cloudflare)
 */
@Slf4j
public class IpAddressUtil {

    // Headers à vérifier dans l'ordre de priorité
    private static final List<String> IP_HEADERS = Arrays.asList(
            "X-Forwarded-For",       // Standard, proxy/load balancer
            "X-Real-IP",             // Nginx
            "Proxy-Client-IP",       // Apache
            "WL-Proxy-Client-IP",    // WebLogic
            "HTTP_X_FORWARDED_FOR",  // Squid
            "HTTP_X_FORWARDED",      // Autre variante
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "CF-Connecting-IP"       // Cloudflare
    );

    // IPs privées à ignorer
    private static final List<String> PRIVATE_IP_PREFIXES = Arrays.asList(
            "10.",
            "172.16.", "172.17.", "172.18.", "172.19.",
            "172.20.", "172.21.", "172.22.", "172.23.",
            "172.24.", "172.25.", "172.26.", "172.27.",
            "172.28.", "172.29.", "172.30.", "172.31.",
            "192.168.",
            "127.",
            "169.254.",
            "0:0:0:0:0:0:0:1",  // IPv6 localhost
            "::1"                // IPv6 localhost short
    );

    /**
     * Extrait l'adresse IP réelle du client depuis la requête
     *
     * @param request ServerHttpRequest Spring WebFlux
     * @return Adresse IP du client ou "unknown" si non trouvée
     */
    public static String getClientIp(ServerHttpRequest request) {
        // Vérification des headers dans l'ordre
        for (String header : IP_HEADERS) {
            String ip = request.getHeaders().getFirst(header);

            if (isValidIp(ip)) {
                // Si plusieurs IPs (proxies chainés), prendre la première publique
                if (ip.contains(",")) {
                    String[] ips = ip.split(",");
                    for (String splitIp : ips) {
                        String trimmedIp = splitIp.trim();
                        if (isValidIp(trimmedIp) && !isPrivateIp(trimmedIp)) {
                            log.debug("IP extraite du header {}: {}", header, trimmedIp);
                            return trimmedIp;
                        }
                    }
                } else if (!isPrivateIp(ip)) {
                    log.debug("IP extraite du header {}: {}", header, ip);
                    return ip;
                }
            }
        }

        // Fallback : IP de la socket distante
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null) {
            InetAddress address = remoteAddress.getAddress();
            if (address != null) {
                String ip = address.getHostAddress();
                log.debug("IP extraite de la remote address: {}", ip);
                return ip;
            }
        }

        log.warn("Impossible d'extraire l'IP du client, retour de 'unknown'");
        return "unknown";
    }

    /**
     * Vérifie si une chaîne est une IP valide
     *
     * @param ip Chaîne à vérifier
     * @return true si l'IP est valide et non vide
     */
    private static boolean isValidIp(String ip) {
        return ip != null
                && !ip.isEmpty()
                && !ip.equalsIgnoreCase("unknown")
                && !ip.equalsIgnoreCase("null");
    }

    /**
     * Vérifie si une IP est privée
     *
     * @param ip Adresse IP à vérifier
     * @return true si l'IP est privée/locale
     */
    private static boolean isPrivateIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }

        // Vérification des préfixes connus
        for (String prefix : PRIVATE_IP_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return true;
            }
        }

        // Vérification supplémentaire pour localhost
        if (ip.equals("localhost") || ip.equals("0.0.0.0")) {
            return true;
        }

        return false;
    }

    /**
     * Extrait toutes les IPs de la chaîne des proxies
     *
     * @param request ServerHttpRequest
     * @return Liste de toutes les IPs traversées (du client au serveur)
     */
    public static List<String> getProxyChain(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return Arrays.stream(xForwardedFor.split(","))
                    .map(String::trim)
                    .filter(IpAddressUtil::isValidIp)
                    .toList();
        }

        return List.of(getClientIp(request));
    }

    /**
     * Détermine si la requête vient d'un proxy/CDN connu
     *
     * @param request ServerHttpRequest
     * @return true si la requête transite par un proxy identifié
     */
    public static boolean isFromProxy(ServerHttpRequest request) {
        return request.getHeaders().containsKey("X-Forwarded-For")
                || request.getHeaders().containsKey("X-Real-IP")
                || request.getHeaders().containsKey("CF-Connecting-IP");
    }

    /**
     * Extrait le pays d'origine si disponible (Cloudflare)
     *
     * @param request ServerHttpRequest
     * @return Code pays ISO (ex: "CM" pour Cameroun) ou null
     */
    public static String getCountryCode(ServerHttpRequest request) {
        String countryCode = request.getHeaders().getFirst("CF-IPCountry");

        if (countryCode != null && !countryCode.isEmpty() && !countryCode.equals("XX")) {
            log.debug("Code pays détecté: {}", countryCode);
            return countryCode;
        }

        return null;
    }

    /**
     * Détermine si l'IP est une IPv4 ou IPv6
     *
     * @param ip Adresse IP
     * @return "IPv4", "IPv6" ou "unknown"
     */
    public static String getIpVersion(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }

        if (ip.contains(":")) {
            return "IPv6";
        } else if (ip.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            return "IPv4";
        }

        return "unknown";
    }

    /**
     * Anonymise une IP pour la protection de la vie privée
     * (masque le dernier octet pour IPv4, les 4 derniers groupes pour IPv6)
     *
     * @param ip Adresse IP à anonymiser
     * @return IP anonymisée (ex: "192.168.1.xxx" ou "2001:0db8:85a3:xxxx:xxxx:xxxx:xxxx:xxxx")
     */
    public static String anonymizeIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "unknown";
        }

        if (getIpVersion(ip).equals("IPv4")) {
            // IPv4: remplacer le dernier octet
            int lastDotIndex = ip.lastIndexOf('.');
            if (lastDotIndex > 0) {
                return ip.substring(0, lastDotIndex) + ".xxx";
            }
        } else if (getIpVersion(ip).equals("IPv6")) {
            // IPv6: garder seulement les 4 premiers groupes
            String[] parts = ip.split(":");
            if (parts.length > 4) {
                return String.join(":", Arrays.copyOfRange(parts, 0, 4))
                        + ":xxxx:xxxx:xxxx:xxxx";
            }
        }

        return ip;
    }

    /**
     * Génère un identifiant unique basé sur l'IP et le User-Agent
     * (utile pour le rate limiting sans identifier précisément l'utilisateur)
     *
     * @param request ServerHttpRequest
     * @return Hash MD5 de l'IP + User-Agent
     */
    public static String generateClientFingerprint(ServerHttpRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");

        String combined = ip + "|" + (userAgent != null ? userAgent : "unknown");

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(combined.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            log.error("Erreur lors de la génération du fingerprint", e);
            return ip; // Fallback
        }
    }
}