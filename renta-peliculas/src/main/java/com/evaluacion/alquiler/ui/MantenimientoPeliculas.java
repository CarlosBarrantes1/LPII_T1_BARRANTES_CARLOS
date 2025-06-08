package com.evaluacion.alquiler.ui;

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

public class MantenimientoPeliculas extends JPanel {

    private JTextField txtTitulo, txtGenero, txtStock, txtPrecio;
    private JButton btnAgregar, btnEliminar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private EntityManager em;

    public MantenimientoPeliculas(EntityManager em) {
        this.em = em;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel formulario con etiquetas y campos para ingresar datos de película
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(10, 10, 20, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels y campos para cada atributo
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Título:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Género:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Stock:"), gbc);
        gbc.gridy++;
        form.add(new JLabel("Precio:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        txtTitulo = new JTextField(12);
        form.add(txtTitulo, gbc);
        gbc.gridy++;
        txtGenero = new JTextField(12);
        form.add(txtGenero, gbc);
        gbc.gridy++;
        txtStock = new JTextField(12);
        form.add(txtStock, gbc);
        gbc.gridy++;
        txtPrecio = new JTextField(12);
        form.add(txtPrecio, gbc);

        // Botones agregar y eliminar
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        btnAgregar = new JButton("Agregar");
        btnEliminar = new JButton("Eliminar");
        btnAgregar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEliminar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(btnAgregar);
        panelBotones.add(Box.createVerticalStrut(8));
        panelBotones.add(btnEliminar);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        form.add(panelBotones, gbc);

        add(form, BorderLayout.NORTH);

        // Tabla para mostrar las películas en la base de datos
        modelo = new DefaultTableModel(new Object[] { "ID", "Título", "Género", "Stock", "Precio" }, 0);
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
        tabla.setSelectionBackground(new Color(44, 62, 80));
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setGridColor(Color.BLACK);

        JTableHeader header = tabla.getTableHeader();
        header.setBackground(Color.BLACK);
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        cargarPeliculas();

        // Estilos de botones
        btnAgregar.setBackground(Color.BLACK);
        btnAgregar.setForeground(Color.WHITE);
        btnEliminar.setBackground(Color.BLACK);
        btnEliminar.setForeground(Color.WHITE);

        // Eventos botones
        btnAgregar.addActionListener(this::agregarPelicula);
        btnEliminar.addActionListener(this::eliminarPelicula);
    }

    // Método para cargar películas desde la BD y mostrarlas en la tabla
    public void cargarPeliculas() {
        modelo.setRowCount(0); // limpiar tabla
        List<Pelicula> lista = em.createQuery("SELECT p FROM Pelicula p", Pelicula.class).getResultList();
        for (Pelicula p : lista) {
            modelo.addRow(new Object[] {
                    p.getIdPelicula(), // supuesto método getId()
                    p.getTitulo(),
                    p.getGenero(),
                    p.getStock(),
                    p.getPrecio()
            });
        }
    }

    // Método para agregar una nueva película
    private void agregarPelicula(ActionEvent e) {
        try {
            String titulo = txtTitulo.getText();
            String genero = txtGenero.getText();
            int stock = Integer.parseInt(txtStock.getText());
            double precio = Double.parseDouble(txtPrecio.getText());

            if (titulo.isEmpty() || genero.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Título y Género son obligatorios.");
                return;
            }

            em.getTransaction().begin();
            Pelicula p = new Pelicula(null, titulo, genero, stock, precio); // Constructor que acepta ID null para
                                                                            // autogenerar
            em.persist(p);
            em.getTransaction().commit();

            cargarPeliculas(); // refrescar tabla

            // limpiar campos
            txtTitulo.setText("");
            txtGenero.setText("");
            txtStock.setText("");
            txtPrecio.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage());
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }

    // Método para eliminar película seleccionada en la tabla
    private void eliminarPelicula(ActionEvent e) {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una película para eliminar.");
            return;
        }
        Long id = (Long) modelo.getValueAt(fila, 0);

        try {
            em.getTransaction().begin();
            Pelicula p = em.find(Pelicula.class, id);
            if (p != null)
                em.remove(p);
            em.getTransaction().commit();
            cargarPeliculas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        }
    }
}
