package com.polarbookshop.edgeservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;

@Configuration(proxyBeanMethods = false)
//@EnableRedisHttpSession
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                .pathMatchers(HttpMethod.GET, "/vitalsigns/**").authenticated()
                .anyExchange().authenticated()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
            .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
            .csrf(csrf -> csrf.csrfTokenRequestHandler(new ServerCsrfTokenRequestAttributeHandler()))
            .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    @Bean
    WebFilter csrfWebFilter() {
        // Required because of https://github.com/spring-projects/spring-security/issues/5766
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                 return csrfToken != null ? csrfToken.then() : Mono.empty();
    //            return csrfToken.doOnSuccess(token -> exchange.getAttributes()
     //               .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
            }));
            return chain.filter(exchange);
        };
    }

// https://docs.spring.io/spring-security/reference/reactive/exploits/csrf.html
/*    @Bean
    Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
        return csrfToken.doOnSuccess(token -> exchange.getAttributes()
            .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }*/
}
