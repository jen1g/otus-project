package com.otushomework.gateway.gatewayservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CheckAuthTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<CheckAuthTokenGatewayFilterFactory.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public static class Config {}

    public CheckAuthTokenGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println("check");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authToken = authHeader.substring(7);
            System.out.println(authToken);
            if (!isValidToken(authToken)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = getClaimsFromToken(authToken);
            String userId = String.valueOf(claims.getSubject());

            // Модифицируем запрос: добавляем X-User-Id и удаляем X-Auth-Token
            exchange = exchange.mutate().request(
                    exchange.getRequest().mutate()
                            .header("X-User-Id", userId)
                            .headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION))
                            .build()
            ).build();
            System.out.println(exchange.getRequest().getPath());
            System.out.println(exchange.getRequest().getHeaders());
            return chain.filter(exchange);
        };
    }

    private boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
