package com.evaluacion.alquiler.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Esta clase representa la entidad Alquiler.
 * Cada objeto de esta clase será un registro en la tabla "alquiler" de la base
 * de datos.
 */
@Entity
@Table(name = "alquiler") // Nombre explícito de la tabla en BD (opcional pero recomendado)
public class Alquiler {

    // Clave primaria del alquiler, se autogenera con IDENTITY (como un
    // autoincrement)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlquiler;

    // Fecha en que se realizó el alquiler (no se permite nulo)
    @Column(nullable = false)
    private LocalDate fecha;

    // Relación: muchos alquileres pueden estar asociados a un mismo cliente
    @ManyToOne
    @JoinColumn(name = "idCliente", nullable = false) // Clave foránea en BD
    private Cliente cliente;

    // Monto total del alquiler (no se permite nulo)
    @Column(nullable = false)
    private Double total;

    // Estado del alquiler (usamos un Enum y lo guardamos como texto en la BD)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoAlquiler estado;

    // Constructor vacío obligatorio para que JPA pueda instanciar la clase
    public Alquiler() {
    }

    // Constructor para facilitar la creación de objetos desde el código
    public Alquiler(LocalDate fecha, Cliente cliente, Double total, EstadoAlquiler estado) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.total = total;
        this.estado = estado;
    }

    // Getter del ID (no se define setter ya que se genera automáticamente)
    public Long getIdAlquiler() {
        return idAlquiler;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public void setEstado(EstadoAlquiler estado) {
        this.estado = estado;
    }

    /**
     * Método útil para imprimir datos del alquiler rápidamente (por consola o
     * logs).
     */
    @Override
    public String toString() {
        return "Alquiler [idAlquiler=" + idAlquiler + ", fecha=" + fecha +
                ", cliente=" + (cliente != null ? cliente.getIdCliente() : null) +
                ", total=" + total + ", estado=" + estado + "]";
    }

    /**
     * Enum que representa los posibles estados del alquiler.
     */
    public enum EstadoAlquiler {
        Activo, // El cliente aún tiene el producto
        Devuelto, // El producto fue devuelto a tiempo
        Retrasado // El producto fue devuelto fuera de plazo
    }
}
