package interfaces;

import dao.ConexionSQL;
import modelos.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulario de donación - Permite a un donante realizar una donación a una fundación
 * Puede abrirse de dos formas:
 * 1. Como diálogo modal desde una publicación específica
 * 2. Como ventana independiente para donar a cualquier fundación
 */
public class DonacionForm extends JDialog {

    /* ====== UI ====== */
    private static final Color VERDE   = new Color(0, 176, 156);
    private static final Color MORADO  = new Color(155, 89, 182);
    private static final Color AZUL    = new Color(52, 152, 219);
    private static final Color FONDO   = new Color(245, 245, 250);
    private static final Color CARD    = Color.WHITE;
    private static final Color BORDE   = new Color(200, 200, 210);

    /* ====== Componentes ====== */
    private final JComboBox<FundacionItem> cbFundacion;
    private final JTextField txtMonto = new JTextField(15);
    private final JTextField txtConcepto = new JTextField(25);
    private final JTextArea txtMensaje = new JTextArea(4, 25);
    private final JButton btnDonar = new JButton("Realizar donación");
    private final JButton btnCancelar = new JButton("Cancelar");
    private final JLabel lblPreview = new JLabel();

    /* ====== Estado ====== */
    private final Usuario donante;
    private boolean donacionExitosa = false;
    private final DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm");

    /**
     * Constructor para donar a una fundación específica (desde una publicación)
     */
    public DonacionForm(Frame owner, Usuario donante, int idFundacion, String nombreFundacion) {
        super(owner, "Nueva donación", true);
        this.donante = donante;
        
        // ComboBox con una sola fundación preseleccionada
        cbFundacion = new JComboBox<>();
        cbFundacion.addItem(new FundacionItem(idFundacion, nombreFundacion));
        cbFundacion.setEnabled(false); // No se puede cambiar
        
        inicializarUI();
    }

    /**
     * Constructor para donar a cualquier fundación (modo libre)
     */
    public DonacionForm(Frame owner, Usuario donante) {
        super(owner, "Nueva donación", true);
        this.donante = donante;
        
        // ComboBox con todas las fundaciones disponibles
        cbFundacion = new JComboBox<>();
        cargarFundaciones();
        
        inicializarUI();
    }

    private void inicializarUI() {
        setSize(650, 650);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);

        // Header
        root.add(crearHeader(), BorderLayout.NORTH);

        // Contenido
        JScrollPane scroll = new JScrollPane(crearContenido());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        root.add(scroll, BorderLayout.CENTER);

