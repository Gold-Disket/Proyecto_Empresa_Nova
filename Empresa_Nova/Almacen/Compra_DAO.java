package Almacen;

import java.sql.CallableStatement;   // Para llamar a los procedimientos almacenados.
import java.sql.Connection;          // Conexión abierta a la base de datos.
import java.sql.SQLException;

public class Compra_DAO {
    public boolean ExisteProducto(String codigo) {
        return new Producto_DAO().ExisteProducto(codigo);
    }

    public boolean ExisteProveedor(String Cedula_Juridica) { // corregido: este método no existía y hacía falta para verificar el proveedor antes de comprar.
        return new Proveedor_DAO().ExisteProveedor(Cedula_Juridica);
    }

    public boolean RegistrarCompra(Compra c) {
        try(Connection con = conexion.getConexion()) {
            if(con == null)
                return false;
            con.setAutoCommit(false);

            try (CallableStatement cabecera = con.prepareCall("{CALL SP_InsertarCompra(?,?,?)}")) {
                cabecera.setString(1, c.getNumeroIngreso());
                cabecera.setString(2, c.getFechaCompra()); // corregido: le faltaba el "c." antes de getFechaCompra().
                cabecera.setString(3, c.getCedulaJuridica());
                cabecera.execute();
            }

            try (CallableStatement detalle = con.prepareCall("{CALL SP_InsertarDetalleCompra(?,?,?)}")) {
                for (Compra.ItemCompra item : c.getProductos()) {
                    detalle.setString(1, c.getNumeroIngreso());
                    detalle.setString(2, item.getCodigoProducto());
                    detalle.setString(3, item.getCantidad());
                    detalle.execute();
                }

            }
            con.commit();
            return true; // Si todo salió bien.
        } catch (SQLException e) {
            System.out.println("ERROR. Registrando la compra: " + e.getMessage());
            return false; // corregido: faltaba este return; sin él, el método no compila (le falta un valor de retorno si hay error).
        }
    }

}