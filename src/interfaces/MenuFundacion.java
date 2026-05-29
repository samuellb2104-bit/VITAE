package interfaces;

import dao.ConexionSQL;
import modelos.Usuario;
import dao.NecesidadDAO;
import modelos.Necesidad;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu para FUNDACION conectado a BD real (tabla dbo.Publicaciones).
 * - Usa Usuario.getId_usuario() y demás getters tal como están en tus modelos.
 * - No usa servicios/DAO externos para evitar dependencias (consulta directa con ConexionSQL).
 * - Vistas: Inicio (feed), Mis Publicaciones. "Donaciones" y otras quedan como TODO.
 */
public class MenuFundacion extends JFrame {

    /* ================== CONFIG UI ================== */
    private static final Color COLOR_VERDE = new Color(0, 176, 156);
    private static final Color COLOR_MORADO = new Color(155, 89, 182);
    private static final Color COLOR_AZUL   = new Color(52, 152, 219);
    private static final Color COLOR_NARANJA= new Color(243, 156, 18);
    private static final Color COLOR_FONDO  = new Color(245, 245, 250);
    private static final Color COLOR_TARJETA= Color.WHITE;
    private static final Color COLOR_BORDE  = new Color(230, 230, 240);

    private final DateTimeFormatter fFecha = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm");

    /* ================== ESTADO ================== */
    private final Usuario usuario;
    private final JPanel panelContenido = new JPanel(new BorderLayout());
    private String vistaActual = "inicio";

