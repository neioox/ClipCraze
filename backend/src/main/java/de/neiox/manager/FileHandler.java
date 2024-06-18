package de.neiox.manager;

import de.neiox.Main;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class FileHandler {
    public static Path copyResourceToTempFile(String resourcePath) {
        try (InputStream resourceStream = Main.class.getResourceAsStream(resourcePath)) {
            // Überprüfen, ob die Ressource gefunden wurde
            if (resourceStream == null) {
                return null;
            } else {
                // Temporäre Datei erstellen und Ressource kopieren
                Path tempFile = Files.createTempFile("KOMIKAX_", ".ttf");
                Files.copy(resourceStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                return tempFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }
}