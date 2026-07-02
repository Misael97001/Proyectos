package com.krakedev.proyectos.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

// EXAMEN 1.3: CORS habilitado para el frontend de React (5173 y 5174)
@CrossOrigin(
        origins = { "http://localhost:5173", "http://localhost:5174" },
        methods = { RequestMethod.GET, RequestMethod.POST,
                    RequestMethod.PUT, RequestMethod.DELETE },
        allowedHeaders = { "Authorization", "Content-Type" }
)
@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    // EXAMEN 1.1: sigue restringido a ADMIN.
    // Si el servicio rechaza la prioridad, respondemos 400 con el JSON del taller
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Tarea tarea) {
        try {
            return new ResponseEntity<>(service.guardar(tarea), HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // Prioridad invalida -> 400 Bad Request + {"error": "Prioridad no válida"}
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Prioridad no válida"));
        }
    }

    // ADMIN y USER pueden listar tareas
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<Tarea> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscar(@PathVariable int id) {
        Tarea tarea = service.buscar(id);
        if (tarea == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tarea);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(
            @PathVariable int id,
            @RequestBody Tarea tarea) {
        Tarea actualizada = service.actualizar(id, tarea);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        boolean eliminado = service.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}