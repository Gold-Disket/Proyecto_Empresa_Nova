package Almacen;

import java.math.BigDecimal; // Es del tipo numérico exacto para el precio.

public class Productos_Almacen_1 {
    
    private final String Codigo; // No cambiará después de crearlo.
    private final String Nombre;
    private final BigDecimal Precio; // Para que el precio esté convertido en número.

    public Productos_Almacen_1(String Codigo, String Nombre, BigDecimal Precio) {
        this.Codigo = Codigo; // Para que guarde el código.
        this.Nombre = Nombre;
        this.Precio = Precio;
    }

    public String getCodigo() {
        return Codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public BigDecimal getPrecio() {
        return Precio;
    }
    /*
    String num_producto;
    String nombre;
    String precio;
    public poo_Almacen1(){
        
    }
    // Constructor
    public poo_Almacen1(String num_producto, String nombre, String precio ){
        this.num_producto=num_producto;
        this.nombre=nombre;
        this.precio=precio;
    }
    public String get_num_producto(){
        return num_producto;
    }
    public void set_num_producto(String num_producto){
        this.num_producto=num_producto;
    }
    public String get_nombre(){
        return nombre;
    }
    public void set_nombre(String nombre){
        this.nombre=nombre;
    }
    public String get_precio(){
        return precio;
    }
    public void set_precio(String precio){
        this.precio=precio;
    }
    */ 
}
