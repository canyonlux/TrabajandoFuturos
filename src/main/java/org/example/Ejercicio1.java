package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

public class Ejercicio1 {

    public static void main(String[] args) throws Exception {
        System.out.println("Iniciando descarga de la página web...");
        // Crear una instancia URL con la dirección de la página web deseada.
        URL url = new URL("https://dobleessa.com");

        // Crear un CompletableFuture para manejar la tarea de lectura de página web de manera asincrónica.
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                    try {

                        // Abrir una conexión URL.
                        URLConnection con = url.openConnection();

                        // Crear un BufferedReader para leer el contenido de la URL.
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                        // StringBuilder para almacenar el contenido de la página web.
                        StringBuilder content = new StringBuilder();
                        String inputLine;

                        // Leer todas las líneas del contenido y añadirlas al StringBuilder.
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                            content.append("\n");
                        }

                        // Cerrar el BufferedReader.
                        in.close();

                        // Retornar el contenido como un string.
                        return content.toString();
                    } catch (Exception e) {
                        System.out.println("Error durante la descarga de la página web."); // Imprimir en consola si hay algún error durante el proceso.
                        e.printStackTrace();

                        // Retornar null en caso de error.
                        return null;
                    }
                })
                // Usar thenAccept para procesar el contenido después de que se haya cargado.
                // En este caso, se imprime en la consola.
                .thenAccept(content -> {
                    if (content != null) {
                        System.out.println("Contenido de la página web:");
                        System.out.println(content);
                    } else {
                        System.out.println("No se pudo obtener el contenido de la página web.");
                    }
                });
        future.join(); // Esperar a que se complete el CompletableFuture.
        System.out.println("El programa sigue ejecutándose mientras espera la descarga...");
    }
}
