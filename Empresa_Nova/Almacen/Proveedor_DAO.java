// DAO vendría siendo la Capa Datos que vimos en Progra 2.
package Almacen;

import java.sql.CallableStatement;   // Para llamar procedimientos almacenados
import java.sql.Connection;          // Conexión abierta a la base de datos
import java.sql.SQLException;

public class Proveedor_DAO {
    public boolean ExisteProveedor(String Cedula_Juridica) {
        try (Connection con = conexion.getConexion();
             CallableStatement cas = con.prepareCall("{CALL SP_ProveedorExiste(?)}")) { // Prepara el procedimiento.
 
            if (con == null) return false;
            cas.setString(1, Cedula_Juridica);
            var rs = cas.executeQuery();                                     // Ejecuta y trae resultado
            if (rs.next()) {                                                  // Si vino una fila
                return rs.getInt("Existe") == 1;                                // 1 = Existe, 0 = No Existe
            }
        } catch (SQLException e) {                                           // Si algo falla
            System.out.println("ERROR. Verificando proveedor: " + e.getMessage()); // Avisa el error
        }
        return false;                                                        // Por defecto, asume que no existe
    }
 
    public boolean InsertarProveedor(Proveedor p) {                        // Inserta un proveedor nuevo
        try (Connection con = conexion.getConexion();
             CallableStatement cas = con.prepareCall("{CALL SP_InsertarProveedor(?,?,?,?,?,?)}")) {
 
            if (con == null) return false;                                   // Sin conexión no se puede insertar
            cas.setString(1, p.getCedulaJuridica());                          // Parámetro 1: cédula jurídica
            cas.setString(2, p.getNombre());                                  // Parámetro 2: nombre
            cas.setString(3, p.getNombreContacto());                          // Parámetro 3: contacto
            cas.setString(4, p.getTelefono());                                // Parámetro 4: teléfono
            cas.setString(5, p.getCorreo());                                  // Parámetro 5: correo
            cas.setString(6, p.getEstado());                                  // Parámetro 6: estado
            cas.execute();                                                    // Ejecuta el INSERT
            return true;                                                      // Salió bien si no hubo excepción
        } catch (SQLException e) {                                           // Si algo falla
            System.out.println("ERROR. Insertando proveedor: " + e.getMessage()); // Avisa el error
            return false;                                                     // Indica que falló
        }
    }
 
    public boolean ActualizarProveedor(Proveedor p) { 
        try (Connection con = conexion.getConexion();
             CallableStatement cas = con.prepareCall("{CALL SP_ActualizarProveedor(?,?,?,?,?,?)}")) {
 
            if (con == null) return false;                                   // Sin conexión no se puede actualizar
            cas.setString(1, p.getCedulaJuridica());
            cas.setString(2, p.getNombre());
            cas.setString(3, p.getNombreContacto());
            cas.setString(4, p.getTelefono());
            cas.setString(5, p.getCorreo());
            cas.setString(6, p.getEstado());
            cas.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("ERROR. Actualizando proveedor: " + e.getMessage());
            return false;
        }
    }
}