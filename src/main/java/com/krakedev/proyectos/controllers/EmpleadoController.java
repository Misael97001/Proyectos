package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.services.EmpleadoService;

// EXAMEN 1.3: CORS habilitado para el frontend de React (5173 y 5174)
@CrossOrigin(
        origins = { "http://localhost:5173", "http://localhost:5174" },
        methods = { RequestMethod.GET, RequestMethod.POST,
                    RequestMethod.PUT, RequestMethod.DELETE },
        allowedHeaders = { "Authorization", "Content-Type" }
)
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Empleado> guardar(@RequestBody Empleado empleado) {
        return new ResponseEntity<>(service.guardar(empleado), HttpStatus.CREATED);
    }

    // ADMIN y USER pueden listar empleados
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<Empleado> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> buscar(@PathVariable int id) {
        Empleado empleado = service.buscar(id);
        if (empleado == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(empleado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizar(
            @PathVariable int id,
            @RequestBody Empleado empleado) {
        Empleado actualizado = service.actualizar(id, empleado);
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