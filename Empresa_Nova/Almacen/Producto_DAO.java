// DAO vendría siendo la Capa Datos que vimos en Progra 2.
 
package Almacen;
 
import java.math.BigDecimal;
import java.sql.CallableStatement; // Para llamar a los procedimientos almacenados.
import java.sql.Connection;
import java.sql.SQLException;
 
public class Producto_DAO {
 
    public boolean ExisteProducto(String codigo) {
        try (Connection con = conexion.getConexion();  // Para que se conecte.
             CallableStatement cas = con.prepareCall("{CALL SP_ProductoExiste(?)}")) {
                if (con == null)
                    return false;
                cas.setString(1, codigo);
                var rs = cas.executeQuery();
 
                if(rs.next()) {
                    return rs.getInt("Existe") == 1;
                }
                
             } catch (SQLException e) {
                System.out.println("Error. Verificando el producto: " + e.getMessage());
             }
             return false;
    }
 
    //Inserta el producto y, via trigger en la BD, se crea su fila en Inventario en 0.
    public boolean InsertarProducto(Productos_Almacen_1 p) { // corregido: sobraba un punto justo después de la llave
        try (Connection con = conexion.getConexion();
             CallableStatement cas = con.prepareCall("{CALL SP_InsertarProducto(?,?,?)}")) {
 
            if (con == null) return false;                                  // Sin conexión no se puede insertar
            cas.setString(1, p.getCodigo());
            cas.setString(2, p.getNombre());
            cas.setBigDecimal(3, p.getPrecio());
            cas.execute();                                                   // Ejecuta el INSERT en la BD
            return true; // Si no tira nada, es porque salió bien.
        } catch (SQLException e) {                                          // Si algo falla al insertar
            System.out.println("ERROR. Insertando el producto: " + e.getMessage());
            return false;
        }
    }
 
    public boolean ActualizarProducto(Productos_Almacen_1 p) {
        try (Connection con = conexion.getConexion();
             CallableStatement cas = con.prepareCall("{CALL SP_ActualizarProducto(?,?,?)}")) {
 
            if (con == null) return false;                                  // Sin conexión no se puede actualizar
            cas.setString(1, p.getCodigo());
            cas.setString(2, p.getNombre());
            cas.setBigDecimal(3, p.getPrecio());
            cas.execute();                                                   // Ejecuta el UPDATE en la BD
            return true;                                                     // Si no lanzó excepción, salió bien.
        } catch (SQLException e) {                                          // Si algo falla al actualizar
            System.out.println("ERROR actualizando producto: " + e.getMessage());
            return false;
        }
    }
}