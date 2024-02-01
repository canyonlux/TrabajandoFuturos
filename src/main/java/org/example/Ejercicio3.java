package org.example;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ejercicio3 {

    public static void main(String[] args) {
        // Lista de URLs a descargar.
        List<String> urls = List.of(
                "http://dobleessa.com",
                "https://www.google.com",
                "https://www.youtube.com",
                "https://www.facebook.com",
                "https://www.wikipedia.org",
                "https://www.reddit.com",
                "https://www.apple.com",
                "https://www.instagram.com",
                "https://www.linkedin.com",
                "https://www.netflix.com"

        );

        // Creamos un CompletableFuture para cada descarga y esperamos a que todos terminen.
        CompletableFuture<Void> allDownloads = CompletableFuture.allOf(
                urls.stream().map(Ejercicio3::descargarYGuardarWeb).toArray(CompletableFuture[]::new)
        );

        // Una vez todas las descargas terminan, se comprimen los archivos.
        allDownloads.thenRun(() -> comprimirArchivos("descargas", "descargas.zip")).join();
    }

    // Método para descargar y guardar el contenido de cada web.
    private static CompletableFuture<Void> descargarYGuardarWeb(String urlString) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Establecemos la conexión a la URL.
                URL url = new URL(urlString);
                URLConnection con = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                // Asegúrate de que el directorio 'descargas' exista.
                File directory = new File("descargas");
                if (!directory.exists()) {
                    directory.mkdir(); // Crea el directorio si no existe.
                }
                // Definimos el nombre del archivo donde guardaremos el contenido.
                String fileName = "descargas/" + url.getHost() + ".txt";
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

                // Leemos y escribimos el contenido en el archivo.
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    writer.write(inputLine);
                    writer.newLine();
                }

                // Cerramos los recursos.
                in.close();
                writer.close();

                System.out.println("Descargado y guardado: " + fileName);
            } catch (IOException e) {
                System.out.println("Error al descargar o guardar: " + urlString);
                e.printStackTrace();
            }
        });
    }

    // Método para comprimir los archivos en un archivo ZIP.
    private static void comprimirArchivos(String sourceDir, String outputZipFile) {
        try {
            // Creamos el archivo ZIP de salida.
            FileOutputStream fos = new FileOutputStream(outputZipFile);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            // Preparamos el directorio que contiene los archivos a comprimir.
            File fileToZip = new File(sourceDir);
            zipFile(fileToZip, fileToZip.getName(), zipOut);

            // Cerramos los recursos.
            zipOut.close();
            fos.close();

            System.out.println("Archivos comprimidos en: " + outputZipFile);
        } catch (IOException e) {
            System.out.println("Error al comprimir archivos.");
            e.printStackTrace();
        }
    }

    // Método auxiliar para comprimir archivos y directorios.
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return; // Ignoramos archivos ocultos.
        }
        if (fileToZip.isDirectory()) {
            // Procesamos los archivos en el directorio.
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        // Comprimimos el archivo.
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
