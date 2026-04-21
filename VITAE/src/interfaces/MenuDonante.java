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
 * Módulo para DONANTE conectado a BD real.
 * - Feed de publicaciones de fundaciones
 * - Donar a una fundación desde una publicación
 * - Mis donaciones
 *
 * Requiere:
 *  - Usuario.getId_usuario(), getNombre(), getCorreo()
 *  - ConexionSQL.getConexion()
 */
public class MenuDonante extends JFrame {

    /* ====== UI ====== */
    private static final Color VERDE   = new Color(0, 176, 156);
    private static final Color MORADO  = new Color(155, 89, 182);
    private static final Color AZUL    = new Color(52, 152, 219);
    private static final Color NARANJA = new Color(243, 156, 18);
    private static final Color FONDO   = new Color(245, 245, 250);
    private static final Color CARD    = Color.WHITE;
    private static final Color BORDE   = new Color(230, 230, 240);
    private final DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm");

    /* ====== Estado ====== */
    private final Usuario usuario;
    private final JPanel panelContenido = new JPanel(new BorderLayout());
    private String vistaActual = "feed";

    public MenuDonante(Usuario usuario) {
        this.usuario = usuario;
        setTitle("VITAE | Donante");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearMain(), BorderLayout.CENTER);
        setContentPane(root);

