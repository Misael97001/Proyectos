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

// EXAMEN 1.3: CORS habilitado para el frontend de React (5173 y 5174)
@CrossOrigin(
        origins = { "http://localhost:5173", "http://localhost:5174" },
        methods = { RequestMethod.GET, RequestMethod.POST,
                    RequestMethod.PUT, RequestMethod.DELETE },
        allowedHeaders = { "Authorization", "Content-Type" }
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TokenBlacklistService blackListService;

    public AuthController(UsuarioService usuarioService,
                           UsuarioRepository usuarioRepository,
                           TokenBlacklistService blackListService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.blackListService = blackListService;
    }

    // POST /api/auth/registrar (sin cambios)
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

    // POST /api/auth/login
    // EXAMEN Fase 2: ahora retorna token + rol + username,
    // porque React necesita el rol para el Navbar condicional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        boolean autenticado = usuarioService.autenticar(username, password);

        if (autenticado) {
            Usuario usuario = usuarioRepository.findByUsername(username).get();

            String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "rol", usuario.getRol(),
                    "username", usuario.getUsername()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario o contraseña incorrectos"));
        }
    }

    // POST /api/auth/logout: agrega el token a la blacklist
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // substring(7) quita el prefijo "Bearer "
            String token = authHeader.substring(7);

            blackListService.invalidarToken(token);

            return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente: token invalidado"));

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Token no proporcionado");
        }
    }

    // GET /api/auth/perfil (ruta protegida de prueba)
    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil() {

        org.springframework.security.core.Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String usuario = auth.getName();
        String rol = auth.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(Map.of(
                "mensaje", "Bienvenido al sistema protegido por Spring Security",
                "usuario", usuario,
                "rolDetectado", rol,
                "status", "autenticado exitosamente"
        ));
    }
}