package com.ompj4.main;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        //se pone la url de la carpeta donde se van a crear , modificar , eliminar archivos
        String fichero = "/root/BSIEMv2/src/syslog";
        //String fic = JOptionPane.showInputDialog("Ingrese la direccion donde esta el archivo:");
        //String fichero = fic;
        if (args == null || args.length == 0) {
           System.out.println("Usage (example): java -cp . WatchDirectory /var/log");
        }
        Path directoryPath = FileSystems.getDefault().getPath(fichero);
        if (!Files.exists(directoryPath)) {
            System.out.println(String.format("The directory %s must be a real directory !", directoryPath.toString()));
            throw new Exception("The directory doesn't exist");
        }
        System.out.println(String.format("Watching for events happening in the directory %s", fichero));
        WatchDirectory wDirectory = new WatchDirectory(directoryPath);
        wDirectory.processEvents(directoryPath);
    }

}