package com.hospital.management.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.api.ApiError;
import com.hospital.management.auth.filter.JwtAuthenticationFilter;
import com.hospital.management.auth.service.HospitalUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final HospitalUserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        HospitalUserDetailsService userDetailsService,
                        ObjectMapper objectMapper) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.userDetailsService = userDetailsService;
                this.objectMapper = objectMapper;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(Customizer.withDefaults())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/actuator/health",
                                                                "/actuator/info",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/v1/doctors").permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response,
                                                                authException) -> writeError(response,
                                                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                                                "Unauthorized",
                                                                                "Authentication is required",
                                                                                request.getRequestURI()))
                                                .accessDeniedHandler((request, response,
                                                                accessDeniedException) -> writeError(response,
                                                                                HttpServletResponse.SC_FORBIDDEN,
                                                                                "Forbidden",
                                                                                "You do not have permission to perform this action",
                                                                                request.getRequestURI())))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
                provider.setUserDetailsService(userDetailsService);
                return new ProviderManager(provider);
        }

        private void writeError(
                        HttpServletResponse response,
                        int status,
                        String error,
                        String message,
                        String path) throws java.io.IOException {
                response.setStatus(status);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getOutputStream(), ApiError.of(status, error, message, path));
        }
}
