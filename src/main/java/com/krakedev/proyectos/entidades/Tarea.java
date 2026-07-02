package com.krakedev.proyectos.entidades;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "tareas")
public class Tarea {

	// EXAMEN 1.1: nuevo atributo, se mapea a la columna "prioridad" en PostgreSQL
	@Column(length = 10)
	private String prioridad;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, length = 250)
	private String descripcion;

	@Column(nullable = false)
	private LocalDate fechaLimite;

	@Column(nullable = false)
	private double costoEstimado;

	@ManyToOne
	@JoinColumn(name = "proyecto_id", nullable = false)
	private Proyecto proyecto;

	@ManyToMany
	@JoinTable(name = "tarea_empleados", joinColumns = @JoinColumn(name = "tarea_id"), inverseJoinColumns = @JoinColumn(name = "empleado_id"))
	private List<Empleado> empleados;

	public Tarea() {
	}

	// Constructor actualizado: ahora recibe también la prioridad
	public Tarea(String descripcion, LocalDate fechaLimite, double costoEstimado, String prioridad, Proyecto proyecto,
			List<Empleado> empleados) {

		this.descripcion = descripcion;
		this.fechaLimite = fechaLimite;
		this.costoEstimado = costoEstimado;
		this.prioridad = prioridad;
		this.proyecto = proyecto;
		this.empleados = empleados;
	}

    // Getter y setter del nuevo campo (necesarios para el JSON de entrada y salida)
    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDate getFechaLimite() {
		return fechaLimite;
	}

	public void setFechaLimite(LocalDate fechaLimite) {
		this.fechaLimite = fechaLimite;
	}

	public double getCostoEstimado() {
		return costoEstimado;
	}

	public void setCostoEstimado(double costoEstimado) {
		this.costoEstimado = costoEstimado;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}
}