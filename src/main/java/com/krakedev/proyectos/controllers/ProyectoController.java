package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService service;

    public ProyectoController(ProyectoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Proyecto> guardar(@RequestBody Proyecto proyecto) {
        return new ResponseEntity<>(service.guardar(proyecto), HttpStatus.CREATED);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {

        boolean eliminado = service.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}