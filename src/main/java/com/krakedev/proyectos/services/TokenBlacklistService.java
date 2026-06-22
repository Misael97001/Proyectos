package com.krakedev.proyectos.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    // ConcurrentHashMap.newKeySet() crea un Set seguro para múltiples hilos (multi-thread)
    // Esto es importante porque varios usuarios pueden hacer logout AL MISMO TIEMPO
    // sin que se corrompan los datos
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    /**
     * Agrega un token a la lista negra (lo invalida).
     * Se llama cuando el usuario hace logout.
     */
    public void invalidarToken(String token) {
        blacklist.add(token);
    }

    /**
     * Verifica si un token está invalidado.
     * Se llama en CADA petición protegida, antes de aceptar el token.
     */
    public boolean estaInvalidado(String token) {
        return blacklist.contains(token);
    }
}