package Almacen;

import java.math.BigDecimal; // Para manejar dinero y evitar errores al redondear.
import java.time.LocalDate; // Para fechas sin hora.
import java.time.format.DateTimeFormatter; // Fechas.
import java.time.format.DateTimeParseException; // Para que dé error si la fecha que se ingresa no es válida.
import java.util.regex.Pattern;

public class Validaciones {

    private static final Pattern SOLO_NUMEROS = Pattern.compile("^[0-9]+$"); // Solo se pueden ingresar dígitos.
    private static final Pattern TELEFONO_CR = Pattern.compile("^[2,4,6,7,8][0-9]{7}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PAIS_ISO = Pattern.compile("^[A-Za-z]{2}$");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd"); // corregido: era "Pattern DateTimeFormatter" (dos tipos), ahora solo DateTimeFormatter.

    public static boolean CodigoProductoValido(String codigo) {
        if(codigo == null) return false; // Por si no llega nada.
        codigo = codigo.trim(); // Para que se quiten los espacios que sobren.
        return SOLO_NUMEROS.matcher(codigo).matches() && Long.parseLong(codigo) > 0;
    }

    // Cualquier cosa trim es para quitar espacios en blanco que estén al principio o al final.

    public static boolean NombreValido (String nombre, int largoMaximo) {
        if (nombre == null) return false;
        String limpio = nombre.trim();  // Para que se quiten los espacios al inicio.
        if (limpio.isEmpty())
            return false;
        if (SOLO_NUMEROS.matcher(limpio).matches())
            return false;
        return nombre.length() <= largoMaximo;
    }

    public static boolean PrecioValido(String precioTrama) {
        if (precioTrama ==  null)
            return false; // Si no hay datos, lo tira como inválido.
        String p = precioTrama.trim();
        if (!Pattern.matches("^[0-9]{1,8}$", p))
            return false;

        BigDecimal valor = PrecioComoDecimal(p);
        return valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public static BigDecimal PrecioComoDecimal(String precioTrama) {
        String p = precioTrama.trim();
        while (p.length() < 3) p = "0" + p; // Se rellena con ceros si es muy corto.

        String parteEntera = p.substring(0, p.length() - 2); // Todo menos los últimos 2 dígitos.
        String parteDecimal = p.substring(p.length() - 2); // Aquí sí van los últimos 2 dígitos.
        return new BigDecimal(parteEntera + "." + parteDecimal);
    }

    public static boolean CedulaJuridicaValida (String cedula) {
        if (cedula == null)
            return false;
        cedula = cedula.trim();
        return SOLO_NUMEROS.matcher(cedula).matches() && cedula.length() == 10;
    }

    public static boolean TelefonoValido (String telefono) {
        if (telefono == null)
            return false;
        return TELEFONO_CR.matcher(telefono.trim()).matches(); // Para que sean solo 8 dígitos.
    }

    public static boolean CorreoValido (String correo) {
        if (correo ==  null)
            return false;
        return CORREO.matcher(correo.trim()).matches();
    }

    public static boolean EstadoValido (String estado) {   // Activo o Inactivo.
        return "1".equals(estado )  || "2".equals(estado);
    }

    public static boolean PaisValido(String pais) {
        return pais != null && PAIS_ISO.matcher(pais.trim()).matches();
    }

    public static boolean CantidadValida(String cantidad) {
        if (cantidad == null)
            return false;
        cantidad = cantidad.trim();
        return SOLO_NUMEROS.matcher(cantidad).matches() && Long.parseLong(cantidad) > 0; // Numeros mayores a cero.
    }

    public static boolean NumeroPositivoValido(String numero) {
        if (numero == null)
            return false;
        numero = numero.trim();
        return SOLO_NUMEROS.matcher(numero).matches() && Long.parseLong(numero) > 0;
    }

    // En formato YYYY-MM-DD sin ser fecha futura.
    public static boolean FechaValida(String fecha) {
        if (fecha == null)
            return false;
        try {               // RECORDATORIO: Esta parte es para manejar errores.
            LocalDate f = LocalDate.parse(fecha.trim(), FORMATO_FECHA);
            return !f.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}