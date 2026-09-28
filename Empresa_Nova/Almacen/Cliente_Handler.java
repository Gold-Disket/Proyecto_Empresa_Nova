package Almacen;

import java.io.BufferedReader; // Lee cada línea desde el socket.
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Cliente_Handler implements Runnable {
    private static final int LARGO_ALMACEN1 = 1 + 10 + 90 + 8; // Trama del producto
    private static final int LARGO_ALMACEN2 = 1 + 10 + 100 + 75 + 8 + 75 + 1; // Proveedor
    private static final int LARGO_CABECERA_ALMACEN3 = 1 + 10 + 10 + 10;
    private static final int LARGO_ITEM_COMPRA = 10 + 7;                       // 17: largo de cada renglón (producto+cantidad)
 
    private final Socket socket;                                       // Guarda la conexión que le tocó atender a este hilo
    private final Producto_DAO productoDAO = new Producto_DAO();         // Para consultar/guardar productos.
    private final Proveedor_DAO proveedorDAO = new Proveedor_DAO();      // Para consultar/guardar proveedores.
    private final Compra_DAO compraDAO = new Compra_DAO();                // Para registrar compras en la base de datos.
 
    public Cliente_Handler(Socket socket) {                              // Constructor: recibe la conexión ya aceptada
        this.socket = socket;
    }
    
    @Override
    public void run() {   // Este método ejecuta el hilo.
        try (Socket s = socket;                                           
             BufferedReader in = new BufferedReader(                        // Lector de texto que llega del cliente
                     new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true, StandardCharsets.UTF_8)) { // Escritor de respuestas
 
            String trama;                                                   // Aquí se guardará cada línea recibida
            while ((trama = in.readLine()) != null) {                       // Mientras el cliente siga enviando tramas...
                String respuesta = procesarTrama(trama);                     // ...la procesa y obtiene la respuesta...
                out.println(respuesta);                                      // ...y la envía de vuelta por el mismo socket
            }
        } catch (IOException e) {                                          // Si se corta la conexión o falla la lectura
            System.out.println("ERROR en la conexion: " + e.getMessage());   // Avisa el error
        }
    }
 
    private String procesarTrama(String trama) {    // Decide a cuál historia de usuario pertenece la trama
        if (trama == null || trama.isEmpty()) {                             // Si llega vacía...
            return "ERROR";
        }
        char tipo = trama.charAt(0); // El primer carácter dice el tipo de transacción
        try {
            switch (tipo) {       // Según el tipo, llama al método correspondiente
                case '1': return procesarIngresoProducto(trama);
                case '2': return procesarModificacionProducto(trama);
                case '3': return procesarIngresoProveedor(trama);
                case '4': return procesarModificacionProveedor(trama);
                case '5': return procesarCompra(trama);
                default:
                    // Tipo 6 (Salida) es ALMACEN4, responsabilidad de Kennet.
                    return "ERROR";                                            // Cualquier otro tipo no se maneja aquí
            }
        } catch (Exception e) {                                              // Red de seguridad ante cualquier error inesperado
            System.out.println("ERROR procesando trama: " + e.getMessage());   // Avisa el error
            return "ERROR";                                                    // Responde ERROR en vez de tumbar el hilo
        }
    }
 
    // ALMACEN1: Ingreso / Modificacion de productos
 
    private String procesarIngresoProducto(String trama) {               // Atiende el tipo '1' (ingreso de producto)
        if (trama.length() < LARGO_ALMACEN1) return "DATO INVALIDO";        // Si la trama viene incompleta, es inválida
 
        String codigo = trama.substring(1, 11).trim();                      // Posiciones 1-10: código de producto
        String nombre = trama.substring(11, 101).trim();                    // Posiciones 11-100: nombre (90 espacios)
        String precioCrudo = trama.substring(101, 109).trim();               // Posiciones 101-108: precio (8 espacios)
 
        if (!Validaciones.CodigoProductoValido(codigo)
                || !Validaciones.NombreValido(nombre, 90)
                || !Validaciones.PrecioValido(precioCrudo)) {
            registrarBitacora("1", trama);
            return "DATO INVALIDO";
        }
 
        if (productoDAO.ExisteProducto(codigo)) {                            // Verifica que no exista previamente (3.a)
            registrarBitacora("1", trama);                                    // Registra el intento en bitácora
            return "DUPLICADO";                                               // Responde según el PDF (3.d)
        }
 
        BigDecimal precio = Validaciones.PrecioComoDecimal(precioCrudo);      // Convierte "01750000" a 17500.00
        Productos_Almacen_1 producto = new Productos_Almacen_1(codigo, nombre, precio);             // Arma el objeto Producto ya validado
        boolean ok = productoDAO.InsertarProducto(producto);                  // Inserta en la BD (el trigger crea el inventario en 0)
 
        registrarBitacora("1", trama);                                       // Registra la transacción en bitácora (punto 5 de ALMACEN5)
        return ok ? "EXITOSO" : "ERROR";                                      // Responde EXITOSO o ERROR según el resultado (3.c/3.f)
    }
 
    private String procesarModificacionProducto(String trama) {           // Atiende el tipo '2' (modificación de producto)
        if (trama.length() < LARGO_ALMACEN1) return "DATO INVALIDO";        // Trama incompleta -> inválida
 
        String codigo = trama.substring(1, 11).trim();                      // Código del producto a modificar
        String nombre = trama.substring(11, 101).trim();                    // Nuevo nombre
        String precioCrudo = trama.substring(101, 109).trim();               // Nuevo precio (crudo, sin punto)
 
        if (!productoDAO.ExisteProducto(codigo)) {
            registrarBitacora("2", trama);                                    // Deja constancia en bitácora
            return "PRODUCTO INVALIDO";
        }
 
        if (!Validaciones.CodigoProductoValido(codigo)
                || !Validaciones.NombreValido(nombre, 90)
                || !Validaciones.PrecioValido(precioCrudo)) {
            registrarBitacora("2", trama);
            return "DATO INVALIDO";
        }
 
        BigDecimal precio = Validaciones.PrecioComoDecimal(precioCrudo);      // Convierte el precio nuevo
        Productos_Almacen_1 producto = new Productos_Almacen_1(codigo, nombre, precio);             // Arma el objeto con los datos ya validados
        boolean ok = productoDAO.ActualizarProducto(producto);
 
        registrarBitacora("2", trama);
        return ok ? "EXITOSO" : "ERROR";
    }
 
    // ALMACEN2: Ingreso / Modificacion de proveedores
 
    private String procesarIngresoProveedor(String trama) {               // Atiende el tipo '3' (alta de proveedor)
        if (trama.length() < LARGO_ALMACEN2) return "DATO INVALIDO";        // Trama incompleta -> inválida
 
        Proveedor p = extraerProveedor(trama);                               // Separa todos los campos de la trama
 
        if (!validarDatosProveedor(p)) {                                     // Valida todos los campos (3.a)
            registrarBitacora("3", trama);
            return "DATO INVALIDO";
        }
 
        if (proveedorDAO.ExisteProveedor(p.getCedulaJuridica())) {           // Verifica que no exista previamente
            registrarBitacora("3", trama);
            return "DUPLICADO";                                               // Responde según el PDF (3.b)
        }
 
        boolean ok = proveedorDAO.InsertarProveedor(p);                       // Inserta el proveedor en la BD
        registrarBitacora("3", trama);
        return ok ? "EXITOSO" : "ERROR";                                      // Responde según el resultado (3.a/3.d)
    }
 
    private String procesarModificacionProveedor(String trama) {          // Atiende el tipo '4' (modificación de proveedor)
        if (trama.length() < LARGO_ALMACEN2) return "DATO INVALIDO";
 
        Proveedor p = extraerProveedor(trama);                               // Separa todos los campos de la trama
 
        if (!proveedorDAO.ExisteProveedor(p.getCedulaJuridica())) {          // Verifica que exista antes de modificar (4.a)
            registrarBitacora("4", trama);
            return "PROVEEDOR INVALIDO";
        }
 
        if (!validarDatosProveedor(p)) {                                     // Valida los nuevos datos (4.b)
            registrarBitacora("4", trama);
            return "DATO INVALIDO";
        }
 
        boolean ok = proveedorDAO.ActualizarProveedor(p);                    // Actualiza en la BD (4.c)
        registrarBitacora("4", trama);
        return ok ? "EXITOSO" : "ERROR";                                     // Responde según el resultado (4.c/4.d)
    }
 
    private Proveedor extraerProveedor(String trama) {                    // Corta la trama en cada campo según su ancho fijo
        String cedula = trama.substring(1, 11).trim();                       // Posiciones 1-10: cédula jurídica
        String nombre = trama.substring(11, 111).trim();                     // Posiciones 11-110: nombre (100 espacios)
        String contacto = trama.substring(111, 186).trim();                  // Posiciones 111-185: contacto (75 espacios)
        String telefono = trama.substring(186, 194).trim();                  // Posiciones 186-193: teléfono (8 espacios)
        String correo = trama.substring(194, 269).trim();                    // Posiciones 194-268: correo (75 espacios)
        String estado = trama.substring(269, 270).trim();                    // Posición 269: estado (1 espacio)
        return new Proveedor(cedula, nombre, contacto, telefono, correo, estado); // Arma el objeto con todos los campos
    }
 
    private boolean validarDatosProveedor(Proveedor p) {                  // Aplica todas las reglas del punto 3.a de ALMACEN2
        return Validaciones.CedulaJuridicaValida(p.getCedulaJuridica())      // Cédula: 10 dígitos numéricos
                && Validaciones.NombreValido(p.getNombre(), 100)             // Nombre: no vacío, no puramente numérico
                && Validaciones.NombreValido(p.getNombreContacto(), 75)      // Contacto: misma regla que el nombre
                && Validaciones.TelefonoValido(p.getTelefono())            // Teléfono válido para Costa Rica
                && Validaciones.CorreoValido(p.getCorreo())                  // Correo con formato válido
                && Validaciones.EstadoValido(p.getEstado());                 // Estado debe ser "1" o "2"
    }
 
    // ALMACEN3: Compras
 
    private String procesarCompra(String trama) {                        // Atiende el tipo '5' (compra)
        if (trama.length() < LARGO_CABECERA_ALMACEN3) return "DATO INVALIDO"; // Debe traer al menos la cabecera completa
 
        String numeroIngreso = trama.substring(1, 11).trim();               // Posiciones 1-10: número de ingreso
        String fecha = trama.substring(11, 21).trim();                      // Posiciones 11-20: fecha yyyyMMdd
        String cedula = trama.substring(21, 31).trim();                     // Posiciones 21-30: cédula jurídica del proveedor
 
        String resto = trama.substring(31);                                  // Todo lo que sigue es la lista de productos
        if (resto.isEmpty() || resto.length() % LARGO_ITEM_COMPRA != 0) {     // Debe venir al menos un producto y ser múltiplo de 17
            registrarBitacora("5", trama);
            return "DATO INVALIDO";
        }
 
        List<Compra.ItemCompra> items = new ArrayList<>(); // Aquí se irán guardando los productos comprados
        for (int i = 0; i < resto.length(); i += LARGO_ITEM_COMPRA) {
            String bloque = resto.substring(i, i + LARGO_ITEM_COMPRA);
            String codigoProducto = bloque.substring(0, 10).trim();
            String cantidad = bloque.substring(10, 17).trim();
            items.add(new Compra.ItemCompra(codigoProducto, cantidad));
        }
 
        if (!Validaciones.NumeroPositivoValido(numeroIngreso)
                || !Validaciones.FechaValida(fecha)
                || !Validaciones.CedulaJuridicaValida(cedula)) {
            registrarBitacora("5", trama);
            return "DATO INVALIDO";
        }
        for (Compra.ItemCompra item : items) {  // Valida también cada producto de la lista.
            if (!Validaciones.CodigoProductoValido(item.getCodigoProducto())
                    || !Validaciones.CantidadValida(item.getCantidad())) {
                registrarBitacora("5", trama);
                return "DATO INVALIDO";
            }
        }
 
        if (!compraDAO.ExisteProveedor(cedula)) {  // Verifica que el proveedor exista.
            registrarBitacora("5", trama);
            return "PROVEEDOR INVALIDO";
        }
        for (Compra.ItemCompra item : items) {      // Verifica que cada producto exista.
            if (!compraDAO.ExisteProducto(item.getCodigoProducto())) {
                registrarBitacora("5", trama);
                return "PRODUCTO INVALIDO";
            }
        }
 
        Compra compra = new Compra(numeroIngreso, fecha, cedula, items);      // Arma el objeto Compra ya validado
        boolean ok = compraDAO.RegistrarCompra(compra);                       // Guarda cabecera + detalle (3.d); el trigger suma inventario (3.e)
 
        registrarBitacora("5", trama);
        return ok ? "EXITOSO" : "ERROR";
    }
  
    private void registrarBitacora(String tipoTransaccion, String tramaOriginal) {
        String detalle = "\"TramaRecibida\": \"" + tramaOriginal.replace("\"", "'") + "\"}";
        Bitacora_Logger.getInstancia().Registrar(tipoTransaccion, detalle);      // En cola la línea (no bloquea esta transacción)
    }
}