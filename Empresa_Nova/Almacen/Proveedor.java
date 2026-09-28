package Almacen;

public class Proveedor {
    private final String CedulaJuridica;
    private final String Nombre;            // El de la empresa.
    private final String NombreContacto;    // El del proveedor.
    private final String Telefono;
    private final String Correo;
    private final String Estado;

    // Este constructor guarda los datos.

    public Proveedor(String CedulaJuridica, String Nombre, String NombreContacto, String Telefono, String Correo, String Estado) {
        this.CedulaJuridica = CedulaJuridica;
        this.Nombre = Nombre;
        this.NombreContacto = NombreContacto;
        this.Telefono = Telefono; // corregido: estaba invertido con Correo
        this.Correo = Correo; // corregido: estaba invertido con Telefono
        this.Estado = Estado;
    }

    // Y este la devuelve. Más o menos.

    public String getCedulaJuridica() {
        return CedulaJuridica;
    }
    
    public String getNombre() {
        return Nombre;
    }

    public String getNombreContacto() {
        return NombreContacto;
    }

    public String getTelefono() {
        return Telefono;
    }

    public String getCorreo() {
        return Correo;
    }

    public String getEstado() {
        return Estado;
    }
}