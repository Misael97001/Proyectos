package com.krakedev.proyectos.repositories;

// Importamos la entidad que maneja este repositorio
import com.krakedev.proyectos.entidades.Usuario;

// JpaRepository nos da gratis: save(), findById(), findAll(), deleteById(), etc.
// El primer parámetro es la entidad, el segundo es el tipo del ID (Long)
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA genera automáticamente el SQL:
    // SELECT * FROM usuarios WHERE username = ?
    // Retorna Optional para manejar el caso en que no exista el usuario
    Optional<Usuario> findByUsername(String username);
}