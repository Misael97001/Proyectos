package com.krakedev.proyectos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // Sin CSRF: usamos tokens, no cookies/sesiones
                .csrf(csrf -> csrf.disable())

                // STATELESS: cada peticion se valida con su token
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Rutas publicas: login y registrar
                        .requestMatchers("/api/auth/login", "/api/auth/registrar").permitAll()

                        // EXAMEN 1.2: el resumen publico queda excluido de la seguridad,
                        // responde sin exigir token JWT
                        .requestMatchers("/api/proyectos/publico/resumen").permitAll()

                        // Todo lo demas requiere autenticacion
                        .anyRequest().authenticated()
                )

                // Nuestro filtro JWT se ejecuta antes del filtro estandar de Spring
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}