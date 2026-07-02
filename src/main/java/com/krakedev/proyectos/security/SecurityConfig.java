package com.krakedev.proyectos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity   // Habilita @PreAuthorize en los controladores
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. SOLUCIÓN AL ERROR DE CONEXIÓN DEL PUERTO 5174:
                // Activa el filtro CORS de Spring Security usando la configuración de tus controladores (@CrossOrigin)
                .cors(Customizer.withDefaults()) 

                // Sin CSRF: usamos tokens stateLess, no cookies/sesiones
                .csrf(csrf -> csrf.disable())

                // STATELESS: cada petición se valida de forma independiente con su token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas de autenticación: login y registrar
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()

                        // EXAMEN 1.2: el resumen público queda excluido de la seguridad, responde sin exigir token JWT
                        .requestMatchers("/api/proyectos/publico/resumen").permitAll()

                        // Todo lo demás requiere autenticación explícita basada en JWT
                        .anyRequest().authenticated()
                )

                // Nuestro filtro JWT se ejecuta antes del filtro estándar de Spring Security
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}