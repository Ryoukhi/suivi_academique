package com.eadl.suivi_academique.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestContextProvider {

    public String getClientIp() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return "UNKNOWN";
        }

        HttpServletRequest request = attrs.getRequest();

        // Cas reverse proxy / load balancer
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    public String getUserAgent() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return "UNKNOWN";
        }

        return attrs.getRequest().getHeader("User-Agent");
    }
}
