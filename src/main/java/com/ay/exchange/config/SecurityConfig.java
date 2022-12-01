package com.ay.exchange.config;

import com.ay.exchange.filter.JwtExceptionFilter;
import com.ay.exchange.filter.JwtFilter;
import com.ay.exchange.jwt.JwtFilterEntryPoint;
import com.ay.exchange.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.cors.CorsUtils;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfig corsConfig;
    //private final JwtFilterEntryPoint jwtFilterEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfig.corsConfigurationSource())
                .and()
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeHttpRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and()
                    .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class);
        //.exceptionHandling().authenticationEntryPoint(jwtFilterEntryPoint);
        return http.build();
    }

    private String[] getPathInSwagger() {
        return new String[]{
                "/swagger",
                "/swagger-ui/index.html",
                "/swagger-ui/swagger-ui.css",
                "/swagger-ui/index.css",
                "/swagger-ui/swagger-ui-bundle.js",
                "/swagger-ui/swagger-ui-standalone-preset.js",
                "/swagger-ui/swagger-initializer.js",
                "/v3/api-docs/swagger-config",
                "/swagger-ui/favicon-32x32.png",
                "/v3/api-docs/user-api",
                "/v3/api-docs/board-api",
                "/v3/api-docs/comment-api",
                "/favicon.ico"
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultHttpFirewall())
                .ignoring()
                .antMatchers("/user/sign-up/verification-code",
                        "/user/find-password/verification-code",
                        "/user/temporary-password",
                        "/user/existence-id",
                        "/user/existence-nickname",
                        "/user/find-id",
                        "/board/{category:\\d+}/**",
                        "/board/content/{category:\\d+}",
                        "/comment/{boardId:\\d+}/**")
                .antMatchers(getPathInSwagger());
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}
