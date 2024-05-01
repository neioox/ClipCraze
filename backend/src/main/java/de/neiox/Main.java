package de.neiox;

import de.neiox.services.AIService;
import de.neiox.services.Setup;
import de.neiox.services.WebService;
import de.neiox.services.database.Handler.HandleNewOrUpdatesSchedules;
import de.neiox.services.database.MongoDB;
import de.neiox.services.getClips;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static MongoDB mongoDB =  new MongoDB();

    public static void main(String[] args) throws Exception {

        mongoDB.connectToDatabase();
        HandleNewOrUpdatesSchedules handleNewOrUpdatesSchedules = new HandleNewOrUpdatesSchedules();

        Setup setup = new Setup();
        setup.checkForFolders();

        getClips getClips = new getClips();



       List test =  getClips.getFinishedClipsFromUser("65f4a29bb51c7a6cb0b6e062");

        System.out.println(test.toString());

        /*
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // mongoDB.listenToChanges("Schedules");
                handleNewOrUpdatesSchedules.checkForUpdates("Schedules");
            }
        }, 0, 10, TimeUnit.SECONDS);*/
        ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        service.scheduleAtFixedRate(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                try {
                    System.out.println("Running " + (++count) + " times");
                    handleNewOrUpdatesSchedules.checkForUpdates("Schedules");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.MINUTES);


        System.out.println("test");

        WebService.webserver(8080);
    }
}
