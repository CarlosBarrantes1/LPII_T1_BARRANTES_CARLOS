package com.evaluacion.alquiler.model;

import jakarta.persistence.*;

/**
 * Clase que representa una película en la base de datos.
 */
@Entity
@Table(name = "pelicula")
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPelicula;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 50)
    private String genero;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Double precio;

    public Pelicula() {
    }

    public Pelicula(Long idPelicula, String titulo, String genero, Integer stock, Double precio) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.stock = stock;
        this.precio = precio;
    }

    public Pelicula(String titulo, String genero, Integer stock, Double precio) {
        this.titulo = titulo;
        this.genero = genero;
        this.stock = stock;
        this.precio = precio;
    }

    public Long getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(Long idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
