package com.krakedev.proyectos.controllers;

import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistService blackListService;

    // Inyectamos los 3 componentes que necesitamos
    public AuthController(UsuarioService usuarioService,
                           UsuarioRepository usuarioRepository,
                           TokenBlacklistService blackListService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.blackListService = blackListService;
    }

    /**
     * Endpoint: POST /api/auth/registrar
     * (sin cambios respecto al Hito 1)
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioNuevo = usuarioService.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    /**
     * Endpoint: POST /api/auth/login
     * NUEVO: ahora genera y retorna un token JWT real
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        boolean autenticado = usuarioService.autenticar(username, password);

        if (autenticado) {
            // Buscamos el usuario completo para obtener su rol
            Usuario usuario = usuarioRepository.findByUsername(username).get();

            // Generamos el token JWT con el username y el rol del usuario
            String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

            // Retornamos el token en la respuesta
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario o contraseña incorrectos");
        }
    }

    /**
     * Endpoint: POST /api/auth/logout
     * NUEVO: invalida el token actual agregándolo a la blacklist
     *
     * El cliente debe enviar el header:
     * Authorization: Bearer <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Verificamos que el header exista y tenga el formato correcto "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // substring(7) elimina los primeros 7 caracteres: "Bearer " (con el espacio)
            String token = authHeader.substring(7);

            // Agregamos el token a la lista negra
            blackListService.invalidarToken(token);

            return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente: token invalidado"));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token no proporcionado");
        }
    }
    
    
    /**
     * Endpoint: GET /api/auth/perfil
     * Ruta PROTEGIDA: requiere un token válido en el header Authorization
     * Lee la identidad del usuario directamente desde el contexto de seguridad de Spring,
     * que fue poblado por nuestro JwtAuthenticationFilter
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil() {

        // Obtenemos el objeto de autenticación que el filtro guardó en el contexto
        org.springframework.security.core.Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        // getName() retorna el username (lo guardamos como "subject" en el token)
        String usuario = auth.getName();

        // getAuthorities() retorna los roles; tomamos el primero (en este caso solo tiene uno)
        String rol = auth.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Bienvenido al sistema protegido por Spring Security",
                "usuario", usuario,
                "rolDetectado", rol,
                "status", "autenticado exitosamente"
        ));
    }
}