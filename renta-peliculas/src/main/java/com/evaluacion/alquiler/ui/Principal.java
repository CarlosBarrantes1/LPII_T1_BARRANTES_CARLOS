package com.evaluacion.alquiler.ui;

import com.evaluacion.alquiler.util.JPAUtil; // ← ruta nueva del util
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del sistema de alquiler de películas.
 */
public class Principal extends JFrame {

    public Principal() {

        /*------------------------------------
         * Configuración básica de la ventana
         *-----------------------------------*/
        setTitle("SAP - Sistema de Alquiler de Películas");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        /*------------------------------------
         * EntityManager único (compartido)
         *-----------------------------------*/
        EntityManager em = JPAUtil.getEntityManagerFactory()
                .createEntityManager();

        /*------------------------------------
         * Pestañas principales
         *-----------------------------------*/
        JTabbedPane tabs = new JTabbedPane();

        /* ===== Pestaña Inicio ===== */
        tabs.addTab("Inicio", crearPanelInicio());

        /* ===== Pestaña Clientes ===== */
        MantenimientoCliente panelClientes = new MantenimientoCliente(em);
        tabs.addTab("Clientes", panelClientes);

        /* ===== Pestaña Películas ===== */
        MantenimientoPeliculas panelPeliculas = new MantenimientoPeliculas(em);
        tabs.addTab("Películas", panelPeliculas);

        /* ===== Pestaña Gestionar Alquileres (con subtabs) ===== */
        MantenimientoAlquileres panelAlquileres = new MantenimientoAlquileres(em);
        DevolucionPeliculas panelDevolucion = new DevolucionPeliculas(
                panelAlquileres, panelPeliculas, em);
        VentanaAlquiler panelRegistrar = new VentanaAlquiler(
                em, panelPeliculas,
                panelDevolucion, panelAlquileres);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Registrar Alquiler", panelRegistrar);
        subTabs.addTab("Devolución", panelDevolucion);
        subTabs.addTab("Listado de Alquileres", panelAlquileres);
        tabs.addTab("Gestionar Alquileres", subTabs);

        /*------------------------------------
         * Eventos de cambio de pestaña
         *-----------------------------------*/
        tabs.addChangeListener(e -> {
            Component sel = tabs.getSelectedComponent();

            // Al entrar a la pestaña de sub-tabs de alquileres
            if (sel == subTabs) {
                panelRegistrar.cargarCombos();
                panelDevolucion.cargarClientesConAlquilerActivo();
                panelPeliculas.cargarPeliculas();
                panelAlquileres.cargarAlquileres();
            }
            // Al entrar a la pestaña de películas
            if (sel == panelPeliculas) {
                panelPeliculas.cargarPeliculas();
            }
        });

        add(tabs);
    }

    /** Crea el panel de bienvenida (texto + imagen) */
    private JPanel crearPanelInicio() {
        JPanel panelInicio = new JPanel(new BorderLayout());
        return panelInicio;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Principal().setVisible(true));
    }
}
