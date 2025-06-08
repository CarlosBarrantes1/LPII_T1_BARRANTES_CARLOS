package com.evaluacion.alquiler.ui;

import com.evaluacion.alquiler.model.Cliente;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel de mantenimiento (ABM) de clientes.
 * Adaptado para la entidad {@link Cliente} del paquete
 * com.evaluacion.alquiler.model.
 */
public class MantenimientoCliente extends JPanel {

    private final JTextField txtNombre = new JTextField(18);
    private final JTextField txtEmail = new JTextField(18);
    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnGuardar = new JButton("Guardar");
    private final JButton btnCancelar = new JButton("Cancelar");

    private final DefaultTableModel modelo;
    private final JTable tabla;

    private final EntityManager em;
    private Long idSeleccionado = null;

    public MantenimientoCliente(EntityManager em) {
        this.em = em;

        /* --------------------------- Layout / estilos -------------------------- */
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(new EmptyBorder(15, 15, 10, 15));

        // ----- Campos -----
        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCampos.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelCampos.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panelCampos.add(txtEmail, gbc);

        panelSuperior.add(panelCampos);

        // ----- Botones -----
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        panelBotones.setBackground(Color.WHITE);
        for (JButton b : new JButton[] { btnAgregar, btnEliminar, btnEditar, btnGuardar, btnCancelar }) {
            b.setBackground(Color.BLACK);
            b.setForeground(Color.WHITE);
            panelBotones.add(b);
        }
        btnEditar.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(false);

        panelSuperior.add(Box.createVerticalStrut(10));
        panelSuperior.add(panelBotones);
        panelSuperior.add(Box.createVerticalStrut(15));

        add(panelSuperior, BorderLayout.NORTH);

        /* --------------------------- Tabla -------------------------- */
        modelo = new DefaultTableModel(new Object[] { "ID", "Nombre", "Email" }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabla = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int r, int c) {
                Component comp = super.prepareRenderer(renderer, r, c);
                if (!isRowSelected(r)) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(230, 230, 230));
                    comp.setForeground(Color.BLACK);
                } else {
                    comp.setBackground(new Color(44, 62, 80));
                    comp.setForeground(Color.WHITE);
                }
                return comp;
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
        scroll.setBorder(new EmptyBorder(10, 15, 10, 15));
        add(scroll, BorderLayout.CENTER);

        /* --------------------------- Eventos -------------------------- */
        btnAgregar.addActionListener(this::agregarCliente);
        btnEliminar.addActionListener(this::eliminarCliente);
        btnEditar.addActionListener(this::editarCliente);
        btnGuardar.addActionListener(this::guardarCliente);
        btnCancelar.addActionListener(e -> prepararParaAgregar());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });

        cargarClientes();
    }

    /*
     * ============================ Carga / selección =============================
     */
    public void cargarClientes() {
        modelo.setRowCount(0);
        List<Cliente> lista = em.createQuery("SELECT c FROM Cliente c", Cliente.class)
                .getResultList();
        for (Cliente c : lista) {
            modelo.addRow(new Object[] { c.getIdCliente(), c.getNombre(), c.getEmail() });
        }
        prepararParaAgregar();
    }

    private void seleccionarFila() {
        int fila = tabla.getSelectedRow();
        if (fila == -1)
            return;
        idSeleccionado = (Long) modelo.getValueAt(fila, 0);
        txtNombre.setText((String) modelo.getValueAt(fila, 1));
        txtEmail.setText((String) modelo.getValueAt(fila, 2));
        txtNombre.setEnabled(false);
        txtEmail.setEnabled(false);
        btnEditar.setEnabled(true);
        btnEliminar.setEnabled(true);
        btnAgregar.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnCancelar.setEnabled(true);
    }

    private void prepararParaAgregar() {
        idSeleccionado = null;
        txtNombre.setText("");
        txtEmail.setText("");
        txtNombre.setEnabled(true);
        txtEmail.setEnabled(true);
        btnAgregar.setEnabled(true);
        btnEditar.setEnabled(false);

        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnCancelar.setEnabled(false);
        tabla.clearSelection();
    }

    /* ============================ Acciones CRUD ============================= */
    private void agregarCliente(ActionEvent e) {
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        if (nombre.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Email son obligatorios.");
            return;
        }
        // ¿email duplicado?
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.email) = :email", Long.class)
                .setParameter("email", email.toLowerCase())
                .getSingleResult();
        if (count > 0) {
            JOptionPane.showMessageDialog(this, "El email ya está registrado.");
            return;
        }
        try {
            em.getTransaction().begin();
            Cliente c = new Cliente(null, nombre, email);
            em.persist(c);
            em.getTransaction().commit();
            cargarClientes();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al agregar: " + ex.getMessage());
        }
    }

    private void eliminarCliente(ActionEvent e) {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para eliminar.");
            return;
        }
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, idSeleccionado);
            if (c != null)
                em.remove(c);
            em.getTransaction().commit();
            cargarClientes();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
        }
    }

    private void editarCliente(ActionEvent e) {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para editar.");
            return;
        }
        txtNombre.setEnabled(true);
        txtEmail.setEnabled(true);
        btnGuardar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnAgregar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnCancelar.setEnabled(true);
    }

    private void guardarCliente(ActionEvent e) {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente para guardar.");
            return;
        }
        String nombre = txtNombre.getText().trim();
        String email = txtEmail.getText().trim();
        if (nombre.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Email son obligatorios.");
            return;
        }
        // ¿email duplicado en otro cliente?
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Cliente c WHERE LOWER(c.email) = :email AND c.idCliente <> :id", Long.class)
                .setParameter("email", email.toLowerCase())
                .setParameter("id", idSeleccionado)
                .getSingleResult();
        if (count > 0) {
            JOptionPane.showMessageDialog(this, "El email ya está registrado.");
            return;
        }
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, idSeleccionado);
            if (c != null) {
                c.setNombre(nombre);
                c.setEmail(email);
                em.merge(c);
            }
            em.getTransaction().commit();
            cargarClientes();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}
