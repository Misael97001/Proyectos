package com.krakedev.proyectos.security;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

// @Component permite que Spring gestione esta clase (aunque usamos métodos estáticos)
@Component
public class JwtUtil {

    // Clave secreta usada para FIRMAR y VERIFICAR los tokens
    // En un proyecto real esto debería estar en application.properties o variables de entorno
    private static final String CLAVE_SECRETA = "EstaEsUnaClaveSecretaMuyLarga123456";

    // Identifica quién emitió el token (nuestro backend)
    private static final String EMISOR = "krakeDevProyectos";

    // Tiempo de expiración del token en milisegundos
    // 360000 ms = 6 minutos (ideal para hacer pruebas durante el taller)
    private static final long TIEMPO_EXPIRACION = 360000;

    /**
     * Genera un nuevo token JWT firmado.
     * Incluye el username como "subject" y el rol como un claim personalizado.
     */
    public static String generarToken(String username, String rol) {

        // Definimos el algoritmo de firma HMAC256 usando nuestra clave secreta
        Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

        long tiempoActual = System.currentTimeMillis();

        // Calculamos la fecha de expiración sumando el tiempo actual + tiempo de vida del token
        Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

        // Construimos el token con JWT.create()
        String tokenGenerado = JWT.create()
                .withIssuer(EMISOR)                       // Quién emite el token
                .withSubject(username)                    // De quién es el token (usuario)
                .withIssuedAt(new Date(tiempoActual))      // Cuándo fue creado
                .withExpiresAt(fechaExpiracion)            // Cuándo expira
                .withClaim("rol", rol)                     // Dato extra: el rol del usuario
                .sign(algoritmo);                          // Firmamos con nuestra clave secreta

        return tokenGenerado;
    }

    /**
     * Valida un token JWT recibido en una petición.
     * Verifica la firma, el emisor y que no haya expirado.
     * Si todo es correcto, retorna el token decodificado.
     * Si algo falla (firma inválida, expiró, etc.), retorna null.
     */
    public static DecodedJWT validarToken(String token) {

        try {
            Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);

            // El verificador valida automáticamente: firma, emisor y expiración
            JWTVerifier verificador = JWT.require(algoritmo)
                    .withIssuer(EMISOR)
                    .build();

            // Si el token es inválido o expiró, verify() lanza una excepción
            DecodedJWT tokenDecodificado = verificador.verify(token);

            return tokenDecodificado;

        } catch (Exception e) {
            // Token inválido, manipulado o expirado
            return null;
        }
    }
}