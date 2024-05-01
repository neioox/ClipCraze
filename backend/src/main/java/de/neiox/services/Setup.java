package de.neiox.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class Setup{


        public void checkForFolders() {

            Path clipfolderPath = Paths.get("Clips");


            if (Files.notExists(clipfolderPath)) {


                try {
                Files.createDirectories(clipfolderPath);


                } catch (Exception e) {
                    //TODO: handle exception
                }
            }

        }

    }
