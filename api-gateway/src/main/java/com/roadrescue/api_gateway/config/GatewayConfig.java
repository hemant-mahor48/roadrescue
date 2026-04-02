package com.roadrescue.api_gateway.config;

import com.roadrescue.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-routes", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route("mechanic-routes", r -> r
                        .path("/api/v1/mechanics/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route("auth-routes", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route("request-routes", r -> r
                        .path("/api/v1/requests/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://REQUEST-SERVICE"))
                .route("location-routes", r -> r
                        .path("/api/v1/location/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://LOCATION-SERVICE"))
                .route("payment-routes", r -> r
                        .path("/api/v1/payments/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://PAYMENT-SERVICE"))
                .route("analytics-routes", r -> r
                        .path("/api/v1/analytics/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://ANALYTICS-SERVICE"))
                .route("rating-routes", r -> r
                        .path("/api/v1/ratings/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://RATING-SERVICE"))
                .build();
    }
}

