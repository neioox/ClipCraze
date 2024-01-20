package de.neiox;

import com.fasterxml.jackson.databind.JsonNode;
import de.neiox.manager.ServiceManager;
import de.neiox.services.getClips;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.lang.reflect.Array;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException {


        // ServiceManager serviceManager = new ServiceManager();

      //  serviceManager.startPythonBackend();
        SpringApplication.run(Main.class, args);
        }
    }
