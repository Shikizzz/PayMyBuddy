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
        /* final ResponseCookie responseCookie = ResponseCookie
                .from("test", "test")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .maxAge(12345)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());*/
        if(!request.getRequestURI().equals("/login") && !request.getRequestURI().equals("/register") && !request.getRequestURI().equals("/error") &&!request.getRequestURI().contains(".css")) {
            HttpSession session = request.getSession();
            if (session.getAttribute("user") == null) {
                response.sendRedirect("/login");
                return false;
            }
        }
        return true;
    }
}
