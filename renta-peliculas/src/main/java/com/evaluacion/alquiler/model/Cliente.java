package com.evaluacion.alquiler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Clase que representa a un cliente en la base de datos.
 */
@Entity // Marca esta clase como entidad para JPA (una tabla en la base de datos)
@Table(name = "cliente") // Nombre de la tabla en la base de datos
public class Cliente {

    @Id // Clave primaria de la tabla
    private Long idCliente; // Identificador único del cliente

    @Column(nullable = false, length = 100) // Campo obligatorio con máximo 100 caracteres
    private String nombre; // Nombre del cliente

    @Column(nullable = false, length = 100) // Campo obligatorio con máximo 100 caracteres
    private String email; // Correo electrónico del cliente

    // Constructor vacío requerido por JPA para crear instancias vía reflexión
    public Cliente() {
    }

    // Constructor con parámetros para inicializar un cliente con datos
    public Cliente(Long idCliente, String nombre, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
    }

    // Getter para obtener el ID del cliente
    public Long getIdCliente() {
        return idCliente;
    }

    // Setter para modificar el ID del cliente
    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    // Getter para obtener el nombre
    public String getNombre() {
        return nombre;
    }

    // Setter para modificar el nombre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Getter para obtener el email
    public String getEmail() {
        return email;
    }

    // Setter para modificar el email
    public void setEmail(String email) {
        this.email = email;
    }

    // Método que devuelve el nombre, útil para imprimir el objeto Cliente
    @Override
    public String toString() {
        return nombre;
    }
}
