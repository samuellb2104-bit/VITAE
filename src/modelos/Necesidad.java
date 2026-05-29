package modelos;

import java.time.LocalDateTime;

public class Necesidad {
    private int idNecesidad;
    private int idFundacion;
    private String titulo;
    private String descripcion;
    private long metaMonto;
    private LocalDateTime fechaInicio;
    private String estado;
    private String nombreFundacion;

    public Necesidad(int idNecesidad, int idFundacion, String titulo,
                     String descripcion, long metaMonto,
                     LocalDateTime fechaInicio, String estado) {
        this.idNecesidad = idNecesidad;
        this.idFundacion = idFundacion;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.metaMonto = metaMonto;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    public int getIdNecesidad()          { return idNecesidad; }
    public int getIdFundacion()          { return idFundacion; }
    public String getTitulo()            { return titulo; }
    public String getDescripcion()       { return descripcion; }
    public long getMetaMonto()           { return metaMonto; }
    public LocalDateTime getFechaInicio(){ return fechaInicio; }
    public String getEstado()            { return estado; }
    public String getNombreFundacion()   { return nombreFundacion; }
    public void setNombreFundacion(String n) { this.nombreFundacion = n; }
}