        // Botones
        root.add(crearPanelBotones(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    /* ====== Header ====== */
    private JPanel crearHeader() {
        JPanel p = new JPanel();
        p.setBackground(VERDE);
        p.setPreferredSize(new Dimension(0, 85));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titulo = new JLabel("Nueva donación");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Apoya a una fundación con tu contribución");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(220, 255, 250));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(titulo);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.add(sub);

        return p;
    }

    /* ====== Contenido ====== */
    private JPanel crearContenido() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Card de información del donante
        p.add(crearCardDonante());
        p.add(Box.createRigidArea(new Dimension(0, 18)));

        // Card del formulario
        p.add(crearCardFormulario());
        p.add(Box.createRigidArea(new Dimension(0, 18)));

        // Preview de la donación
        p.add(crearCardPreview());

        return p;
    }

    private JPanel crearCardDonante() {
        JPanel card = card();
        
        JLabel titulo = new JLabel("Donante");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(MORADO);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel nombre = new JLabel("Nombre: " + donante.getNombre());
        nombre.setFont(new Font("Arial", Font.PLAIN, 13));
        card.add(nombre);

        JLabel correo = new JLabel("Correo: " + donante.getCorreo());
        correo.setFont(new Font("Arial", Font.PLAIN, 12));
        correo.setForeground(new Color(100, 100, 120));
        card.add(correo);

        return card;
    }

    private JPanel crearCardFormulario() {
        JPanel card = card();
        
        JLabel titulo = new JLabel("Datos de la donación");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(VERDE);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Fundación
        JLabel lblFund = crearLabel("Fundación destino:");
        card.add(lblFund);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        estilizarComboBox(cbFundacion);
        card.add(cbFundacion);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Monto
        JLabel lblMonto = crearLabel("Monto (COP):");
        card.add(lblMonto);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        estilizarTextField(txtMonto);
        txtMonto.setToolTipText("Ingresa el monto en pesos colombianos");
        card.add(txtMonto);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Concepto
        JLabel lblConcepto = crearLabel("Concepto (opcional):");
        card.add(lblConcepto);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        estilizarTextField(txtConcepto);
        txtConcepto.setToolTipText("Breve descripción del propósito de la donación");
        card.add(txtConcepto);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Mensaje personal
        JLabel lblMensaje = crearLabel("Mensaje personal (opcional):");
        card.add(lblMensaje);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        txtMensaje.setFont(new Font("Arial", Font.PLAIN, 12));
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtMensaje.setToolTipText("Mensaje opcional para la fundación");
        JScrollPane scrollMsg = new JScrollPane(txtMensaje);
        scrollMsg.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        card.add(scrollMsg);

        // Listeners para actualizar preview
        txtMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarPreview(); }
        });
        cbFundacion.addActionListener(e -> actualizarPreview());
        txtConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { actualizarPreview(); }
        });

        return card;
    }

    private JPanel crearCardPreview() {
        JPanel card = card();
        card.setBackground(new Color(250, 250, 252));
        
        JLabel titulo = new JLabel("Vista previa");
        titulo.setFont(new Font("Arial", Font.BOLD, 13));
        titulo.setForeground(AZUL);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        lblPreview.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPreview.setForeground(new Color(60, 60, 80));
        card.add(lblPreview);

        actualizarPreview();
        return card;
    }

    /* ====== Botones ====== */
    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        p.setBackground(FONDO);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE));

        estilizarBotonSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());
        p.add(btnCancelar);

        estilizarBotonPrimario(btnDonar, VERDE);
        btnDonar.addActionListener(e -> procesarDonacion());
        p.add(btnDonar);

        return p;
    }

    /* ====== Lógica ====== */
    private void actualizarPreview() {
        StringBuilder html = new StringBuilder("<html>");
        
        FundacionItem fund = (FundacionItem) cbFundacion.getSelectedItem();
        String monto = txtMonto.getText().trim();
        String concepto = txtConcepto.getText().trim();

        if (fund != null) {
            html.append("<b>Fundación:</b> ").append(fund.nombre).append("<br>");
        }
        
        if (!monto.isEmpty()) {
            try {
                long montoNum = Long.parseLong(monto.replace(".", "").replace(",", ""));
                html.append("<b>Monto:</b> ").append(formatoCOP(montoNum)).append("<br>");
            } catch (NumberFormatException e) {
                html.append("<b>Monto:</b> <font color='red'>Formato inválido</font><br>");
            }
        } else {
            html.append("<b>Monto:</b> <font color='gray'>Pendiente</font><br>");
        }

        if (!concepto.isEmpty()) {
            html.append("<b>Concepto:</b> ").append(concepto).append("<br>");
        }

        html.append("<b>Fecha:</b> ").append(LocalDateTime.now().format(fFecha));
        html.append("</html>");

        lblPreview.setText(html.toString());
    }

    private void procesarDonacion() {
        // Validaciones
        FundacionItem fund = (FundacionItem) cbFundacion.getSelectedItem();
        if (fund == null) {
            JOptionPane.showMessageDialog(this, 
                "Selecciona una fundación.", 
                "Dato requerido", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sMonto = txtMonto.getText().trim();
        if (sMonto.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Ingresa el monto de la donación.", 
                "Dato requerido", 
                JOptionPane.WARNING_MESSAGE);
            txtMonto.requestFocus();
            return;
        }

        long monto;
        try {
            monto = Long.parseLong(sMonto.replace(".", "").replace(",", ""));
            if (monto <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "El monto debe ser un número válido mayor a cero.", 
                "Monto inválido", 
                JOptionPane.ERROR_MESSAGE);
            txtMonto.requestFocus();
            return;
        }

        // Confirmación
        String confirmMsg = String.format(
            "¿Confirmas la donación?\n\n" +
            "Fundación: %s\n" +
            "Monto: %s\n" +
            "Concepto: %s",
            fund.nombre,
            formatoCOP(monto),
            txtConcepto.getText().trim().isEmpty() ? "(Sin concepto)" : txtConcepto.getText().trim()
        );

        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMsg, 
            "Confirmar donación", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Insertar en BD
        String concepto = txtConcepto.getText().trim();
        boolean ok = insertarDonacion(
            fund.id, 
            donante.getId_usuario(), 
            monto, 
            concepto.isEmpty() ? null : concepto
        );

        if (ok) {
            donacionExitosa = true;
            JOptionPane.showMessageDialog(this, 
                "¡Donación realizada exitosamente!\n\nGracias por tu generosidad.", 
                "Donación exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "No se pudo procesar la donación.\nIntenta nuevamente.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ====== SQL ====== */
    private void cargarFundaciones() {
        String sql = """
            SELECT id_usuario, nombre 
            FROM dbo.Usuarios 
            WHERE tipo_usuario = 'Fundacion'
            ORDER BY nombre
        """;
        
        try (Connection cn = ConexionSQL.getConexion();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                cbFundacion.addItem(new FundacionItem(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre")
                ));
            }
            
            if (cbFundacion.getItemCount() == 0) {
                cbFundacion.addItem(new FundacionItem(0, "(No hay fundaciones disponibles)"));
                cbFundacion.setEnabled(false);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al cargar fundaciones.", 
                "Error de conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean insertarDonacion(int idFundacion, int idDonante, long monto, String concepto) {
        String sql = """
            INSERT INTO dbo.Donaciones (ReceptorId, DonadorId, Monto, Fecha)
            VALUES (?, ?, ?, SYSDATETIME())
        """;
        
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            ps.setInt(2, idDonante);
            ps.setLong(3, monto);
            return ps.executeUpdate() == 1;
        }  catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error: " + ex.getMessage() + 
                "\nSQLState: " + ex.getSQLState() + 
                "\nCódigo: " + ex.getErrorCode(),
                "Error SQL", JOptionPane.ERROR_MESSAGE);
}
return false;
    }
    /* ====== Helpers ====== */
    private JPanel card() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(60, 60, 80));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void estilizarTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(Color.WHITE);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void estilizarComboBox(JComboBox<?> cb) {
        cb.setFont(new Font("Arial", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void estilizarBotonPrimario(JButton btn, Color color) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
    }

    private void estilizarBotonSecundario(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(80, 80, 100));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE, 2),
            BorderFactory.createEmptyBorder(8, 22, 8, 22)
        ));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String formatoCOP(long valor) {
        return String.format("COP $%,d", valor).replace(',', '.');
    }

    /* ====== DTO interno ====== */
    private static class FundacionItem {
        int id;
        String nombre;

        FundacionItem(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    /* ====== Getters ====== */
    public boolean isDonacionExitosa() {
        return donacionExitosa;
    }

    /* ====== Main de prueba ====== */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {}

            // Usuario de prueba
            Usuario donante = new Usuario(1, "Juan Pérez", "juan@test.com", 
                "pass", "Donante", "3001234567", "Medellín", "Donante de prueba");

            // Modo 1: Donar a fundación específica
            // new DonacionForm(null, donante, 2, "Fundación Corazón Verde").setVisible(true);

            // Modo 2: Donar a cualquier fundación
            new DonacionForm(null, donante).setVisible(true);
        });
    }
}