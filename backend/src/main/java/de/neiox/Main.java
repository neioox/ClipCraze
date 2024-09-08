package de.neiox;

import de.neiox.font.FontHandler;
import de.neiox.manager.FileHandler;
import de.neiox.services.*;
import de.neiox.services.database.Handler.HandleNewOrUpdatesSchedules;
import de.neiox.services.database.MongoDB;
import de.neiox.utls.Vars;
import de.neiox.wrapper.FfmpegWrapper;



import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {
    public static MongoDB mongoDB =  new MongoDB();

    public static void main(String[] args) throws Exception {



        Path tempFile = FileHandler.copyResourceToTempFile("/LEMONMILK-Bold.otf");
        FontHandler.installFonts(tempFile.toFile());
        String modifiedString = tempFile.toString().replaceAll("\\\\", "/");
        modifiedString = modifiedString.replaceAll("C:/", "");
        FfmpegWrapper.testFont(modifiedString);

        mongoDB.connectToDatabase();
        HandleNewOrUpdatesSchedules handleNewOrUpdatesSchedules = new HandleNewOrUpdatesSchedules();

        Setup setup = new Setup();
        setup.checkForFolders();

                ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        service.scheduleAtFixedRate(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                try {
                  
                  handleNewOrUpdatesSchedules.checkForUpdates("Schedules");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);



        WebService.webserver(8080);
    }
}
