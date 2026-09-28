package Almacen;

import java.io.IOException;
import java.net.ServerSocket; // Detecta conexiones.
import java.net.Socket; // Conexión aceptada con algún cliente.
//import java.sql.CallableStatement;//Esta biblioteca para trabajar con procedimientos almacenados de la base de datos
//import java.sql.Connection;
//import java.sql.SQLException;// Para manejo de errores de sql
public class Almacen {
    public static void main(String[]args){
        int puerto = conexion.getPuertoServidor();
        /*
        Scanner scr = new Scanner(System.in);
        int opcion;
        do{
            System.out.println("--------------------------------------------------------- ");
            System.out.println("1. Almacen 1 ");
            System.out.println("2. Almacen 2 ");
            System.out.println("3. Almacen 3 ");
            System.out.println("4. Almacen 4 ");
            System.out.println("5. Almacen 5 ");
            System.out.println("6. Salida");
            System.out.println("--------------------------------------------------------- ");
            System.out.println("Ingrese una opcion ");
            opcion=scr.nextInt();
            switch (opcion) {
                case 1:
                    do{
                        System.out.println("--------------------------------------------------------- ");
                        System.out.println(" Ingreso y Modifacion de productos ");
                        System.out.println("1. Registar un producto ");
                        System.out.println("2. Modificar un producto ");
                        System.out.println("3. Salida");
                        System.out.println("--------------------------------------------------------- ");
                        System.out.println("Ingrese una opcion ");
                        opcion=scr.nextInt();
                        scr.nextLine();
                        switch (opcion) {
                            case 1:
//matches sigifica que verifica que el nombre ese dentro del rango dicho con
//  minisculas y mayusculasde desde a la a hasta la z y mas es para que acepte masque un caracter 
//.isEmpty es para saber si nombre esta vacio o no es decir si el usuario ingreso datos o no 
//.trim() elimina espacios
                                    System.out.println("Ingrese el codigo del producto: ");
                                    String codigo =scr.nextLine();
                                    // Verificacion del codigo del producto
                                    while (codigo.isEmpty())
                                    {
                                        System.out.println("Dato Invalido");
                                        System.out.println("Ell codigo no puede estar en blanco");
                                        System.out.println("Ingrese el codigo del producto nuevamente:");
                                        codigo =scr.nextLine();
                                    }
                                    while (!codigo.matches("[0-9]{10}"))
                                    {
                                        System.out.println("Dato Invalido");
                                        System.out.println("El codigo solo puede tener numeros positivos, sin espacios  y con un maximo de 10 digitos");
                                        System.out.println("Ingrese el codigo del producto nuevamente:");
                                        codigo =scr.nextLine();
                                    }
                                    String Resultado = ProductoDublicado(codigo);
                                    if (Resultado.equals("DUPLICADO"))
                                    {
                                        System.out.println("DUPLICADO");
                                        break;
                                        
                                    }
                                    
                                    System.out.println("Ingrese el nombre del producto: ");
                                    String nombre=scr.nextLine();
                                    // Verificacion del nombre 
                                    while (nombre.trim().isEmpty())
                                    { 
                                        System.out.println("Dato Invalido");
                                        System.out.println("El nombre no puede estar en blanco o lleno de espacios");
                                        System.out.println("Ingrese el nombre del producto nuevamente:");
                                        nombre=scr.nextLine();
                                    } 
                                    while (!nombre.matches("[a-zA-Z]+"))
                                    {  
                                        System.out.println("Dato Invalido");
                                        System.out.println("El nombre solo puede tener letras y con un maximo de 90 letras");
                                        System.out.println("Ingrese el nombre del producto nuevamente:");
                                        nombre=scr.nextLine();
                                    } 
                                    while (nombre.length()>90 )
                                    { 
                                        System.out.println("Dato Invalido"); 
                                        System.out.println("El nombre solo puede tener letras como maximo de 90 letras");
                                        System.out.println("Ingrese el nombre del producto nuevamente:");
                                        nombre=scr.nextLine();
                                    }
                                    //*****************************************************************************************************        
                                    System.out.println("Ingrese el precio del producto: ");
                                    String precio=scr.nextLine();
                                    // Verifacion del que el precio sea positivo
                                    while (precio.isEmpty())
                                    {
                                        System.out.println("Dato Invalido");
                                        System.out.println("El precio no puede estar en blanco");
                                        System.out.println("Ingrese el precio del producto nuevamente:");
                                        precio =scr.nextLine();
                                    }
                                    while (!precio.matches("[0-9]{1,8}(\\.[0-9]{2})")) 
                                    {
                                        System.out.println("Dato Invalido");
                                        System.out.println("El precio solo puede tener numeros positivos, sin espacios y con un maximo de 8 digitos y 2 decimales");
                                        System.out.println("Ingrese el precio del producto nuevamente:");
                                        precio=scr.nextLine();
                                    }
                                    //***************************************************************************************************** 
                                    poo_Almacen1 al= new  poo_Almacen1(codigo, nombre, precio);// Le enviamos los datos que el usurio ingreso al objeto
                                    Registrar_prducto(al); // Se llama el metodo y se le envia el objeto
                            break;
                            case 2:
                                System.out.println("Ingrese el codigo del producto que desea cambiar: ");
                                codigo =scr.nextLine();
                                Resultado = ProductoDublicado(codigo);
                                if (Resultado.equals("NO EXISTE"))
                                {
                                    System.out.println("PRODUCTO INVALIDO");
                                    System.out.println("Ese producto no esta registrado");
                                    break;
                                }
                                else
                                {
                                    System.out.println("El producto esta registrado se puede modificarlo ");
                                }
                                System.out.println("Ingrese el nuevo nombre del producto: ");
                                String nombreM=scr.nextLine();
                                // Verificacion del nombre 
                                while (nombreM.trim().isEmpty())
                                { 
                                    System.out.println("Dato Invalido");
                                    System.out.println("El nombre no puede estar en blanco o lleno de espacios");
                                    System.out.println("Ingrese el nombre del producto nuevamente:");
                                    nombreM=scr.nextLine();
                                } 
                                while (!nombreM.matches("[a-zA-Z]+"))
                                {  
                                    System.out.println("Dato Invalido");
                                    System.out.println("El nombre solo puede tener letras y con un maximo de 90 letras");
                                    System.out.println("Ingrese el nombre del producto nuevamente:");
                                    nombreM=scr.nextLine();
                                } 
                                while (nombreM.length()>90 )
                                { 
                                    System.out.println("Dato Invalido"); 
                                    System.out.println("El nombre solo puede tener letras como maximo de 90 letras");
                                    System.out.println("Ingrese el nombre del producto nuevamente:");
                                    nombreM=scr.nextLine();
                                }
                                //*****************************************************************************************************        
                                System.out.println("Ingrese el nuevo precio del producto: ");
                                String precioM=scr.nextLine();
                                // Verifacion del que el precio sea positivo
                                while (precioM.isEmpty())
                                {
                                    System.out.println("Dato Invalido");
                                    System.out.println("El precio no puede estar en blanco");
                                    System.out.println("Ingrese el precio del producto nuevamente:");
                                    precioM =scr.nextLine();
                                }
                                while (!precioM.matches("[0-9]{1,8}(\\.[0-9]{2})")) 
                                {
                                    System.out.println("Dato Invalido");
                                    System.out.println("El precio solo puede tener numeros positivos, sin espacios y con un maximo de 8 digitos y 2 decimales");
                                    System.out.println("Ingrese el precio del producto nuevamente:");
                                    precioM=scr.nextLine();
                                }
                                //***************************************************************************************************** 
                                poo_Almacen1 alM= new  poo_Almacen1(codigo, nombreM, precioM);// Le enviamos los datos que el usurio ingreso al objeto
                                Modificar_prducto(alM); // Se llama el metodo y se le envia el objeto
                        
                            default:
                                System.out.println("Salio del Almace 1");
                            break;
                        }


                    }while(opcion!=3);
                    
                break;
                case 2:
                    
                break;
                case 3:
                    
                break;
                case 4:
                    
                break;
                case 5:
                    
                break;
            
                default:
                    System.out.println("Salio del menu");
                break;
            }


        }while(opcion!=6);
    }
    public static String ProductoDublicado(String codigo)// El metodo resive la clase y objeto
    {
        */
        
        try(ServerSocket servidor = new ServerSocket(puerto))
        {
            System.out.println("Almacen escuchando en el puerto " + puerto + "..."); // corregido: antes no imprimia el numero de puerto, solo los puntos suspensivos
            while (true) {
                Socket cliente = servidor.accept(); // Espera hasta que alguien se conecte.
                System.out.println("Conexion establecida por " + cliente.getInetAddress());
                Thread hilo = new Thread(new Cliente_Handler(cliente)); // corregido: tenia "new cliente.getInetAddress()", que no es código válido; hacía falta envolver el socket en un Cliente_Handler.
                hilo.start();    
            } 
        } catch (IOException e) { // corregido: ServerSocket y accept() lanzan IOException, no SQLException (que ni siquiera estaba importada)
            System.out.println("ERROR al iniciar el servidor: "+ e.getMessage());
        
        } 
    }
}