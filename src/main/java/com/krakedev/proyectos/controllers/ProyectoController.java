package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

// EXAMEN 1.3: CORS habilitado para el frontend de React (5173 y 5174)
@CrossOrigin(
        origins = { "http://localhost:5173", "http://localhost:5174" },
        methods = { RequestMethod.GET, RequestMethod.POST,
                    RequestMethod.PUT, RequestMethod.DELETE },
        allowedHeaders = { "Authorization", "Content-Type" }
)
@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService service;

    public ProyectoController(ProyectoService service) {
        this.service = service;
    }

    // EXAMEN 1.2: endpoint PUBLICO (sin @PreAuthorize).
    // Ruta completa: GET /api/proyectos/publico/resumen -> retorna el total (Long)
    @GetMapping("/publico/resumen")
    public ResponseEntity<Long> resumenPublico() {
        Long total = service.contarProyectos();
        return ResponseEntity.ok(total);
    }

    // Solo un ADMIN puede crear proyectos nuevos
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Proyecto> guardar(@RequestBody Proyecto proyecto) {
        return new ResponseEntity<>(service.guardar(proyecto), HttpStatus.CREATED);
    }

    // ADMIN y USER pueden listar/consultar proyectos
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<Proyecto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> buscar(@PathVariable int id) {
        Proyecto proyecto = service.buscar(id);
        if (proyecto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proyecto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(
            @PathVariable int id,
            @RequestBody Proyecto proyecto) {
        Proyecto actualizado = service.actualizar(id, proyecto);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(actualizado);
    }

    // Solo un ADMIN puede eliminar proyectos
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        boolean eliminado = service.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}