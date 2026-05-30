package modelos;

import java.time.LocalDateTime;

public class Mensaje {
    private int idMensaje;
    private int idEmisor;
    private int idReceptor;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private String nombreEmisor;

    public Mensaje(int idMensaje, int idEmisor, int idReceptor,
                   String contenido, LocalDateTime fechaEnvio) {
        this.idMensaje = idMensaje;
        this.idEmisor = idEmisor;
        this.idReceptor = idReceptor;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
    }

    public int getIdMensaje()            { return idMensaje; }
    public int getIdEmisor()             { return idEmisor; }
    public int getIdReceptor()           { return idReceptor; }
    public String getContenido()         { return contenido; }
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public String getNombreEmisor()      { return nombreEmisor; }
    public void setNombreEmisor(String n){ this.nombreEmisor = n; }
}