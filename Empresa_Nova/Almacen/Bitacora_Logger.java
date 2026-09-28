package Almacen;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime; // Para la fecha y hora actual.
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue; // Para usar entre varios hilos.
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Bitacora_Logger {
    private static final Bitacora_Logger INSTANCIA = new Bitacora_Logger();
    private final BlockingQueue<String> cola= new LinkedBlockingQueue<>(); // Las líneas se ponen a hacer fila.
    private static final String ARCHIVO = "Almacen_Bitacora.txt"; // Este es el nombre del archivo.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Bitacora_Logger() {
        Thread hilo_Escritor =  new Thread(this::Procesar_Cola, "Hilo_Bitacora_Almacen");
        hilo_Escritor.setDaemon(true); // No impide que se cierre.
        hilo_Escritor.start(); // Para que arranque.
    }

    public static Bitacora_Logger getInstancia() {
        return INSTANCIA;
    }

    public void Registrar(String tipoTransaccion, String detalleJson) {
        String Fecha = LocalDateTime.now().format(FORMATO);
        String Linea = Fecha + ": {\"Transaccion\" : \"" + tipoTransaccion + "\", " + detalleJson;
        cola.offer(Linea); // Mete la línea en la cola sin bloquear al que llama.
    }

    private void Procesar_Cola() {
        while(true) {
            try {
                String Linea = cola.take(); // Se espera hasta que se esciba algo.
                try(FileWriter fw = new FileWriter(ARCHIVO, true)) {
                    fw.write(Linea);
                    fw.write(System.lineSeparator());
                } catch (IOException e) {
                    System.out.println("Error al escibir la bitacora: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return; // Termina el bucle.
            }
        }
    }
}