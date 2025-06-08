package com.evaluacion.alquiler.ui;

import com.evaluacion.alquiler.model.Alquiler;
import com.evaluacion.alquiler.model.Cliente;
import com.evaluacion.alquiler.model.DetalleAlquiler;
import com.evaluacion.alquiler.model.Pelicula;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel Swing encargado de listar, buscar y eliminar alquileres.
 * Ajustado para trabajar con las entidades:
 * - {@link Alquiler}
 * - {@link Cliente}
 * - {@link Pelicula}
 * - {@link DetalleAlquiler}
 */
public class MantenimientoAlquileres extends JPanel {

    private final JTable tabla;
    private final DefaultTableModel modelo;
    private final JButton btnEliminar;
    private final JButton btnBuscar;
    private final JTextField txtBuscar;
    private final EntityManager em;

    public MantenimientoAlquileres(EntityManager em) {
        this.em = em;

        /*
         * -------------------------------- Layout general
         * --------------------------------
         */
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        /*
         * -------------------------------- Panel de búsqueda
         * ------------------------------
         */
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(Color.WHITE);
        panelBusqueda.setBorder(new EmptyBorder(15, 15, 25, 15));

        JLabel lblBuscar = new JLabel("Buscar cliente o película:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 15));
        panelBusqueda.add(lblBuscar);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 15));
        panelBusqueda.add(txtBuscar);

        btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 15));
        btnBuscar.setBackground(Color.BLACK);
        btnBuscar.setForeground(Color.WHITE);
        panelBusqueda.add(btnBuscar);

        add(panelBusqueda, BorderLayout.NORTH);

        /*
         * -------------------------------- Tabla
         * ------------------------------------------
         */
        modelo = new DefaultTableModel(new Object[] {
                "ID", "Cliente", "Fecha", "Película", "Cantidad", "Total", "Estado" }, 0);

        tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(230, 230, 230));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(44, 62, 80));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        };
        tabla.setFont(new Font("Arial", Font.PLAIN, 15));
        tabla.setRowHeight(28);
        tabla.setSelectionBackground(new Color(44, 62, 80));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(Color.BLACK);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 15f));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(scroll, BorderLayout.CENTER);

        /*
         * -------------------------------- Botón eliminar
         * ---------------------------------
         */
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 15));
        btnEliminar.setBackground(Color.BLACK);
        btnEliminar.setForeground(Color.WHITE);

        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelBoton.add(btnEliminar);
        add(panelBoton, BorderLayout.SOUTH);

        /*
         * -------------------------------- Eventos
         * ----------------------------------------
         */
        btnEliminar.addActionListener(this::eliminarAlquiler);
        btnBuscar.addActionListener(this::buscarAlquileres);

        /* Carga inicial */
        cargarAlquileres();
    }

    /** Carga todos los alquileres disponibles */
    public void cargarAlquileres() {
        cargarAlquileresPorBusqueda("");
    }

    /** Busca alquileres según texto en cliente o película */
    private void buscarAlquileres(ActionEvent e) {
        cargarAlquileresPorBusqueda(txtBuscar.getText().trim());
    }

    /**
     * Rellena la tabla con los detalles de alquiler filtrados.
     * 
     * @param texto texto de búsqueda; si está vacío muestra todos.
     */
    private void cargarAlquileresPorBusqueda(String texto) {
        modelo.setRowCount(0);
        List<DetalleAlquiler> detalles;

        if (texto == null || texto.isEmpty()) {
            detalles = em.createQuery("SELECT d FROM DetalleAlquiler d", DetalleAlquiler.class)
                    .getResultList();
        } else {
            detalles = em.createQuery(
                    "SELECT d FROM DetalleAlquiler d " +
                            "WHERE LOWER(d.alquiler.cliente.nombre)  LIKE :texto " +
                            "   OR LOWER(d.pelicula.titulo)          LIKE :texto",
                    DetalleAlquiler.class)
                    .setParameter("texto", "%" + texto.toLowerCase() + "%")
                    .getResultList();
        }

        for (DetalleAlquiler d : detalles) {
            Alquiler a = d.getAlquiler();
            Cliente c = a.getCliente();
            Pelicula p = d.getPelicula();

            modelo.addRow(new Object[] {
                    a.getIdAlquiler(),
                    (c != null ? c.getNombre() : ""),
                    a.getFecha(),
                    (p != null ? p.getTitulo() : ""),
                    d.getCantidad(),
                    a.getTotal(),
                    a.getEstado()
            });
        }
    }

    /** Elimina el alquiler seleccionado (y sus detalles por cascada, si existe) */
    private void eliminarAlquiler(ActionEvent e) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alquiler para eliminar.");
            return;
        }

        Long id = (Long) modelo.getValueAt(fila, 0);
        try {
            em.getTransaction().begin();
            Alquiler alquiler = em.find(Alquiler.class, id);
            if (alquiler != null) {
                em.remove(alquiler); // Suponiendo cascade = ALL en DetalleAlquiler
            }
            em.getTransaction().commit();
            cargarAlquileres();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }
}
