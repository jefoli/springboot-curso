package com.springboot.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtUtils {
    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";

    //tem que ter no mínimo 32 caracteres
    public static final String SECRET_KEY = "0123456789-0123456789-0123456789";

    //vars vão armazenar o tempo que o token irá expirar
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 2;

    private JwtUtils(){
    }

    private static Key generateKey() {

        //recurso da lib jwt que prepara a chave para ser criptografada
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    //precesso de expiração do token - data em que o token foi criado e a data que desejamos que seja expirado

    private static Date toExpireDate(Date start) {
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);

        //precisamos retornar um objeto do tipo Date, pois a lib JWT trabalha com formato Date
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    //metodo que gera o token
    public static JwtToken createToken(String username, String role) {
        Date issueAt = new Date();
        Date limit = toExpireDate(issueAt);

        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(issueAt)
                .setExpiration(limit)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .claim("role", role)
                .compact();

        return new JwtToken(token);
    }

    //método para recuperar conteúdo do token
    private static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build()
                    .parseClaimsJws(refactorToken(token)).getBody();
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s", ex.getMessage()));
        }

        return null;
    }

    //método para recuperar o username de dentro do token
    public static String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    //método para testar a validade do token
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(generateKey()).build()
                    .parseClaimsJws(refactorToken(token));
            return true;
        } catch (JwtException ex) {
            log.error(String.format("Token inválido %s", ex.getMessage()));
        }
        return false;
    }

    //método para remover o bearer
    private static String refactorToken(String token) {
        if(token.contains(JWT_BEARER)) {
            return token.substring(JWT_BEARER.length());
        }
        return token;
    }

}