    public MenuFundacion(Usuario usuario) {
        this.usuario = usuario;

        setTitle("VITAE | Fundación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_FONDO);
        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(crearMain(), BorderLayout.CENTER);

        setContentPane(root);
        cambiarVista("inicio");
    }

    /* ================== HEADER ================== */
    private JPanel crearHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 65));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE));

        JLabel lblLogo = new JLabel("💚 VITAE | Fundación");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 22));
        lblLogo.setForeground(COLOR_VERDE);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.add(lblLogo, BorderLayout.WEST);

        JButton btnNueva = new JButton("✍️ Nueva publicación");
        estilizarPrimario(btnNueva, COLOR_MORADO);
        btnNueva.addActionListener(e -> abrirDialogNuevaPublicacion());
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.add(btnNueva);
        panel.add(centro, BorderLayout.CENTER);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        der.setBackground(Color.WHITE);
        der.add(new JLabel("🏢 " + usuario.getNombre()));
        JButton salir = new JButton("Salir");
        estilizarPeligro(salir);
        salir.addActionListener(e -> cerrarSesion());
        der.add(salir);
        panel.add(der, BorderLayout.EAST);

        return panel;
    }

    /* ================== MAIN (Sidebar + Contenido + Derecha) ================== */
    private JPanel crearMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(COLOR_FONDO);

        main.add(crearSidebar(), BorderLayout.WEST);

        panelContenido.setBackground(COLOR_FONDO);
        JScrollPane scroll = new JScrollPane(panelContenido);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        main.add(scroll, BorderLayout.CENTER);

        main.add(crearColumnaDerecha(), BorderLayout.EAST);
        return main;
    }

    private JPanel crearSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(Color.WHITE);
        s.setPreferredSize(new Dimension(240, 0));
        s.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDE));

        s.add(Box.createRigidArea(new Dimension(0, 12)));
        s.add(opSidebar("🏠 Inicio", "inicio"));
        s.add(opSidebar("📝 Mis publicaciones", "publicaciones"));
        s.add(opSidebar("💰 Donaciones", "donaciones")); // TODO vista
        s.add(opSidebar("📋 Necesidades", "necesidades"));
        s.add(Box.createVerticalGlue());

        // mini perfil
        JPanel perfil = new JPanel();
        perfil.setLayout(new BoxLayout(perfil, BoxLayout.Y_AXIS));
        perfil.setBackground(new Color(250, 250, 252));
        perfil.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE,1),
                BorderFactory.createEmptyBorder(12,12,12,12)
        ));
        JLabel n = new JLabel(usuario.getNombre());
        n.setFont(new Font("Arial", Font.BOLD, 12));
        n.setForeground(COLOR_VERDE);
        perfil.add(n);
        perfil.add(new JLabel("📧 " + usuario.getCorreo()));
        s.add(perfil);
        s.add(Box.createRigidArea(new Dimension(0, 12)));

        return s;
    }

    private JPanel opSidebar(String texto, String vista) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        p.setBackground(vista.equals(vistaActual) ? new Color(240,250,248) : Color.WHITE);
        p.setMaximumSize(new Dimension(240, 46));
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", vista.equals(vistaActual) ? Font.BOLD : Font.PLAIN, 13));
        l.setForeground(vista.equals(vistaActual) ? COLOR_VERDE : new Color(60, 60, 80));
        p.add(l);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { cambiarVista(vista); }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!vista.equals(vistaActual)) p.setBackground(new Color(245,245,250));
            }
            public void mouseExited (java.awt.event.MouseEvent e) {
                if (!vista.equals(vistaActual)) p.setBackground(Color.WHITE);
            }
        });
        return p;
    }

    private JPanel crearColumnaDerecha() {
        JPanel r = new JPanel();
        r.setLayout(new BoxLayout(r, BoxLayout.Y_AXIS));
        r.setBackground(COLOR_FONDO);
        r.setPreferredSize(new Dimension(280, 0));
        r.setBorder(BorderFactory.createEmptyBorder(16,12,16,12));

        // Resumen simple (dinámico desde BD)
        JPanel card = card();
        JLabel t = new JLabel("📊 Resumen");
        t.setForeground(COLOR_VERDE); t.setFont(new Font("Arial", Font.BOLD, 13));
        card.add(t); card.add(Box.createRigidArea(new Dimension(0,8)));

        int totalPosts = contarPublicacionesFundacion(usuario.getId_usuario());
        card.add(itemStat("Publicaciones", String.valueOf(totalPosts), COLOR_MORADO));
        // Aquí podrías sumar donaciones reales si tu tabla Donaciones las vincula a la fundación.
        r.add(card);

        return r;
    }

    /* ================== VISTAS ================== */
    private void cambiarVista(String vista) {
        vistaActual = vista;
        panelContenido.removeAll();

        switch (vista) {
            case "inicio" -> mostrarFeed();
            case "publicaciones" -> mostrarMisPublicaciones();
            case "donaciones" -> mostrarDonacionesTODO(); // placeholder
            case "necesidades" -> mostrarNecesidades();
            default -> panelContenido.add(new JLabel("Módulo en construcción"));
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarFeed() {
        JPanel cont = stack();
        cont.add(banner("¡Bienvenido, " + usuario.getNombre() + "!", "Comparte tus avances y conecta con donantes"));

        List<PubRow> posts = listarPublicacionesFundacion(usuario.getId_usuario(), 30);
        if (posts.isEmpty()) {
            cont.add(infoVacio("Aún no tienes publicaciones. ¡Crea la primera!"));
        } else {
            for (PubRow p : posts) {
                cont.add(cardPublicacion(p));
                cont.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    private void mostrarMisPublicaciones() {
        JPanel cont = stack();
        JLabel tt = new JLabel("📝 Mis publicaciones");
        tt.setFont(new Font("Arial", Font.BOLD, 16));
        tt.setForeground(new Color(60,60,80));
        cont.add(tt);
        cont.add(Box.createRigidArea(new Dimension(0, 10)));

        List<PubRow> posts = listarPublicacionesFundacion(usuario.getId_usuario(), 100);
        if (posts.isEmpty()) {
            cont.add(infoVacio("No hay publicaciones para mostrar."));
        } else {
            for (PubRow p : posts) {
                cont.add(cardPublicacion(p));
                cont.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    private void mostrarDonacionesTODO() {
        JPanel cont = stack();
        cont.add(banner("💰 Donaciones", "Este módulo se conectará a dbo.Donaciones"));
        cont.add(infoVacio("Pronto verás tu historial de donaciones aquí."));
        panelContenido.add(wrap(cont), BorderLayout.NORTH);
    }

    /* ================== UI HELPERS ================== */
    private JPanel card() {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(COLOR_TARJETA);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE,1),
                BorderFactory.createEmptyBorder(14,14,14,14)
        ));
        return c;
    }

    private JPanel stack() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(18,24,18,24));
        return p;
    }

    private Component wrap(JComponent c) { c.setAlignmentX(Component.LEFT_ALIGNMENT); return c; }

    private JPanel banner(String titulo, String sub) {
        JPanel c = card();
        JLabel a = new JLabel(titulo);
        a.setForeground(COLOR_VERDE); a.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel b = new JLabel(sub);
        b.setForeground(new Color(90, 90, 110));
        c.add(a); c.add(Box.createRigidArea(new Dimension(0,6))); c.add(b);
        return c;
    }

    private JPanel infoVacio(String text) {
        JPanel c = card();
        JLabel l = new JLabel(text);
        l.setForeground(new Color(110,110,130));
        c.add(l);
        return c;
    }

    private JPanel itemStat(String label, String valor, Color color) {
        JPanel i = new JPanel(new BorderLayout());
        i.setBackground(COLOR_TARJETA);
        JLabel a = new JLabel(label);
        a.setFont(new Font("Arial", Font.PLAIN, 11));
        a.setForeground(new Color(100,100,120));
        JLabel b = new JLabel(valor);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setForeground(color);
        i.add(a, BorderLayout.WEST); i.add(b, BorderLayout.EAST);
        return i;
    }

    private JPanel cardPublicacion(PubRow p) {
        JPanel card = card();

        // header
        JLabel h = new JLabel("⏰ " + p.fecha_publicacion.format(fFecha) + (p.estado != null ? " • " + p.estado : ""));
        h.setFont(new Font("Arial", Font.PLAIN, 11));
        h.setForeground(new Color(120,120,140));
        card.add(h);
        card.add(Box.createRigidArea(new Dimension(0,8)));

        // título
        JLabel titulo = new JLabel(p.titulo == null ? "(Sin título)" : p.titulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 15));
        titulo.setForeground(new Color(50,50,70));
        card.add(titulo);

        // categoría
        if (p.categoria != null && !p.categoria.isBlank()) {
            JLabel chip = new JLabel(" " + p.categoria + " ");
            chip.setFont(new Font("Arial", Font.PLAIN, 10));
            chip.setOpaque(true);
            chip.setBackground(new Color(240, 250, 248));
            chip.setForeground(COLOR_VERDE);
            chip.setBorder(BorderFactory.createLineBorder(COLOR_VERDE));
            chip.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(Box.createRigidArea(new Dimension(0,6)));
            card.add(chip);
        }

        card.add(Box.createRigidArea(new Dimension(0,6)));

        // descripción
        String html = "<html>" + escape(p.descripcion == null ? "" : p.descripcion) + "</html>";
        JLabel contenido = new JLabel(html);
        contenido.setFont(new Font("Arial", Font.PLAIN, 13));
        contenido.setForeground(new Color(40,40,60));
        card.add(contenido);

        // imagen (solo mostramos el URL como link simple si existe)
        if (p.imagen_url != null && !p.imagen_url.isBlank()) {
            JLabel img = new JLabel("🖼️ " + p.imagen_url);
            img.setFont(new Font("Arial", Font.ITALIC, 11));
            img.setForeground(COLOR_AZUL);
            card.add(Box.createRigidArea(new Dimension(0,6)));
            card.add(img);
        }

        // acciones mínimas
        card.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setBackground(COLOR_TARJETA);
        JButton editar = new JButton("✏️ Editar");
        JButton eliminar = new JButton("🗑️ Eliminar");
        estilizarSecundario(editar);
        estilizarPeligro(eliminar);
        editar.addActionListener(e -> JOptionPane.showMessageDialog(this, "TODO: pantalla de edición"));
        eliminar.addActionListener(e -> eliminarPublicacion(p.id_publicacion));
        acciones.add(editar); acciones.add(eliminar);
        card.add(acciones);

        return card;
    }

    private void estilizarPrimario(JButton b, Color color) {
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(color); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    private void estilizarSecundario(JButton b) {
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setBackground(Color.WHITE); b.setForeground(new Color(70,70,90));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE,1),
                BorderFactory.createEmptyBorder(6,12,6,12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    private void estilizarPeligro(JButton b) {
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setBackground(new Color(231,76,60)); b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    /* ================== NUEVA PUBLICACION (INSERT real) ================== */
    private void abrirDialogNuevaPublicacion() {
        JTextField txtTitulo = new JTextField(28);
        JTextField txtCategoria = new JTextField(18);
        JTextField txtImagen = new JTextField(28);
        JTextArea  txtDesc = new JTextArea(6, 28);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_FONDO);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6); c.fill = GridBagConstraints.HORIZONTAL; c.weightx=1;

        int y=0;
        c.gridx=0; c.gridy=y; form.add(new JLabel("Título:"), c); c.gridx=1; form.add(txtTitulo, c); y++;
        c.gridx=0; c.gridy=y; form.add(new JLabel("Categoría:"), c); c.gridx=1; form.add(txtCategoria, c); y++;
        c.gridx=0; c.gridy=y; form.add(new JLabel("Imagen URL:"), c); c.gridx=1; form.add(txtImagen, c); y++;
        c.gridx=0; c.gridy=y; c.anchor = GridBagConstraints.NORTH; form.add(new JLabel("Descripción:"), c);
        c.gridx=1; c.fill = GridBagConstraints.BOTH; form.add(new JScrollPane(txtDesc), c);

        int r = JOptionPane.showConfirmDialog(this, form, "Nueva publicación", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            String titulo = txtTitulo.getText().trim();
            String categoria = txtCategoria.getText().trim();
            String imagen = txtImagen.getText().trim();
            String descripcion = txtDesc.getText().trim();

            if (titulo.isEmpty() && descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Escribe al menos título o descripción.", "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = insertarPublicacion(usuario.getId_usuario(), titulo, descripcion, categoria, imagen);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Publicación creada.");
                cambiarVista("inicio");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo crear la publicación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================== SQL DIRECTO (sin servicios) ================== */

    private List<PubRow> listarPublicacionesFundacion(int idFundacion, int limit) {
        String sql = """
            SELECT TOP (?) id_publicacion, id_fundacion, titulo, descripcion, categoria, imagen_url, fecha_publicacion, estado
            FROM dbo.Publicaciones
            WHERE id_fundacion = ?
            ORDER BY fecha_publicacion DESC
        """;
        List<PubRow> list = new ArrayList<>();
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, idFundacion);
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
                    list.add(p);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private int contarPublicacionesFundacion(int idFundacion) {
        String sql = "SELECT COUNT(*) FROM dbo.Publicaciones WHERE id_fundacion = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private boolean insertarPublicacion(int idFundacion, String titulo, String descripcion,
                                        String categoria, String imagenUrl) {
        String sql = """
            INSERT INTO dbo.Publicaciones
            (id_fundacion, titulo, descripcion, categoria, imagen_url, fecha_publicacion, estado)
            VALUES (?, ?, ?, ?, ?, SYSDATETIME(), 'ACTIVA')
        """;
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idFundacion);
            ps.setString(2, titulo.isEmpty()? null : titulo);
            ps.setString(3, descripcion.isEmpty()? null : descripcion);
            ps.setString(4, categoria.isEmpty()? null : categoria);
            ps.setString(5, (imagenUrl==null || imagenUrl.isBlank())? null : imagenUrl);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void eliminarPublicacion(int idPublicacion) {
        int ok = JOptionPane.showConfirmDialog(this, "¿Eliminar publicación #" + idPublicacion + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM dbo.Publicaciones WHERE id_publicacion = ?";
        try (Connection cn = ConexionSQL.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPublicacion);
            int rows = ps.executeUpdate();
            if (rows == 1) {
                JOptionPane.showMessageDialog(this, "Eliminada.");
                cambiarVista(vistaActual);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró/Eliminó.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ================== DTO interno para renderizar ================== */
    private static class PubRow {
        int id_publicacion;
        int id_fundacion;
        String titulo;
        String descripcion;
        String categoria;
        String imagen_url;
        LocalDateTime fecha_publicacion;
        String estado;
    }
    private void mostrarNecesidades() {
    JPanel cont = stack();
    JLabel tt = new JLabel("📋 Necesidades");
    tt.setFont(new Font("Arial", Font.BOLD, 16));
    tt.setForeground(new Color(60, 60, 80));
    cont.add(tt);
    cont.add(Box.createRigidArea(new Dimension(0, 10)));

    JButton btnNueva = new JButton("+ Nueva necesidad");
    estilizarPrimario(btnNueva, COLOR_VERDE);
    btnNueva.addActionListener(e -> abrirDialogNuevaNecesidad());
    cont.add(btnNueva);
    cont.add(Box.createRigidArea(new Dimension(0, 15)));

    NecesidadDAO dao = new NecesidadDAO();
    List<Necesidad> lista = dao.listarPorFundacion(usuario.getId_usuario());

    if (lista.isEmpty()) {
        cont.add(infoVacio("No tienes necesidades registradas aún."));
    } else {
        for (Necesidad n : lista) {
            cont.add(cardNecesidad(n));
            cont.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }
    panelContenido.add(wrap(cont), BorderLayout.NORTH);
}

private JPanel cardNecesidad(Necesidad n) {
    JPanel card = card();

    JLabel estado = new JLabel(n.getEstado());
    estado.setFont(new Font("Arial", Font.PLAIN, 11));
    estado.setForeground(n.getEstado().equals("ACTIVA") ? COLOR_VERDE : new Color(150, 150, 150));
    card.add(estado);

    card.add(Box.createRigidArea(new Dimension(0, 6)));
    JLabel titulo = new JLabel(n.getTitulo());
    titulo.setFont(new Font("Arial", Font.BOLD, 14));
    card.add(titulo);

    if (n.getDescripcion() != null && !n.getDescripcion().isBlank()) {
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        JLabel desc = new JLabel("<html>" + n.getDescripcion() + "</html>");
        desc.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(desc);
    }

    if (n.getMetaMonto() > 0) {
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        JLabel meta = new JLabel("Meta: COP $" + String.format("%,d", n.getMetaMonto()).replace(',', '.'));
        meta.setFont(new Font("Arial", Font.BOLD, 12));
        meta.setForeground(COLOR_MORADO);
        card.add(meta);
    }

    card.add(Box.createRigidArea(new Dimension(0, 10)));
    JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    acciones.setBackground(COLOR_TARJETA);
    JButton cerrar = new JButton("Cerrar necesidad");
    estilizarPeligro(cerrar);
    cerrar.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Cerrar esta necesidad?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new NecesidadDAO().cerrar(n.getIdNecesidad());
            cambiarVista("necesidades");
        }
    });
    acciones.add(cerrar);
    card.add(acciones);

    return card;
}

private void abrirDialogNuevaNecesidad() {
    JTextField txtTitulo = new JTextField(25);
    JTextArea txtDesc = new JTextArea(4, 25);
    txtDesc.setLineWrap(true);
    txtDesc.setWrapStyleWord(true);
    JTextField txtMeta = new JTextField(25);

    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(6, 6, 6, 6);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.weightx = 1;

    int y = 0;
    c.gridx = 0; c.gridy = y; form.add(new JLabel("Título:"), c);
    c.gridx = 1; form.add(txtTitulo, c); y++;
    c.gridx = 0; c.gridy = y; form.add(new JLabel("Descripción:"), c);
    c.gridx = 1; form.add(new JScrollPane(txtDesc), c); y++;
    c.gridx = 0; c.gridy = y; form.add(new JLabel("Meta en COP (0 = sin meta):"), c);
    c.gridx = 1; form.add(txtMeta, c);

    int r = JOptionPane.showConfirmDialog(this, form,
        "Nueva necesidad", JOptionPane.OK_CANCEL_OPTION);

    if (r == JOptionPane.OK_OPTION) {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio.");
            return;
        }
        long meta = 0;
        try {
            String sm = txtMeta.getText().trim();
            if (!sm.isEmpty()) meta = Long.parseLong(sm.replace(".", "").replace(",", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Meta inválida, debe ser un número.");
            return;
        }
        boolean ok = new NecesidadDAO().crear(
            usuario.getId_usuario(), titulo,
            txtDesc.getText().trim(), meta
        );
        if (ok) {
            JOptionPane.showMessageDialog(this, "Necesidad creada.");
            cambiarVista("necesidades");
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear la necesidad.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

    private void cerrarSesion() {
        int x = JOptionPane.showConfirmDialog(this, "¿Cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (x == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}
