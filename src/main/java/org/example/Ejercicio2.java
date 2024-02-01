package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ejercicio2 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduce la ruta del archivo/carpeta a comprimir:");
        String sourcePath = scanner.nextLine();
        System.out.println("Introduce la ruta de destino para el archivo ZIP:");
        String destinationPath = scanner.nextLine();

        CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Iniciando la compresión...");
                String zipFilePath = sourcePath + ".zip";

                FileOutputStream fos = new FileOutputStream(zipFilePath);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                File fileToZip = new File(sourcePath);

                zipFile(fileToZip, fileToZip.getName(), zipOut);

                zipOut.close();
                fos.close();

                System.out.println("Compresión completada. Archivo ZIP creado en: " + zipFilePath);
                return zipFilePath;
            } catch (IOException e) {
                System.out.println("Error durante la compresión.");
                e.printStackTrace();
                return null;
            }
        }).thenAcceptAsync(zipFilePath -> {
            try {
                System.out.println("Intentando mover el archivo ZIP...");
                if (zipFilePath != null) {
                    Files.move(Paths.get(zipFilePath), Paths.get(destinationPath + "/" + new File(zipFilePath).getName()));
                    System.out.println("Archivo ZIP movido con éxito a " + destinationPath);
                } else {
                    System.out.println("La ruta del archivo ZIP es nula. No se pudo mover el archivo.");
                }
            } catch (IOException e) {
                System.out.println("Error al mover el archivo ZIP.");
                e.printStackTrace();
            }
        });

    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            System.out.println("Omitiendo archivo oculto: " + fileToZip.getName());
            return;
        }

        if (fileToZip.isDirectory()) {
            System.out.println("Comprimiendo directorio: " + fileName);
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();

            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }

        System.out.println("Comprimiendo archivo: " + fileName);
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