        cambiarVista("feed");
    }

    /* ====== Header ====== */
    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD);
        p.setPreferredSize(new Dimension(0, 65));
        p.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDE));

        JLabel logo = new JLabel(" VITAE | Donante");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(MORADO);
        logo.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        p.add(logo, BorderLayout.WEST);

        // busqueda simple (categoría/título)
        JTextField txtBuscar = new JTextField(26);
        txtBuscar.putClientProperty("JComponent.roundRect", true);
        JButton btnBuscar = new JButton("Buscar");
        estilizarPrimario(btnBuscar, AZUL);
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.add(new JLabel("🔎"));
        center.add(txtBuscar);
        center.add(btnBuscar);
        btnBuscar.addActionListener(e -> mostrarFeed(txtBuscar.getText().trim()));
        p.add(center, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        der.setBackground(CARD);
        der.add(new JLabel("👤 " + usuario.getNombre()));
        JButton salir = new JButton("Salir");
        estilizarPeligro(salir);
        salir.addActionListener(e -> cerrarSesion());
        der.add(salir);
        p.add(der, BorderLayout.EAST);

        return p;
    }

    /* ====== Main ====== */
    private JPanel crearMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(FONDO);

        main.add(crearSidebar(), BorderLayout.WEST);

        panelContenido.setBackground(FONDO);
        JScrollPane sc = new JScrollPane(panelContenido);
        sc.setBorder(null);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        main.add(sc, BorderLayout.CENTER);

        main.add(crearColumnaDerecha(), BorderLayout.EAST);
        return main;
    }

    private JPanel crearSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(CARD);
        s.setPreferredSize(new Dimension(240, 0));
        s.setBorder(BorderFactory.createMatteBorder(0,0,0,1,BORDE));
        s.add(Box.createRigidArea(new Dimension(0, 12)));

        s.add(opSidebar(" Feed", "feed"));
        s.add(opSidebar(" Mis donaciones", "mis_donaciones"));
        s.add(opSidebar(" Mensajes", "mensajes")); // TODO
        s.add(Box.createVerticalGlue());

        JPanel perfil = new JPanel();
        perfil.setLayout(new BoxLayout(perfil, BoxLayout.Y_AXIS));
        perfil.setBackground(new Color(250,250,252));
        perfil.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE,1),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel n = new JLabel(usuario.getNombre());
        n.setForeground(MORADO); n.setFont(new Font("Arial", Font.BOLD, 12));
        perfil.add(n);
        perfil.add(new JLabel( usuario.getCorreo()));
        s.add(perfil);
        s.add(Box.createRigidArea(new Dimension(0, 12)));

        return s;
    }

    private JPanel crearColumnaDerecha() {
        JPanel r = new JPanel();
        r.setLayout(new BoxLayout(r, BoxLayout.Y_AXIS));
        r.setBackground(FONDO);
        r.setPreferredSize(new Dimension(280, 0));
        r.setBorder(BorderFactory.createEmptyBorder(16,12,16,12));

        JPanel card = card();
        JLabel t = new JLabel(" Resumen");
        t.setForeground(MORADO); t.setFont(new Font("Arial", Font.BOLD, 13));
        card.add(t); card.add(Box.createRigidArea(new Dimension(0,8)));

        // KPIs del donante
        long donadoTotal = sumarMontoDonadoTotal(usuario.getId_usuario());
        int cantDons = contarDonacionesHechas(usuario.getId_usuario());
        int fundacionesUnicas = contarFundacionesApoyadas(usuario.getId_usuario());

        card.add(itemStat("Total donado", formatoCOP(donadoTotal), VERDE));
        card.add(itemStat("Donaciones", String.valueOf(cantDons), AZUL));
        card.add(itemStat("Fundaciones apoyadas", String.valueOf(fundacionesUnicas), NARANJA));
        r.add(card);

        return r;
    }

    private JPanel opSidebar(String txt, String vista) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        p.setBackground(vista.equals(vistaActual) ? new Color(244,240,250) : CARD);
        p.setMaximumSize(new Dimension(240, 46));
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Arial", vista.equals(vistaActual) ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(vista.equals(vistaActual) ? MORADO : new Color(60, 60, 80));
        p.add(l);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { cambiarVista(vista); }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!vista.equals(vistaActual)) p.setBackground(new Color(245,245,250));
            }
            public void mouseExited (java.awt.event.MouseEvent e) {
                if (!vista.equals(vistaActual)) p.setBackground(CARD);
            }
        });
        return p;
    }

    /* ====== Vistas ====== */
    private void cambiarVista(String v) {
        vistaActual = v;
        panelContenido.removeAll();

        switch (v) {
            case "feed" -> mostrarFeed(null);
            case "mis_donaciones" -> mostrarMisDonaciones();
            case "mensajes" -> mostrarMensajesTODO();
            default -> panelContenido.add(new JLabel("En construcción"));
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    /** Feed con filtro opcional por texto (título/categoría) */
    private void mostrarFeed(String filtro) {
        JPanel cont = stack();
        cont.add(banner("¡Hola, " + usuario.getNombre() + "!", "Descubre publicaciones de fundaciones"));

        List<PubRow> pubs = listarPublicaciones(filtro, 40);
        if (pubs.isEmpty()) {
            cont.add(infoVacio("No hay publicaciones para mostrar."));
        } else {
            for (PubRow p : pubs) {
                cont.add(cardPublicacion(p));
                cont.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }
        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    private void mostrarMisDonaciones() {
        JPanel cont = stack();
        JLabel tt = new JLabel(" Mis donaciones");
        tt.setFont(new Font("Arial", Font.BOLD, 16));
        tt.setForeground(new Color(60,60,80));
        cont.add(tt);
        cont.add(Box.createRigidArea(new Dimension(0, 10)));

        List<DonRow> dons = listarDonacionesHechas(usuario.getId_usuario(), 60);
        if (dons.isEmpty()) {
            cont.add(infoVacio("Aún no has realizado donaciones."));
        } else {
            for (DonRow d : dons) {
                cont.add(cardDonacion(d));
                cont.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    private void mostrarMensajesTODO() {
        JPanel cont = stack();
        cont.add(banner(" Mensajes", "Este módulo se conectará a dbo.Mensajes"));
        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    /* ====== UI helpers ====== */
    private JPanel card() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(CARD);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE,1),
            BorderFactory.createEmptyBorder(14,14,14,14)
        ));
        return c;
    }
    private JPanel stack() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(18,24,18,24));
        return p;
    }
    private Component wrap(JComponent c) { c.setAlignmentX(Component.LEFT_ALIGNMENT); return c; }

    private JPanel banner(String t1, String t2) {
        JPanel c = card();
        JLabel a = new JLabel(t1); a.setForeground(MORADO); a.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel b = new JLabel(t2); b.setForeground(new Color(90,90,110));
        c.add(a); c.add(Box.createRigidArea(new Dimension(0,6))); c.add(b);
        return c;
    }
    private JPanel infoVacio(String text) {
        JPanel c = card(); JLabel l = new JLabel(text); l.setForeground(new Color(110,110,130)); c.add(l); return c;
    }
    private JPanel itemStat(String label, String valor, Color color) {
        JPanel i = new JPanel(new BorderLayout());
        i.setBackground(CARD);
        JLabel a = new JLabel(label); a.setFont(new Font("Arial", Font.PLAIN, 11)); a.setForeground(new Color(100,100,120));
        JLabel b = new JLabel(valor); b.setFont(new Font("Arial", Font.BOLD, 13)); b.setForeground(color);
        i.add(a, BorderLayout.WEST); i.add(b, BorderLayout.EAST);
        return i;
    }

    private JPanel cardPublicacion(PubRow p) {
        JPanel c = card();

        // Encabezado (fundación + fecha + estado)
        JLabel h = new JLabel( (p.nombre_fundacion == null ? ("Fundación #" + p.id_fundacion) : p.nombre_fundacion)
                + "  •   " + p.fecha_publicacion.format(fFecha)
                + (p.estado != null ? " • " + p.estado : ""));
        h.setFont(new Font("Arial", Font.PLAIN, 11));
        h.setForeground(new Color(120,120,140));
        c.add(h);

        // Título
        c.add(Box.createRigidArea(new Dimension(0,8)));
        JLabel titulo = new JLabel(p.titulo == null ? "(Sin título)" : p.titulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(new Color(50,50,70));
        c.add(titulo);

        // Categoría
        if (p.categoria != null && !p.categoria.isBlank()) {
            JLabel chip = new JLabel(" " + p.categoria + " ");
            chip.setFont(new Font("Arial", Font.PLAIN, 10));
            chip.setOpaque(true);
            chip.setBackground(new Color(244, 240, 250));
            chip.setForeground(MORADO);
            chip.setBorder(BorderFactory.createLineBorder(MORADO));
            c.add(Box.createRigidArea(new Dimension(0,6)));
            c.add(chip);
        }

        // Descripción
        c.add(Box.createRigidArea(new Dimension(0,6)));
        JLabel desc = new JLabel("<html>" + escape(p.descripcion == null ? "" : p.descripcion) + "</html>");
        desc.setFont(new Font("Arial", Font.PLAIN, 13));
        c.add(desc);

        // Imagen URL (solo texto por ahora)
        if (p.imagen_url != null && !p.imagen_url.isBlank()) {
            JLabel img = new JLabel("" + p.imagen_url);
            img.setFont(new Font("Arial", Font.ITALIC, 11));
            img.setForeground(AZUL);
            c.add(Box.createRigidArea(new Dimension(0,6)));
            c.add(img);
        }

        // Acciones
        c.add(Box.createRigidArea(new Dimension(0, 10)));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setBackground(CARD);
        JButton donar = new JButton("Donar");
        estilizarPrimario(donar, VERDE);
        donar.addActionListener(e -> abrirDialogDonar(p.id_fundacion, p.nombre_fundacion));
        acciones.add(donar);
        c.add(acciones);

        return c;
    }

    private JPanel cardDonacion(DonRow d) {
        JPanel c = card();
        JLabel a = new JLabel( (d.nombre_fundacion == null ? ("Fundación #" + d.usuario_id_destino) : d.nombre_fundacion));
        a.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel b = new JLabel(  d.fecha.format(fFecha));
        b.setFont(new Font("Arial", Font.PLAIN, 11));
        b.setForeground(new Color(120,120,140));
        JLabel m = new JLabel(formatoCOP(d.monto));
        m.setFont(new Font("Arial", Font.BOLD, 16));
        m.setForeground(VERDE);

        c.add(a); c.add(b);
        if (d.concepto != null && !d.concepto.isBlank()) {
            JLabel con = new JLabel("📝 " + d.concepto);
            con.setFont(new Font("Arial", Font.PLAIN, 12));
            c.add(con);
        }
        c.add(Box.createRigidArea(new Dimension(0,6)));
        c.add(m);
        return c;
    }

    private void estilizarPrimario(JButton b, Color color) {
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    private void estilizarPeligro(JButton b) {
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setBackground(new Color(231,76,60)); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    private String escape(String s) { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
    private String formatoCOP(long v) {
        // formato sencillo tipo COP $1.234.567
        return String.format("COP $%,d", v).replace(',', '.');
    }

    /* ====== Diálogo de Donar (INSERT real) ====== */
    private void abrirDialogDonar(int idFundacion, String nombreFundacion) {
    DonacionForm dialog = new DonacionForm(this, usuario, idFundacion, nombreFundacion);
    dialog.setVisible(true);
    
    // Si la donación fue exitosa, refrescar la vista
    if (dialog.isDonacionExitosa()) {
        cambiarVista("mis_donaciones");
    }
}

    /* ====== SQL directo ====== */

    // Publicaciones de todas las fundaciones (filtro por título/categoría)
    private List<PubRow> listarPublicaciones(String filtro, int limit) {
        String base = """
            SELECT TOP (?) p.id_publicacion, p.id_fundacion, p.titulo, p.descripcion, p.categoria,
                   p.imagen_url, p.fecha_publicacion, p.estado,
                   u.nombre AS nombre_fundacion
            FROM dbo.Publicaciones p
            LEFT JOIN dbo.Usuarios u ON u.id_usuario = p.id_fundacion
        """;
        String where = (filtro == null || filtro.isBlank())
                ? ""
                : " WHERE (p.titulo LIKE ? OR p.categoria LIKE ?)";
        String order = " ORDER BY p.fecha_publicacion DESC";

        List<PubRow> list = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(base + where + order)) {
            int idx = 1;
            ps.setInt(idx++, limit);
            if (!where.isEmpty()) {
                String like = "%" + filtro + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PubRow p = new PubRow();
                    p.id_publicacion   = rs.getInt("id_publicacion");
                    p.id_fundacion     = rs.getInt("id_fundacion");
                    p.titulo           = rs.getString("titulo");
                    p.descripcion      = rs.getString("descripcion");
                    p.categoria        = rs.getString("categoria");
                    p.imagen_url       = rs.getString("imagen_url");
                    Timestamp ts       = rs.getTimestamp("fecha_publicacion");
                    p.fecha_publicacion= (ts == null) ? LocalDateTime.now() : ts.toLocalDateTime();
                    p.estado           = rs.getString("estado");
                    p.nombre_fundacion = rs.getString("nombre_fundacion");
                    list.add(p);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    private boolean insertarDonacion(int idFundacion, int idDonante, long monto, String concepto) {
        String sql = """
            INSERT INTO dbo.Donaciones (usuario_id_destino, usuario_id_donante, monto, fecha, concepto)
            VALUES (?, ?, ?, SYSDATETIME(), ?)
        """;
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            ps.setInt(2, idDonante);
            ps.setLong(3, monto);
            ps.setString(4, concepto);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) { ex.printStackTrace(); }
        return false;
    }

    private List<DonRow> listarDonacionesHechas(int idDonante, int limit) {
        String sql = """
            SELECT TOP (?) d.id, d.usuario_id_destino, d.usuario_id_donante, d.monto, d.fecha, d.concepto,
                   u.nombre AS nombre_fundacion
            FROM dbo.Donaciones d
            LEFT JOIN dbo.Usuarios u ON u.id_usuario = d.usuario_id_destino
            WHERE d.usuario_id_donante = ?
            ORDER BY d.fecha DESC
        """;
        List<DonRow> out = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, idDonante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonRow d = new DonRow();
                    d.id = rs.getInt("id");
                    d.usuario_id_destino = rs.getInt("usuario_id_destino");
                    d.usuario_id_donante = rs.getInt("usuario_id_donante");
                    d.monto = rs.getLong("monto");
                    Timestamp ts = rs.getTimestamp("fecha");
                    d.fecha = (ts == null) ? LocalDateTime.now() : ts.toLocalDateTime();
                    d.concepto = rs.getString("concepto");
                    d.nombre_fundacion = rs.getString("nombre_fundacion");
                    out.add(d);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return out;
    }

    private long sumarMontoDonadoTotal(int idDonante) {
        String sql = "SELECT COALESCE(SUM(monto),0) FROM dbo.Donaciones WHERE usuario_id_donante = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDonante);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getLong(1) : 0L; }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0L;
    }
    private int contarDonacionesHechas(int idDonante) {
        String sql = "SELECT COUNT(*) FROM dbo.Donaciones WHERE usuario_id_donante = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDonante);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }
    private int contarFundacionesApoyadas(int idDonante) {
        String sql = "SELECT COUNT(DISTINCT usuario_id_destino) FROM dbo.Donaciones WHERE usuario_id_donante = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idDonante);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }

    /* ====== DTOs internos ====== */
    private static class PubRow {
        int id_publicacion;
        int id_fundacion;
        String titulo;
        String descripcion;
        String categoria;
        String imagen_url;
        LocalDateTime fecha_publicacion;
        String estado;
        String nombre_fundacion;
    }
    private static class DonRow {
        int id;
        int usuario_id_destino;
        int usuario_id_donante;
        long monto;
        LocalDateTime fecha;
        String concepto;
        String nombre_fundacion;
    }

    /* ====== Sesión ====== */
    private void cerrarSesion() {
        int x = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (x == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}
