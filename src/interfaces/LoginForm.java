package interfaces;

import modelos.Usuario;
import servicios.UsuarioServicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {
    private final JTextField txtCorreo = new JTextField(25);
    private final JPasswordField txtPass = new JPasswordField(25);
    private final JButton btnLogin = new JButton("Entrar");
    private final JButton btnRegistro = new JButton("Crear cuenta");
    private final UsuarioServicio servicio = new UsuarioServicio();
    
    private static final Color COLOR_VERDE = new Color(0, 176, 156);
    private static final Color COLOR_MORADO = new Color(155, 89, 182);
    private static final Color COLOR_FONDO = new Color(245, 245, 250);

    public LoginForm() {
        setTitle("VITAE | Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 900);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(COLOR_FONDO);

        // Panel header con logo grande
        JPanel panelHeader = crearPanelHeader();
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);

        // Panel contenido
        JPanel panelContenido = crearPanelContenido();
        panelPrincipal.add(panelContenido, BorderLayout.CENTER);

        setContentPane(panelPrincipal);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));

        // Logo grande (círculos)
        JPanel panelLogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Círculos pequeños
                g2d.setColor(COLOR_VERDE);
                g2d.fillOval(w / 2 - 90, h / 2 - 80, 60, 60);

                g2d.setColor(COLOR_MORADO);
                g2d.fillOval(w / 2 + 30, h / 2 - 80, 60, 60);

                // Corazones (simplificado con dos arcos grandes)
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(COLOR_VERDE);
                g2d.fillArc(w / 2 - 110, h / 2 - 50, 100, 100, 0, 180);

                g2d.setColor(COLOR_MORADO);
                g2d.fillArc(w / 2 + 10, h / 2 - 50, 100, 100, 0, 180);
            }
        };
        panelLogo.setPreferredSize(new Dimension(250, 180));
        panelLogo.setOpaque(false);
        panelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(panelLogo);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitulo = new JLabel("VITAE");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitulo.setForeground(COLOR_VERDE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Red de donaciones para fundaciones");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(100, 100, 120));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblSubtitulo);

        return panel;
    }

    private JPanel crearPanelContenido() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 50, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 0, 12, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Correo
        JLabel lblCorreo = new JLabel("Correo electrónico");
        lblCorreo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCorreo.setForeground(COLOR_VERDE);
        c.gridx = 0; c.gridy = 0;
        panel.add(lblCorreo, c);

        estilizarTextField(txtCorreo);
        c.gridy = 1; c.ipady = 10;
        panel.add(txtCorreo, c);

        // Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPass.setForeground(COLOR_VERDE);
        c.gridy = 2; c.ipady = 0; c.insets = new Insets(25, 0, 12, 0);
        panel.add(lblPass, c);

        estilizarTextField(txtPass);
        c.gridy = 3; c.ipady = 10; c.insets = new Insets(12, 0, 12, 0);
        panel.add(txtPass, c);

        // Botón login
        estilizarBotonPrimario(btnLogin, COLOR_VERDE);
        c.gridy = 4; c.insets = new Insets(30, 0, 12, 0); c.ipady = 0;
        panel.add(btnLogin, c);

        // Botón registro
        estilizarBotonSecundario(btnRegistro, COLOR_MORADO);
        c.gridy = 5; c.insets = new Insets(12, 0, 12, 0);
        panel.add(btnRegistro, c);

        btnLogin.addActionListener(this::onLogin);
        btnRegistro.addActionListener(e -> onAbrirRegistro());

        return panel;
    }

    private void estilizarTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
    }

    private void estilizarBotonPrimario(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
    }

    private void estilizarBotonSecundario(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 2));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(color);
            }
        });
    }

    private void onLogin(ActionEvent e) {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa correo y contraseña.", "Faltan datos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario u = servicio.login(correo, pass);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        abrirMenuPorRol(u);
    }

    private void onAbrirRegistro() {
        RegistroForm dlg = new RegistroForm(this);
        dlg.setVisible(true);
    }

    private void abrirMenuPorRol(Usuario u) {
        dispose();
        if ("Fundacion".equalsIgnoreCase(u.getTipo_usuario())) {
            new MenuFundacion(u).setVisible(true);
        } else {
            new MenuDonante(u).setVisible(true);
        }
    }
}