package com.evaluacion.alquiler.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Entidad que representa el detalle de un alquiler (películas incluidas y
 * cantidad).
 * Emplea clave primaria compuesta formada por el alquiler y la película.
 */
@Entity
@IdClass(DetalleAlquiler.DetalleAlquilerId.class)
@Table(name = "detalleAlquiler") // ← nombre de la tabla en BD
public class DetalleAlquiler {

    /*
     * ------------------------------------------------------------------
     * CLAVE PRIMARIA COMPUESTA (parte 1): referencia al alquiler
     * ------------------------------------------------------------------
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "id_alquiler", // ← columna FK hacia alquiler.id_alquiler
            nullable = false)
    private Alquiler alquiler;

    /*
     * ------------------------------------------------------------------
     * CLAVE PRIMARIA COMPUESTA (parte 2): referencia a la película
     * ------------------------------------------------------------------
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "id_pelicula", // ← columna FK hacia pelicula.id_pelicula
            nullable = false)
    private Pelicula pelicula;

    /*
     * ------------------------------------------------------------------
     * Atributos propios
     * ------------------------------------------------------------------
     */
    @Column(nullable = false)
    private Integer cantidad; // Nº de copias alquiladas

    /*
     * ------------------------------------------------------------------
     * Constructores
     * ------------------------------------------------------------------
     */
    public DetalleAlquiler() {
    }

    public DetalleAlquiler(Alquiler alquiler,
            Pelicula pelicula,
            Integer cantidad) {
        this.alquiler = alquiler;
        this.pelicula = pelicula;
        this.cantidad = cantidad;
    }

    /*
     * ------------------------------------------------------------------
     * Getters y setters
     * ------------------------------------------------------------------
     */
    public Alquiler getAlquiler() {
        return alquiler;
    }

    public void setAlquiler(Alquiler alquiler) {
        this.alquiler = alquiler;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /*
     * ------------------------------------------------------------------
     * toString – útil para depuración
     * ------------------------------------------------------------------
     */
    @Override
    public String toString() {
        return "DetalleAlquiler [alquiler=" + (alquiler != null ? alquiler.getIdAlquiler() : null) +
                ", pelicula=" + (pelicula != null ? pelicula.getIdPelicula() : null) +
                ", cantidad=" + cantidad + "]";
    }

    /*
     * ==================================================================
     * Clase estática para la clave primaria compuesta
     * ==================================================================
     */
    public static class DetalleAlquilerId implements Serializable {
        private static final long serialVersionUID = 1L;

        /** FK simple (no objeto) → coincide con el tipo de la PK en Alquiler */
        private Long alquiler; // id_alquiler
        /** FK simple (no objeto) → coincide con el tipo de la PK en Pelicula */
        private Long pelicula; // id_pelicula

        public DetalleAlquilerId() {
        }

        public DetalleAlquilerId(Long alquiler, Long pelicula) {
            this.alquiler = alquiler;
            this.pelicula = pelicula;
        }

        /* equals y hashCode son obligatorios para Id compuestas */
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof DetalleAlquilerId))
                return false;
            DetalleAlquilerId that = (DetalleAlquilerId) o;
            return Objects.equals(alquiler, that.alquiler) &&
                    Objects.equals(pelicula, that.pelicula);
        }

        @Override
        public int hashCode() {
            return Objects.hash(alquiler, pelicula);
        }
    }
}
