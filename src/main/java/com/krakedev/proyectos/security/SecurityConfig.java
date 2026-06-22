package com.krakedev.proyectos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // Habilita el uso de @PreAuthorize en los controladores (Hito 4)
public class SecurityConfig {

    // Inyectamos nuestro filtro personalizado
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Deshabilitamos CSRF: no usamos cookies/sesiones, usamos tokens
                .csrf(csrf -> csrf.disable())

                // STATELESS: el servidor no guarda sesiones, cada petición se valida con su token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Solo login y registrar quedan públicos
                        // logout ahora SÍ requiere pasar por el filtro JWT
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()
                        // Cualquier otra ruta requiere autenticación válida
                        .anyRequest().authenticated()
                )

                // Insertamos nuestro filtro ANTES del filtro estándar de Spring Security
                // Esto garantiza que nuestra lógica JWT se ejecute primero en cada petición
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}