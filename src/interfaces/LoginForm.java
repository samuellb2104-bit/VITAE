package interfaces;

import modelos.Usuario;
import servicios.UsuarioServicio;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    private static final Color VERDE       = new Color(0, 176, 156);
    private static final Color MORADO      = new Color(155, 89, 182);
    private static final Color FONDO       = new Color(240, 242, 248);
    private static final Color CARD        = Color.WHITE;
    private static final Color TEXTO       = new Color(40, 40, 60);
    private static final Color PLACEHOLDER = new Color(180, 180, 200);
    private static final Color BORDE_INPUT = new Color(220, 220, 235);

    private JTextField txtCorreo;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnTabLogin;
    private JButton btnTabRegistro;
    private boolean mostrandoLogin = true;

    private final UsuarioServicio servicio = new UsuarioServicio();

    public LoginForm() {
        setTitle("VITAE | Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(480, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(FONDO);

        JPanel card = crearCard();
        fondo.add(card);

        setContentPane(fondo);
    }

    private JPanel crearCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(400, 560));
        card.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        // Logo
        card.add(crearLogo());
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        // Título
        JLabel titulo = new JLabel("VITAE");
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(VERDE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titulo);

        JLabel subtitulo = new JLabel("Red de donaciones para fundaciones");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(PLACEHOLDER);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitulo);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tabs
        card.add(crearTabs());
        card.add(Box.createRigidArea(new Dimension(0, 24)));

        // Correo
        JLabel lblCorreo = new JLabel("Correo electrónico");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 12));
        lblCorreo.setForeground(TEXTO);
        lblCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblCorreo);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        txtCorreo = new JTextField();
        estilizarInput(txtCorreo, "✉  tu@correo.com");
        card.add(txtCorreo);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        // Contraseña
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        lblPass.setForeground(TEXTO);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        txtPass = new JPasswordField();
        estilizarInput(txtPass, "🔒  ········");
        card.add(txtPass);
        card.add(Box.createRigidArea(new Dimension(0, 28)));

        // Botón entrar
        btnLogin = crearBotonPrimario("Iniciar Sesión", VERDE);
        btnLogin.addActionListener(this::onLogin);
        card.add(btnLogin);

        return card;
    }

private JPanel crearLogo() {
    JPanel logo = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;

            // Fondo círculo suave
            g2.setColor(new Color(230, 245, 242));
            g2.fillOval(cx - 48, cy - 48, 96, 96);

            // Corazón VERDE (izquierda)
            dibujarCorazon(g2, cx - 52, cy - 10, 26, VERDE);

            // Corazón MORADO (derecha, sin solapar)
            dibujarCorazon(g2, cx - 4, cy - 10, 26, MORADO);
        }

        private void dibujarCorazon(Graphics2D g2, int x, int y, int size, Color color) {
            g2.setColor(color);
            int mitad = size / 2;
            // Arco izquierdo
            g2.fillArc(x, y - mitad, size, size, 0, 180);
            // Arco derecho
            g2.fillArc(x + mitad, y - mitad, size, size, 0, 180);
            // Triángulo inferior
            int[] px = {x, x + size + mitad, x + mitad};
            int[] py = {y, y, y + size};
            g2.fillPolygon(px, py, 3);
}
    };
    logo.setOpaque(false);
    logo.setPreferredSize(new Dimension(110, 100));
    logo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    return logo;
}

    private JPanel crearTabs() {
        JPanel tabs = new JPanel(new GridLayout(1, 2, 0, 0));
        tabs.setBackground(new Color(240, 240, 248));
        tabs.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 235), 1));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnTabLogin = new JButton("Iniciar Sesión");
        btnTabRegistro = new JButton("Crear Cuenta");

        estilizarTab(btnTabLogin, true);
        estilizarTab(btnTabRegistro, false);

        btnTabLogin.addActionListener(e -> {
            mostrandoLogin = true;
            estilizarTab(btnTabLogin, true);
            estilizarTab(btnTabRegistro, false);
            btnLogin.setText("Iniciar Sesión");
            btnLogin.removeActionListener(btnLogin.getActionListeners()[0]);
            btnLogin.addActionListener(this::onLogin);
        });

        btnTabRegistro.addActionListener(e -> {
            mostrandoLogin = false;
            estilizarTab(btnTabRegistro, true);
            estilizarTab(btnTabLogin, false);
            btnLogin.setText("Crear cuenta");
            btnLogin.removeActionListener(btnLogin.getActionListeners()[0]);
            btnLogin.addActionListener(ev -> onAbrirRegistro());
        });

        tabs.add(btnTabLogin);
        tabs.add(btnTabRegistro);
        tabs.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tabs;
    }

    private void estilizarTab(JButton btn, boolean activo) {
        btn.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(activo ? CARD : new Color(240, 240, 248));
        btn.setForeground(activo ? VERDE : PLACEHOLDER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarInput(JTextField field, String placeholder) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(TEXTO);
        field.setBackground(new Color(247, 247, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_INPUT, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Placeholder
        field.setText(placeholder);
        field.setForeground(PLACEHOLDER);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXTO);
                    if (field instanceof JPasswordField p) p.setEchoChar('•');
                }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER);
                    if (field instanceof JPasswordField p) p.setEchoChar((char) 0);
                }
            }
        });

        if (field instanceof JPasswordField p) p.setEchoChar((char) 0);
    }

    private JButton crearBotonPrimario(String texto, Color color) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    private void onLogin(ActionEvent e) {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (correo.isEmpty() || correo.equals("✉  tu@correo.com") ||
            pass.isEmpty() || pass.equals("🔒  ········")) {
            JOptionPane.showMessageDialog(this,
                "Ingresa correo y contraseña.", "Faltan datos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario u = servicio.login(correo, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas.", "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            abrirMenuPorRol(u);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error de conexión:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
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