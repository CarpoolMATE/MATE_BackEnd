package MATE.Carpool.config.securityConfig;


import MATE.Carpool.common.exception.*;
import MATE.Carpool.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final EndpointHandler endpointHandler;
    private final static String[] PERMIT_URI = {
            "/api/member/signUp",
            "/api/member/signIn",
            "/api/admin/signIn",

            "/api/member/findMemberId",
            "/api/member/findPassword",

            "/api/member/checkEmail",
            "/api/member/checkMemberId",
            "/api/member/checkNickname",

            "/h2-console/*",
            "/h2-console",
            "/api/social/**",
            "/actuator/**",
            "/actuator",
            "/favicon.ico",
            "/manage/prometheus",
            "/manage/prometheus/**"

    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_URI).permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin","/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
//
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(endpointHandler)
                        .accessDeniedHandler(endpointHandler)
                );
        //h2-console
        http.headers(headers ->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://carple-front.vercel.app");
        configuration.addAllowedOrigin("https://frontend-rouge-nu-94.vercel.app");
        configuration.addAllowedOrigin("https://admin-iota-lemon.vercel.app");
        configuration.addAllowedOrigin("https://*.carpool.com");
        configuration.addAllowedOrigin("https://.carpool.com");
        configuration.addAllowedOrigin("https://kyucar.duckdns.org");

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Refreshtoken"));

        configuration.setExposedHeaders(List.of("Authorization","Refreshtoken"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
