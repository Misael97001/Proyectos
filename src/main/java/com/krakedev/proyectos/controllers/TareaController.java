package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final TareaService service;

    public TareaController(TareaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Tarea> guardar(@RequestBody Tarea tarea) {
        return new ResponseEntity<>(service.guardar(tarea), HttpStatus.CREATED);
    }

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