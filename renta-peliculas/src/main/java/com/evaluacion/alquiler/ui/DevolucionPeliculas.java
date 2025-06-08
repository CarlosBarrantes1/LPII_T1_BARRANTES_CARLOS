package com.evaluacion.alquiler.ui;

import com.evaluacion.alquiler.model.Alquiler;
import com.evaluacion.alquiler.model.Cliente;
import com.evaluacion.alquiler.model.DetalleAlquiler;
import com.evaluacion.alquiler.model.Pelicula;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel Swing encargado de gestionar la devolución de películas.
 * <p>
 * Se apoya en las entidades actualizadas:
 * - {@link Cliente}
 * - {@link Pelicula}
 * - {@link Alquiler}
 * - {@link DetalleAlquiler}
 */
public class DevolucionPeliculas extends JPanel {

    private final JComboBox<Cliente> comboClientes = new JComboBox<>();
    private final JComboBox<Pelicula> comboPeliculas = new JComboBox<>();
    private final JButton btnDevolver = new JButton("Devolver");
    private final JLabel lblMensaje = new JLabel(" ");

    private final EntityManager em;
    private final MantenimientoAlquileres panelAlquileres;
    private final MantenimientoPeliculas panelPeliculas;

    public DevolucionPeliculas(MantenimientoAlquileres panelAlquileres,
            MantenimientoPeliculas panelPeliculas,
            EntityManager em) {
        this.panelAlquileres = panelAlquileres;
        this.panelPeliculas = panelPeliculas;
        this.em = em;

        /* ----------------- Estilos básicos ----------------- */
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        comboClientes.setFont(fieldFont);
        comboClientes.setBackground(Color.WHITE);

        comboPeliculas.setFont(fieldFont);
        comboPeliculas.setBackground(Color.WHITE);

        btnDevolver.setFont(labelFont);
        btnDevolver.setBackground(Color.BLACK);
        btnDevolver.setForeground(Color.WHITE);

        lblMensaje.setFont(labelFont);

        /* ----------------- Layout ----------------- */
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 8, 12, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Cliente con alquiler activo/retrasado:") {
            {
                setFont(labelFont);
            }
        }, gbc);
        gbc.gridx = 1;
        add(comboClientes, gbc);

        // Película
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Película alquilada:") {
            {
                setFont(labelFont);
            }
        }, gbc);
        gbc.gridx = 1;
        add(comboPeliculas, gbc);

        // Botón
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnDevolver, gbc);

        // Mensaje
        gbc.gridy = 3;
        add(lblMensaje, gbc);

        /* ----------------- Lógica ----------------- */
        cargarClientesConAlquilerActivo();
        comboClientes.addActionListener(e -> cargarPeliculasAlquiladas());
        btnDevolver.addActionListener(this::marcarDevuelto);
    }

    /**
     * Carga en el combo los clientes con alquileres Activos o Retrasados.
     */
    public void cargarClientesConAlquilerActivo() {
        comboClientes.removeAllItems();
        List<Cliente> clientes = em.createQuery(
                "SELECT DISTINCT a.cliente FROM Alquiler a " +
                        "WHERE a.estado = :estado1 OR a.estado = :estado2",
                Cliente.class)
                .setParameter("estado1", Alquiler.EstadoAlquiler.Activo)
                .setParameter("estado2", Alquiler.EstadoAlquiler.Retrasado)
                .getResultList();

        clientes.forEach(comboClientes::addItem);
        cargarPeliculasAlquiladas();
    }

    /**
     * Muestra las películas alquiladas por el cliente seleccionado.
     */
    private void cargarPeliculasAlquiladas() {
        comboPeliculas.removeAllItems();
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        if (cliente == null)
            return;

        List<Pelicula> peliculas = em.createQuery(
                "SELECT DISTINCT d.pelicula FROM DetalleAlquiler d " +
                        "WHERE d.alquiler.cliente = :cliente " +
                        "AND (d.alquiler.estado = :estado1 OR d.alquiler.estado = :estado2)",
                Pelicula.class)
                .setParameter("cliente", cliente)
                .setParameter("estado1", Alquiler.EstadoAlquiler.Activo)
                .setParameter("estado2", Alquiler.EstadoAlquiler.Retrasado)
                .getResultList();

        peliculas.forEach(comboPeliculas::addItem);
    }

    /**
     * Marca la película seleccionada como devuelta para el cliente elegido.
     */
    private void marcarDevuelto(ActionEvent e) {
        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        Pelicula pelicula = (Pelicula) comboPeliculas.getSelectedItem();
        if (cliente == null || pelicula == null) {
            lblMensaje.setText("Seleccione cliente y película.");
            return;
        }

        try {
            em.getTransaction().begin();

            // 1. Encontrar el alquiler activo/retrasado para ese cliente y película
            Alquiler alquiler = em.createQuery(
                    "SELECT d.alquiler FROM DetalleAlquiler d " +
                            "WHERE d.alquiler.cliente = :cliente " +
                            "AND   d.pelicula         = :pelicula " +
                            "AND  (d.alquiler.estado  = :estado1 OR d.alquiler.estado = :estado2)",
                    Alquiler.class)
                    .setParameter("cliente", cliente)
                    .setParameter("pelicula", pelicula)
                    .setParameter("estado1", Alquiler.EstadoAlquiler.Activo)
                    .setParameter("estado2", Alquiler.EstadoAlquiler.Retrasado)
                    .setMaxResults(1)
                    .getSingleResult();

            // 2. Obtener el detalle concreto
            DetalleAlquiler detalle = em.createQuery(
                    "SELECT d FROM DetalleAlquiler d WHERE d.alquiler = :alquiler AND d.pelicula = :pelicula",
                    DetalleAlquiler.class)
                    .setParameter("alquiler", alquiler)
                    .setParameter("pelicula", pelicula)
                    .getSingleResult();

            // 3. Actualizar stock de la película
            pelicula.setStock(pelicula.getStock() + detalle.getCantidad());
            em.merge(pelicula);

            // 4. Verificar si ya no quedan más detalles -> marcar alquiler como devuelto
            long restantes = em.createQuery(
                    "SELECT COUNT(d) FROM DetalleAlquiler d WHERE d.alquiler = :alquiler", Long.class)
                    .setParameter("alquiler", alquiler)
                    .getSingleResult();

            if (restantes == 1) { // Sólo este detalle
                alquiler.setEstado(Alquiler.EstadoAlquiler.Devuelto);
                em.merge(alquiler);
            }

            em.getTransaction().commit();
            lblMensaje.setText("Película devuelta con éxito.");

            // Refrescar vistas
            cargarClientesConAlquilerActivo();
            if (panelAlquileres != null)
                panelAlquileres.cargarAlquileres();
            if (panelPeliculas != null)
                panelPeliculas.cargarPeliculas();

        } catch (Exception ex) {
            em.getTransaction().rollback();
            lblMensaje.setText("Error: " + ex.getMessage());
        }
    }
}
