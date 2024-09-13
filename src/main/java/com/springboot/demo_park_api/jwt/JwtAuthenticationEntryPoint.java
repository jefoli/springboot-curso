package com.springboot.demo_park_api.jwt;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("Http Status 401 {}", authException.getMessage());
        //quando usuario nao estiver autenticado, teremos o cabecao www. com info Bearer - e esse deverá sen viado para api/...
        response.setHeader("www-authenticate", "Bearer realm='/api/v1/auth"); //primeiro param. chave, segundo valor
        response.sendError(401);
    }
}
