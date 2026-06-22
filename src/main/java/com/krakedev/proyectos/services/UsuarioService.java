package com.krakedev.proyectos.services;

// Importamos BCrypt de la librería jbcrypt que agregamos al pom.xml
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

import java.util.Optional;

// @Service le dice a Spring que esta clase es un componente de lógica de negocio
// Spring la gestiona y permite inyectarla en otros componentes
@Service
public class UsuarioService {

    // Repositorio para acceder a la tabla usuarios en la BD
    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor (forma recomendada en Spring)
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * IMPORTANTE: Antes de guardar, encripta la contraseña con BCrypt.
     * BCrypt.gensalt() genera una "sal" aleatoria que hace único cada hash.
     * Nunca se guarda la contraseña en texto plano.
     */
    public Usuario guardar(Usuario usuario) {
        // Tomamos la contraseña que vino en el JSON (texto plano)
        // y la convertimos a un hash BCrypt (ej: $2a$10$xyz...)
        String contrasenaEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

        // Reemplazamos la contraseña plana por el hash seguro
        usuario.setPassword(contrasenaEncriptada);

        // Guardamos en la base de datos (ya con contraseña encriptada)
        return usuarioRepository.save(usuario);
    }

    /**
     * Verifica si las credenciales son correctas.
     * BCrypt.checkpw compara el texto plano con el hash guardado en BD.
     * Retorna true si coinciden, false si no.
     */
    public boolean autenticar(String username, String password) {
        // Buscamos al usuario por su nombre de usuario
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername(username);

        if (usuarioEncontrado.isPresent()) {
            Usuario usuario = usuarioEncontrado.get();

            // BCrypt.checkpw hace la comparación segura:
            // toma el texto plano (password) y lo compara con el hash (usuario.getPassword())
            if (BCrypt.checkpw(password, usuario.getPassword())) {
                return true;
            }
        }

        // Retorna false si el usuario no existe o la contraseña no coincide
        return false;
    }
}