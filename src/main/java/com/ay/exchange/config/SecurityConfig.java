package com.ay.exchange.config;

import com.ay.exchange.filter.JwtExceptionFilter;
import com.ay.exchange.filter.JwtFilter;
import com.ay.exchange.jwt.JwtTokenProvider;

import com.ay.exchange.oauth.handler.OAuth2SuccessHandler;
import com.ay.exchange.oauth.service.Oauth2Service;
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
public class SecurityConfig{
    private final CorsConfig corsConfig;
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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
                .and()
                .formLogin().disable()
                .oauth2Login()
                .userInfoEndpoint().userService(oauth2Service)
                .and()
                .successHandler(oAuth2SuccessHandler);
        //.exceptionHandling().authenticationEntryPoint(jwtFilterEntryPoint);
        http
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class);

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
                "/v3/api-docs/report-api",
                "/v3/api-docs/oauth2-api",
                "/favicon.ico"
        };
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web
                .httpFirewall(defaultHttpFirewall())
                .ignoring()
                .antMatchers(getPathInSwagger());
//                .antMatchers(
//                        "/user/temporary-password",
//                        "/board/{category:\\d+}/**",
//                        "/board/content/{category:\\d+}",
//                        "/comment/{boardId:\\d+}/**")
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}
