package com.p6.payMyBuddy.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ConnectionInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(ConnectionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info(request.getRequestURI());
        if (!request.getRequestURI().contains("/login") && !request.getRequestURI().contains("/register") && !request.getRequestURI().contains("/error") && !request.getRequestURI().contains(".css")) {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") == null) {
                response.sendRedirect("/login");
                return false;
            }
        }
        return true;
    }
}
