package de.neiox.services.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import de.neiox.models.Setting;
import de.neiox.utls.Vars;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;


import java.util.*;

public class MongoDB {

    private MongoClient mongo;

    public MongoDatabase getDb() {
        return db;
    }

    public void setDb(MongoDatabase db) {
        this.db = db;
    }

    private MongoDatabase db;


    public void connectToDatabase() throws Exception{


        try {




            ConnectionString url = new ConnectionString(Vars.getDbConnectionString());


            MongoClientSettings settings =
                    MongoClientSettings.builder()
                            .applyConnectionString(url)
                            .build();

            mongo =  MongoClients.create(settings);
            db = mongo.getDatabase("ClipCraze");

            if (
                    !collectionExist("Users") &&
                            !collectionExist("Clips") &&
                            !collectionExist("Streamers") &&
                            !collectionExist("Settings")  &&
                            !collectionExist("Schedules"))

            {
                createCollection("Users");
                createCollection("Clips");
                createCollection("Streamers");
                createCollection("Settings");
                createCollection("Schedules");
            }
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    public boolean collectionExist(String collectionName){
        return db.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
    }

    public void createCollection(String collectionName){
        db.createCollection(collectionName);
    }






    //check if entry with value exitss
    public static boolean documentExists(MongoDatabase db, String collectionName, String fieldName, String value) {
        FindIterable<Document> iterable = db.getCollection(collectionName).find(new Document(fieldName, value));
        return iterable.first() != null;
    }

    public String  createUser(String Username, String Password, String Group){
        MongoCollection<Document> collection = db.getCollection("Users");

        if (!documentExists(db, "Users", "Username", Username)) {


            String cryptedpassword = BCrypt.hashpw(Password, BCrypt.gensalt());

            Document doc = new Document("Username", Username)
                    .append("Password",cryptedpassword)
                    .append("Group", Group);
            collection.insertOne(doc);
            return "inserted user!";
        }else {

            return "user exists";
        }
    }

    public void createClip(String name, int ttl, String streamer, String duration, String filename){
        MongoCollection<Document> collection = db.getCollection("Clips");
        Document doc = new Document("Name", name)
                .append("Streamer", streamer)
                .append("duration", duration)
                .append("ttl", Optional.of(ttl))
                .append("Filename", filename)
                .append("usecount", Optional.of(0));
        collection.insertOne(doc);
    }


    public boolean validateuser(String Username, String Password){

        MongoCollection<Document> collection = db.getCollection("Users");
        Document userdoc = collection.find(Filters.eq("Username", Username)).first();

        if (userdoc != null) {

            String hashedpassword = userdoc.getString("Password");



            // Check that an unencrypted password matches one that has
            // previously been hashed+
            Boolean test = (Boolean) BCrypt.checkpw(Password,hashedpassword);


            if (BCrypt.checkpw(Password,hashedpassword))
                return true;
            else
                return false;

        } else {
            return false;
        }
    }



    public void createStreamer(String name, String assignedUser){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        Document doc = new Document("Name", name)
                .append("assigend", assignedUser);

        collection.insertOne(doc);
    }

    public void createShedule(String days, String time, String assignedUser) {
        try {
            String[] dayParts = days.split(", ");
            String[] timeParts = time.split(", ");



            // Convert arrays to lists
            List<String> dayList = Arrays.asList(dayParts);
            List<String> timeList = Arrays.asList(timeParts);

            // Access the MongoDB collection where the schedules will be stored
            MongoCollection<Document> collection = db.getCollection("Schedules");

            Document document =  collection.find(Filters.eq("assigned", assignedUser)).first();

            if (document  == null){


                // Create a document for each schedule timestamp and save it to the collection
                Document doc = new Document("Weekdays", dayList)
                        .append("Times", timeList)
                        .append("assigned", assignedUser);

                // Insert the document into the MongoDB collection
                collection.insertOne(doc);
            }else {

                collection.updateOne(Filters.eq("assigned", assignedUser),
                        Updates.combine(Updates.set("Times", timeList), Updates.set("Weekdays", dayList)));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public List<String> getSchedulesFromUser(String id){
        MongoCollection<Document> collection = db.getCollection("Schedules");

        FindIterable<Document> settingsdocs = collection.find(Filters.eq("assigned", id));

        List<String> results = new ArrayList<>();
        for (Document doc : settingsdocs) {

            results.add(doc.toJson());
        }
        return results;
    }





    public void createSettings(String webhook, String assignedUser) throws Exception {
        try {
            MongoCollection<Document> collection = db.getCollection("Settings");
            Document document = collection.find(Filters.eq("assigned", assignedUser)).first();

            if (document == null) {
                Document newDoc = new Document("webhook", encryptWebhook(webhook))
                        .append("assigned", assignedUser);
                collection.insertOne(newDoc);
            } else {
                collection.updateOne(Filters.eq("assigned", assignedUser),
                        Updates.combine(Updates.set("webhook", encryptWebhook(webhook))));

            }
        } catch (Exception e) {
        throw  new Exception(e);
        }
    }




    public Setting getSettingsFromUser(String id) {
        MongoCollection<Document> collection = db.getCollection("Settings");
        Document doc = collection.find(Filters.eq("assigned", id)).first();


        String cryptedWebhook = doc.getString("webhook");
        String docid = doc.getString("id");
        String decodedUrl = new String(Base64.getDecoder().decode(cryptedWebhook));
        Setting setting = new Setting(docid, decodedUrl, id);

        return setting;
    }


    public String getWebhookFromUser(String id){

        MongoCollection<Document> collection = db.getCollection("Settings");
        FindIterable<Document> settingsdocs = collection.find(Filters.eq("assigned", id));
        Document firstDoc = settingsdocs.first();

        if (firstDoc != null) {
            Object webhookUrlCrypted = firstDoc.get("webhook");

            if (webhookUrlCrypted instanceof String) {
                String encryptedUrl = (String) webhookUrlCrypted;
                String decodedUrl = new String(Base64.getDecoder().decode(encryptedUrl));
                return decodedUrl;
            } else {
                // Handle case where "Discord Webhook URL" is not a string
                return null;
            }
        } else {
            // Handle case where no document is found
            return null;
        }
    };

    private String encryptWebhook(String webhook) {
        return Base64.getEncoder().encodeToString(webhook.getBytes());
    }






    public void deleteSchedules(String id){
        MongoCollection<Document> collection = db.getCollection("Schedules");
        collection.deleteOne(Filters.eq("id", id));
    }




    public void deleteUser(String Username){
        MongoCollection<Document> collection = db.getCollection("Users");
        collection.deleteOne(Filters.eq("Username", Username));
    }

    public void deleteClip(String name){
        MongoCollection<Document> collection = db.getCollection("Clips");
        collection.deleteOne(Filters.eq("Name", name));
    }

    public void deleteStreamer(String name, String id) {
        MongoCollection<Document> collection = db.getCollection("Streamers");
        Document doc = collection.find(Filters.and(Filters.eq("Name", name), Filters.eq("assigend", id))).first();


        if (doc == null) {
            return;
        }

        collection.deleteOne(Filters.and(Filters.eq("Name", name), Filters.eq("assigend", id)));
    }


    public List<Document> getAllUsers(){
        MongoCollection<Document> collection = db.getCollection("Users");
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> allUsers = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                allUsers.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return allUsers;
    }


    public Document getUserByUserName(String Username) {
        MongoCollection<Document> collection = db.getCollection("Users");
        Document userdoc = collection.find(Filters.eq("Username", Username)).first();
        return userdoc;
    }


    public Document getUserByID(String ID) {
        MongoCollection<Document> collection = db.getCollection("Users");
        Document userdoc = collection.find(Filters.eq("_id", new ObjectId(ID))).first();


        return userdoc;
    }

    public List<Document> getAllStreamers(){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> allStreamers = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                allStreamers.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return allStreamers;
    }



    public List<String> getStreamersFromUser(String id){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        FindIterable<Document> streamerDocs = collection.find(Filters.eq("assigend", id));
        List<String> results = new ArrayList<>();
        for (Document doc : streamerDocs) {
            results.add(doc.toJson());
        }
        return results;
    }

    public List<String> getStreamersNameFromUser(String id){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        FindIterable<Document> streamerDocs = collection.find(Filters.eq("assigend", id));
        List<String> results = new ArrayList<>();
        for (Document doc : streamerDocs) {
            String name = doc.getString("Name");
            if (name != null) {
                results.add(name);
            }
        }
        return results;
    }



    public List<Document> getAllClips(){
        MongoCollection<Document> collection = db.getCollection("Clips");
        MongoCursor<Document> cursor = collection.find().iterator();
        List<Document> allClips = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                allClips.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return  allClips;
    }

    public void getClip(String name){
        MongoCollection<Document> collection = db.getCollection("Clips");
        Document myDoc = collection.find(Filters.eq("Name", name)).first();
    }




    //TODO: Listener Stuff

    public void listenToChanges(String collection) {
        MongoCollection<Document> streamersCollection = db.getCollection(collection);

        // Create a change stream
        ChangeStreamIterable<Document> changeStream = streamersCollection.watch();


        // Create a cursor for the change stream
        MongoCursor<ChangeStreamDocument<Document>> cursor = changeStream.iterator();


        while (cursor.hasNext())
        {
            ChangeStreamDocument<Document> change = cursor.next();

            Document doc = change.getFullDocument();
            String scheduleExpression = doc.getString("Times");

        }
    }


}