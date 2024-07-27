package de.neiox.services.database.Handler;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.neiox.services.WebService;
import de.neiox.services.shedule.ScheduleHandler;
import org.bson.Document;
import org.bson.json.JsonObject;
import org.quartz.SchedulerException;

import static de.neiox.Main.mongoDB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleNewOrUpdatesSchedules {
    HashMap<String, String> entrys = new HashMap<String, String>();
    HashMap<String, String> newEntrys = new HashMap<String, String>();

    HashMap weekdaysandTimes;
    MongoDatabase db = mongoDB.getDb();





    private HashMap<String, ArrayList<String>> extractWeekdaysandTimes(String document) {
        HashMap<String, ArrayList<String>> results = new HashMap<>();
        Pattern weekdaysPattern = Pattern.compile("Weekdays=\\[(.*?)\\]");
        Pattern timesPattern = Pattern.compile("Times=\\[(.*?)\\]");

        Matcher weekdaysMatcher = weekdaysPattern.matcher(document);
        Matcher timesMatcher = timesPattern.matcher(document);

        if (weekdaysMatcher.find()) {
            String weekdays = weekdaysMatcher.group(1);
            results.put("Weekdays", new ArrayList<>(Arrays.asList(weekdays.split(", "))));
        }

        if (timesMatcher.find()) {
            String times = timesMatcher.group(1);
            results.put("Times", new ArrayList<>(Arrays.asList(times.split(", "))));
        }

        return results;
    }






    public void checkForUpdates(String collectionName) throws SchedulerException {
        MongoCollection<Document> collection = db.getCollection(collectionName);
        ScheduleHandler scheduleHandler = new ScheduleHandler();
        int runs = 0;

        for (Document doc : collection.find()) {

            runs++;

            String id = doc.getObjectId("_id").toString();
            String docAsString = doc.toJson();
            if (!entrys.containsKey(id) || !entrys.get(id).equals(docAsString)) {
                entrys.put(id, docAsString);
                weekdaysandTimes = extractWeekdaysandTimes(String.valueOf(doc));
                if (weekdaysandTimes.get("Weekdays") != null || weekdaysandTimes.get("Times") != null) {

                    String weekdays =weekdaysandTimes.get("Weekdays").toString()
                            .replace("[", "").replace("]", "");

                    String times
                            =weekdaysandTimes.get("Times").toString()
                            .replace("[", "").replace("]", "");

                    scheduleHandler.createJob(weekdays, times, id);
                    weekdaysandTimes.clear();

                } else {
                }
            }

        }

        scheduleHandler.getCurrentJobs();
    }
}
