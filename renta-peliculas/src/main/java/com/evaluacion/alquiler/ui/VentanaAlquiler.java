package com.evaluacion.alquiler.ui;

import com.evaluacion.alquiler.model.Cliente;
import com.evaluacion.alquiler.model.Pelicula;
import com.evaluacion.alquiler.model.Alquiler;
import com.evaluacion.alquiler.model.DetalleAlquiler;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class VentanaAlquiler extends JPanel {
    private JComboBox<Cliente> comboClientes;
    private JComboBox<Pelicula> comboPeliculas;
    private JTextField txtCantidad;
    private JLabel lblTotal;
    private JButton btnRegistrar;
    private JLabel lblMensaje;

    private EntityManager em;
    private MantenimientoPeliculas panelPeliculas;
    private DevolucionPeliculas panelDevolucion;
    private MantenimientoAlquileres panelAlquileres;

    public VentanaAlquiler(EntityManager em, MantenimientoPeliculas panelPeliculas, DevolucionPeliculas panelDevolucion,
            MantenimientoAlquileres panelAlquileres) {
        this.em = em;
        this.panelPeliculas = panelPeliculas;
        this.panelDevolucion = panelDevolucion;
        this.panelAlquileres = panelAlquileres;

        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 20, 30));

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        comboClientes = new JComboBox<>();
        comboClientes.setFont(fieldFont);
        comboClientes.setBackground(Color.WHITE);
        comboClientes.setForeground(Color.BLACK);

        comboPeliculas = new JComboBox<>();
        comboPeliculas.setFont(fieldFont);
        comboPeliculas.setBackground(Color.WHITE);
        comboPeliculas.setForeground(Color.BLACK);

        txtCantidad = new JTextField("1", 5);
        txtCantidad.setFont(fieldFont);
        txtCantidad.setBackground(Color.WHITE);
        txtCantidad.setForeground(Color.BLACK);

        lblTotal = new JLabel("Total: S/ 0.00");
        lblTotal.setFont(labelFont);
        lblTotal.setForeground(Color.BLACK);

        btnRegistrar = new JButton("Registrar Alquiler");
        btnRegistrar.setFont(labelFont);
        btnRegistrar.setBackground(Color.BLACK);
        btnRegistrar.setForeground(Color.WHITE);

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(labelFont);
        lblMensaje.setForeground(Color.BLACK);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Cliente:") {
            {
                setFont(labelFont);
                setForeground(Color.BLACK);
            }
        }, gbc);
        gbc.gridx = 1;
        add(comboClientes, gbc);

        // Película
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Película:") {
            {
                setFont(labelFont);
                setForeground(Color.BLACK);
            }
        }, gbc);
        gbc.gridx = 1;
        add(comboPeliculas, gbc);

        // Cantidad
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Cantidad:") {
            {
                setFont(labelFont);
                setForeground(Color.BLACK);
            }
        }, gbc);
        gbc.gridx = 1;
        add(txtCantidad, gbc);

        // Total
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel(""), gbc); // Espacio
        gbc.gridx = 1;
        add(lblTotal, gbc);

        // Botón
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(btnRegistrar, gbc);

        // Mensaje
        gbc.gridy = 5;
        add(lblMensaje, gbc);

        comboPeliculas.addActionListener(e -> calcularTotal());
        txtCantidad.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calcularTotal();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calcularTotal();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calcularTotal();
            }
        });

        btnRegistrar.addActionListener(this::registrarAlquiler);

        cargarCombos();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        cargarCombos();
    }

    public void cargarCombos() {
        comboClientes.removeAllItems();
        comboPeliculas.removeAllItems();

        List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
        for (Cliente c : clientes)
            comboClientes.addItem(c);

        List<Pelicula> peliculas = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class).getResultList();
        for (Pelicula p : peliculas)
            comboPeliculas.addItem(p);

        calcularTotal();
    }

    private void calcularTotal() {
        Pelicula peli = (Pelicula) comboPeliculas.getSelectedItem();
        int cantidad = 1;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException ignored) {
        }

        double precio = (peli != null && peli.getPrecio() != null) ? peli.getPrecio() : 0.0;
        double total = cantidad * precio;
        lblTotal.setText("Total: S/ " + String.format("%.2f", total));
    }

    private void registrarAlquiler(ActionEvent evt) {
        try {
            Cliente cliente = (Cliente) comboClientes.getSelectedItem();
            Pelicula pelicula = (Pelicula) comboPeliculas.getSelectedItem();
            int cantidad = Integer.parseInt(txtCantidad.getText());

            if (cliente == null || pelicula == null || cantidad <= 0) {
                lblMensaje.setText("Datos inválidos.");
                return;
            }
            if (pelicula.getStock() < cantidad) {
                lblMensaje.setText("No hay stock suficiente para realizar el alquiler.");
                return;
            }

            double precio = (pelicula != null && pelicula.getPrecio() != null) ? pelicula.getPrecio() : 0.0;
            double total = cantidad * precio;

            em.getTransaction().begin();

            // Crea el alquiler con fecha actual, cliente, total y estado activo
            Alquiler alquiler = new Alquiler();
            alquiler.setFecha(LocalDate.now());
            alquiler.setCliente(cliente);
            alquiler.setTotal(total);
            alquiler.setEstado(Alquiler.EstadoAlquiler.Activo); // Ajustar enum si es distinto

            em.persist(alquiler);

            // Crea el detalle del alquiler
            DetalleAlquiler detalle = new DetalleAlquiler();
            detalle.setAlquiler(alquiler);
            detalle.setPelicula(pelicula);
            detalle.setCantidad(cantidad);

            em.persist(detalle);

            // Actualiza el stock
            pelicula.setStock(pelicula.getStock() - cantidad);
            em.merge(pelicula);

            em.getTransaction().commit();

            lblMensaje.setText("Alquiler realizado con éxito");

            // Actualiza interfaces si existen
            if (panelPeliculas != null)
                panelPeliculas.cargarPeliculas();
            if (panelDevolucion != null)
                panelDevolucion.cargarClientesConAlquilerActivo();
            if (panelAlquileres != null)
                panelAlquileres.cargarAlquileres();

        } catch (Exception ex) {
            lblMensaje.setText("Error al registrar: " + ex.getMessage());
        }
    }
}
