package modelos;

import java.time.LocalDateTime;

public class Publicacion {
    private int id;
    private int usuario_id;
    private String contenido;
    private LocalDateTime fecha_creacion;
    private int likes;
    private int donaciones;

    public Publicacion(int id, int usuario_id, String contenido,
                       LocalDateTime fecha_creacion, int likes, int donaciones) {
        this.id = id; this.usuario_id = usuario_id; this.contenido = contenido;
        this.fecha_creacion = fecha_creacion; this.likes = likes; this.donaciones = donaciones;
    }

    public int getId() { return id; }
    public int getUsuario_id() { return usuario_id; }
    public String getContenido() { return contenido; }
    public LocalDateTime getFecha_creacion() { return fecha_creacion; }
    public int getLikes() { return likes; }
    public int getDonaciones() { return donaciones; }
    
}
