package com.krakedev.proyectos.entidades;

// Importaciones de JPA para mapear la clase a la tabla de la base de datos
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// @Entity le dice a Hibernate que esta clase es una tabla de la BD
@Entity
// @Table(name="usuarios") indica el nombre exacto de la tabla en PostgreSQL
@Table(name = "usuarios")
public class Usuario {

    // @Id marca este campo como la clave primaria
    // @GeneratedValue indica que el id lo genera automáticamente la BD (SERIAL)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable=false → campo obligatorio | unique=true → no se repite
    @Column(nullable = false, unique = true)
    private String username;

    // nullable=false → campo obligatorio
    // Aquí se guardará el hash BCrypt, no la contraseña real
    @Column(nullable = false)
    private String password;

    // Guardará "ADMIN" o "USER"
    private String rol;

    // Constructor vacío OBLIGATORIO para que JPA/Hibernate pueda
    // instanciar objetos cuando lee filas de la base de datos
    public Usuario() {
    }

    // Constructor con parámetros para crear usuarios desde el código
    public Usuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    // --- GETTERS Y SETTERS ---
    // Necesarios para que Spring pueda leer/escribir los campos
    // cuando convierte JSON a objeto y viceversa

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}