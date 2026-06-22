package com.krakedev.proyectos.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.services.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @Component: Spring crea una instancia única de este filtro y la gestiona
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Necesitamos el servicio de blacklist para verificar tokens invalidados (logout)
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    /**
     * Este método se ejecuta AUTOMÁTICAMENTE en cada petición HTTP,
     * antes de que llegue al controlador correspondiente.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtenemos el header "Authorization" de la petición
        String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", dejamos pasar la petición
        //    SIN autenticar al usuario. Spring Security decidirá después si la ruta
        //    requiere autenticación o no (rutas públicas como /login pasan sin problema)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // IMPORTANTE: cortamos aquí para no seguir ejecutando el resto del método
        }

        // 3. Extraemos el token quitando el prefijo "Bearer " (7 caracteres)
        String token = authHeader.substring(7);

        // 4. Verificamos si el token fue invalidado (logout)
        if (blacklistService.estaInvalidado(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Acceso denegado: sesión cerrada\"}");
            return; // Cortamos la petición aquí, no sigue al controlador
        }

        // 5. Validamos la firma y expiración del token
        DecodedJWT datosToken = JwtUtil.validarToken(token);

        // 6. Si el token es válido (no es null), configuramos la autenticación en Spring Security
        if (datosToken != null) {

            // Extraemos el username (subject) y el rol (claim personalizado) del token
            String username = datosToken.getSubject();
            String rolOriginal = datosToken.getClaim("rol").asString();

            // Spring Security espera que los roles tengan el prefijo "ROLE_"
            // Por eso "ADMIN" se convierte en "ROLE_ADMIN"
            String rolSpring = "ROLE_" + rolOriginal;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);

            // Creamos un objeto de autenticación con: usuario, sin credenciales (null), y su rol
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));

            // Guardamos esta autenticación en el contexto de seguridad de Spring
            // A partir de aquí, Spring SABE quién es el usuario y qué rol tiene
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. Dejamos continuar la petición hacia el controlador correspondiente
        filterChain.doFilter(request, response);
    }
}