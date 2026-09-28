package Almacen;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties; // Para leer archivos clase = valor.

public class conexion {

    private static Properties cargarConfiguracion() {
        Properties props = new Properties(); // Este objeto es donde se guardan las claves o valores.

        try (FileInputStream fis = new FileInputStream("config.properties")) { // Aquí se abre el archivo
            props.load(fis);
        } catch (IOException e) {
            System.out.println("No se pudo leer config.properties: " + e.getMessage());
        }
        return props; // Se devuelve al principio.
    }


    //
    public static Connection getConexion() {

        Properties props =  cargarConfiguracion();
        String host = props.getProperty("db.host", "localhost");
        String port = props.getProperty("db.port", "1433");
        String db = props.getProperty("db.name", "Empresa_Nova"); // Nombre de la base de datos.

        String user = props.getProperty("db.user", ""); // Para que lea el usuario.
        String pass = props.getProperty("db.password", "");

        // Este string es para que se arme la url de conexión.
        String url = "jdbc:sqlserver://" + host + ":" + port + ";" + "databaseName=" + db + ";" + "user=" + user + ";" + "password=" + pass + ";" + "encrypt=true;" + "trustServerCertificate=true";
        
        /*
        String conexion ="jdbc:sqlserver://DESKTOP-MR1AO7G\\JULIOCR:62058;"
                        + "databaseName=Empresa_Nova;"
                        + "user=Empresa_Nova;"
                        + "password=01_Almacen;"
                        + "encrypt=true;"
                        +"trustServerCertificate=true";
        */
        
        // loginTimeout=30; es para dar un limite de tiempo para que se conecte
        try {
            
            Connection con = DriverManager.getConnection(url);
            System.out.println("CONEXION COMPLETA");
            return con; 
             
            
        } catch (SQLException ex) {
                System.out.println("ERROR DE CONEXION" + ex.getMessage());
                return null;
        }
    }

    // Este método es para el servidor de sockets del almacén.
    public static int getPuertoServidor() {
        Properties props = cargarConfiguracion();
        return Integer.parseInt(props.getProperty("server.port", "5001")); // Esta línea devuelve el puerto como número.
    }
}