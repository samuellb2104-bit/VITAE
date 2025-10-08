package modelos;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String correo;
    private String contraseña;
    private String tipo_usuario; // "Donante" o "Fundacion"
    private String telefono;
    private String direccion;
    private String descripcion;

    public Usuario() {}

    public Usuario(int id_usuario, String nombre, String correo, String contraseña, 
                   String tipo_usuario, String telefono, String direccion, String descripcion) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
        this.tipo_usuario = tipo_usuario;
        this.telefono = telefono;
        this.direccion = direccion;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getTipo_usuario() { return tipo_usuario; }
    public void setTipo_usuario(String tipo_usuario) { this.tipo_usuario = tipo_usuario; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
