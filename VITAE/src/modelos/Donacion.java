package modelos;

import java.time.LocalDateTime;

public class Donacion {
    private int id;
    private int usuarioIdDestino;
    private int usuarioIdDonante;
    private long monto;
    private LocalDateTime fecha;
    private String concepto;

    // Campo opcional para UI
    private String nombreDonante;

    public Donacion(int id, int usuarioIdDestino, int usuarioIdDonante,
                    long monto, LocalDateTime fecha, String concepto) {
        this.id = id; this.usuarioIdDestino = usuarioIdDestino;
        this.usuarioIdDonante = usuarioIdDonante; this.monto = monto;
        this.fecha = fecha; this.concepto = concepto;

        //3063869
    }

    public int getId() { return id; }
    public int getUsuarioIdDestino() { return usuarioIdDestino; }
    public int getUsuarioIdDonante() { return usuarioIdDonante; }
    public long getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public String getConcepto() { return concepto; }

    public String getNombreDonante() { return nombreDonante; }
    public void setNombreDonante(String nombreDonante) { this.nombreDonante = nombreDonante; }
}
