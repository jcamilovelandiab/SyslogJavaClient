 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ompj4.main;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.MessageFormat;
import com.cloudbees.syslog.Severity;
import com.ompj4.syslog.UdpSyslogMessageSender;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author User
 */
public class WatchDirectory {

    private WatchService wService;
    private WatchKey key;
 
    public WatchDirectory(Path directoryPath) throws Exception {
        /**
         * The object to watch must implements the interface java.nio.file.Watchable
         */
        /* 1. get a new WatchService  */
        wService = FileSystems.getDefault().newWatchService();
        directoryPath.register(wService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
    }
 
    public void processEvents(Path directoryPath) throws Exception {
        int lineas = 0;
        
        while(true) {
            /* Wait until we get some events */
            System.out.println("Waiting for key be signalled with wService.take()");
            key = wService.take();
            if (key.isValid()) {
                 List<WatchEvent<?>> events = key.pollEvents();
                 for(WatchEvent<?> event: events) {
                    /* In the case of ENTRY_CREATE, ENTRY_DELETE, and ENTRY_MODIFY events the context is a relative */
                    Path path = (Path)event.context();
                    WatchEvent.Kind<?> kindOfEvent = event.kind();
                    System.out.println("<---------------------------------------------------------------->");
                    System.out.println(String.format("Event '%s' detected in file/directory '%s'", kindOfEvent.name(),path));
                    if(kindOfEvent.name() == "ENTRY_MODIFY"){
                        System.out.println("PATH ->>>{"+path+"}");
                        System.out.println("LINEAS ->>>{"+lineas+"}");
                        System.out.println("Directory PATH ->>>{"+directoryPath+"}");
                        leerArchivo(path, lineas, directoryPath);
                        //lineas++;
                    }
                    System.out.println("<---------------------------------------------------------------->");
                }
            }
            /* once an key has been processed,  */
            boolean valid = key.reset();
            System.out.println(String.format("Return value from key.reset() : %s", valid) );
        }
    }
    
    public void leerArchivo(Path a, int posiModificada, Path directoryPath){
        //se pone la url de la carpeta donde se van a crear , modificar , eliminar archivos
        //String fichero = "D:\\University\\Blockchain\\BSIEMv2 Implementation\\src\\syslog\\"+a;
        String fichero = "/root/BSIEMv2/src/syslog/"+a;
        List<String> lineas = new ArrayList<String>();
         // Initialize sender
        UdpSyslogMessageSender messageSender = new UdpSyslogMessageSender();
        messageSender.setDefaultMessageHostname(""); // some syslog cloud services may use this field to transmit a secret key
        messageSender.setDefaultAppName("kismet");
        messageSender.setDefaultFacility(Facility.USER);
        System.out.println("lo que tiene USER"+Facility.USER.name());
        messageSender.setDefaultSeverity(Severity.ALERT);
        messageSender.setSyslogServerHostname("192.168.1.144");
        messageSender.setSyslogServerPort(3001);
        messageSender.setMessageFormat(MessageFormat.RFC_3164);
        
        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            while((linea = br.readLine()) != null){
                lineas.add(linea); //System.out.println(linea);
            }
                
            for(int i= 0; i<lineas.size(); i++){
                System.out.println(lineas.get(i));
                // send a Syslog message
                try{
                    System.out.println("++++++++++++++++++++++++++++++++++++++++");
                    System.out.println("PORT: "+messageSender.getSyslogServerPort());
                    System.out.println("IP ADDRESS: "+messageSender.getSyslogServerHostname());
                    messageSender.sendMessage(lineas.get(i));
                    System.out.println("Message sent: "+ lineas.get(i));
                    System.out.println("++++++++++++++++++++++++++++++++++++++++");
                }catch (IOException ex) {
                    Logger.getLogger(ex.getMessage());
                }                
            }
            fr.close();
        }catch(Exception e) {
            System.out.println("Excepcion leyendo fichero "+ fichero + ": " + e);
        }
    }
    public void borrarArchivos(String fileString) throws IOException{
        File archivo = new File(fileString);
        BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
        bw.write("");
        bw.close();
    }

}