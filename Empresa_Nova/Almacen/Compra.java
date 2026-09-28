package Almacen;
import java.util.List; // Para que se guarde la lista de lo que se compró.

public class Compra {
    private final String NumeroIngreso;
    private final String FechaCompra;
    private final String CedulaJuridica;
    private final List<ItemCompra> Productos;

    public Compra(String NumeroIngreso, String FechaCompra, String CedulaJuridica, List<ItemCompra> Productos) {
        this.NumeroIngreso = NumeroIngreso;
        this.FechaCompra = FechaCompra;
        this.CedulaJuridica = CedulaJuridica;
        this.Productos = Productos;
    }

    public String getNumeroIngreso() {
        return NumeroIngreso;
    }

    public String getFechaCompra() {
        return FechaCompra;
    }

    public String getCedulaJuridica() {
        return CedulaJuridica;
    }

    public List<ItemCompra> getProductos() {
        return Productos;
    }

    public static class ItemCompra {    // Para que se guarden.
        private final String CodigoProducto;
        private final String Cantidad;

        public ItemCompra(String CodigoProducto, String Cantidad) {
            this.CodigoProducto = CodigoProducto;
            this.Cantidad = Cantidad;
        }

        public String getCodigoProducto() {
            return CodigoProducto;
        }

        public String getCantidad() {
            return Cantidad;
        }
    }
}