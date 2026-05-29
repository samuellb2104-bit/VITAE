package interfaces;

import modelos.Usuario;
import servicios.UsuarioServicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegistroForm extends JDialog {
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtCorreo = new JTextField(22);
    private final JPasswordField txtPass = new JPasswordField(22);
    private final JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Donante", "Fundacion"});
    private final JTextField txtTelefono = new JTextField(22);
    private final JTextField txtCiudad = new JTextField(22);
    private final JTextArea txtDesc = new JTextArea(3, 22);
    private final JButton btnCrear = new JButton("Crear cuenta");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final UsuarioServicio servicio = new UsuarioServicio();

    private static final Color COLOR_VERDE = new Color(0, 176, 156);
    //private static final Color COLOR_MORADO = new Color(155, 89, 182);
    private static final Color COLOR_FONDO = new Color(245, 245, 250);

    public RegistroForm(Frame owner) {
        super(owner, "Crear cuenta en VITAE", true);
        setSize(580, 700);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Header
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(COLOR_VERDE);
        panelHeader.setPreferredSize(new Dimension(0, 70));
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("Únete a VITAE");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Crea tu cuenta para comenzar");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(220, 255, 250));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelHeader.add(titulo);
        panelHeader.add(Box.createRigidArea(new Dimension(0, 3)));
        panelHeader.add(subtitulo);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // Contenido con scroll
        JPanel panelContenido = new JPanel(new GridBagLayout());
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(10, 0, 10, 0);

        int y = 0;

        // Nombre
        agregarCampo(panelContenido, c, "Nombre completo:", txtNombre, y++);

        // Correo
        agregarCampo(panelContenido, c, "Correo electrónico:", txtCorreo, y++);

        // Contraseña
        agregarCampo(panelContenido, c, "Contraseña:", txtPass, y++);

        // Tipo
        JLabel lblTipo = new JLabel("Tipo de usuario:");
        lblTipo.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTipo.setForeground(COLOR_VERDE);
        c.gridx = 0; c.gridy = y; c.weightx = 0.3;
        panelContenido.add(lblTipo, c);

        cbTipo.setFont(new Font("Arial", Font.PLAIN, 12));
        cbTipo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 1));
        cbTipo.setBackground(Color.WHITE);
        c.gridx = 1; c.weightx = 1.0; c.ipady = 6;
        panelContenido.add(cbTipo, c);
        y++;

        // Teléfono
        agregarCampo(panelContenido, c, "Teléfono:", txtTelefono, y++);

        // Ciudad
        agregarCampo(panelContenido, c, "Ciudad:", txtCiudad, y++);

        // Descripción
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDesc.setForeground(COLOR_VERDE);
        c.gridx = 0; c.gridy = y; c.weightx = 0.3; c.ipady = 0;
        c.anchor = GridBagConstraints.NORTH;
        panelContenido.add(lblDesc, c);

        txtDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDesc.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 1));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        c.gridx = 1; c.weightx = 1.0; c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        panelContenido.add(new JScrollPane(txtDesc), c);

        JScrollPane scrollPane = new JScrollPane(panelContenido);
        scrollPane.setBackground(COLOR_FONDO);
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        scrollPane.setBorder(null);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 210)));

        estilizarBoton(btnCancelar, Color.WHITE, COLOR_VERDE, true);
        panelBotones.add(btnCancelar);

        estilizarBoton(btnCrear, COLOR_VERDE, Color.WHITE, false);
        panelBotones.add(btnCrear);

        btnCancelar.addActionListener(e -> dispose());
        btnCrear.addActionListener(this::onCrear);

        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        setContentPane(panelPrincipal);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, String etiqueta, JTextField field, int y) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(COLOR_VERDE);
        c.gridx = 0; c.gridy = y; c.weightx = 0.3; c.ipady = 0;
        panel.add(lbl, c);

        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210), 1));
        field.setBackground(Color.WHITE);
        c.gridx = 1; c.weightx = 1.0; c.ipady = 6;
        panel.add(field, c);
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto, boolean contorno) {
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFocusPainted(false);
        if (contorno) {
            btn.setBorder(BorderFactory.createLineBorder(COLOR_VERDE, 2));
        } else {
            btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (contorno) {
                    btn.setBackground(COLOR_VERDE);
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(COLOR_VERDE.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(fondo);
                btn.setForeground(texto);
            }
        });
    }

    private void onCrear(ActionEvent e) {
        if (txtNombre.getText().isBlank() || txtCorreo.getText().isBlank() || txtPass.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Nombre, correo y contraseña son obligatorios.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario u = new Usuario(0,
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                new String(txtPass.getPassword()),
                cbTipo.getSelectedItem().toString(),
                txtTelefono.getText().trim(),
                txtCiudad.getText().trim(),
                txtDesc.getText().trim()
        );
        boolean ok = servicio.registrar(u);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cuenta creada. Ya puedes iniciar sesión.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo crear la cuenta (¿correo duplicado?).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}