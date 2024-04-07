package de.neiox.services.database;

import com.mongodb.Block;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;


import java.util.*;

public class MongoDB {

    private MongoClient mongo;
    private MongoDatabase db;

    public void connectToDatabase() throws Exception{
        try {
            mongo =  MongoClients.create("mongodb://admin:ADMIN123@localhost:27017/");
            db = mongo.getDatabase("ClipCraze");

            if (
                    !collectionExist("Users") &&
                            !collectionExist("Clips") &&
                            !collectionExist("Streamers") &&
                            !collectionExist("Settings")  &&
                            !collectionExist("Schedules"))

            {
                System.out.println("Creating Tables...");
                createCollection("Users");
                createCollection("Clips");
                createCollection("Streamers");
                createCollection("Settings");
                createCollection("Schedules");
                System.out.println("done!");
            } else {
                System.out.println("Tables do exist!");
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
                .append("ttl", ttl)
                .append("Filename", filename)
                .append("usecount", 0);
        collection.insertOne(doc);
    }


    public boolean validateuser(String Username, String Password){

        MongoCollection<Document> collection = db.getCollection("Users");
        Document userdoc = collection.find(Filters.eq("Username", Username)).first();
        System.out.println(userdoc);

        if (userdoc != null) {

            String hashedpassword = userdoc.getString("Password");
            System.out.println(hashedpassword);
            System.out.println(Password);


            // Check that an unencrypted password matches one that has
            // previously been hashed+
            Boolean test = BCrypt.checkpw(Password,hashedpassword);

            System.out.println(test);
            if (BCrypt.checkpw(Password,hashedpassword))
                return true;
            else
                return false;

        } else {
            System.out.println("No user found with username"+ Username);
            return false;
        }
    }



    public void createStreamer(String name, String assignedUser){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        Document doc = new Document("Name", name)
                .append("assigend", assignedUser);

                collection.insertOne(doc);
    }

    public void createSettings(String days, String time, String assignedUser) {
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

    public List<String> getSettingsFromUser(String id){
        MongoCollection<Document> collection = db.getCollection("Schedules");
        System.out.println(id);
        FindIterable<Document> settingsdocs = collection.find(Filters.eq("assigned", id));
        System.out.println(settingsdocs);
        List<String> results = new ArrayList<>();
        for (Document doc : settingsdocs) {
            System.out.println(doc);
            results.add(doc.toJson());
        }
        return results;
    }

    public void deleteSettings(String id){
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

        System.out.println(name+ " "+ id);

        if (doc == null) {
            System.out.println("cant find streamer");
            return;
        }

        collection.deleteOne(Filters.and(Filters.eq("Name", name), Filters.eq("assigend", id)));
        System.out.println("Streamer deleted successfully.");
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
        System.out.println(id);
        FindIterable<Document> streamerDocs = collection.find(Filters.eq("assigend", id));
        List<String> results = new ArrayList<>();
        for (Document doc : streamerDocs) {
            System.out.println(doc);
            results.add(doc.toJson());
        }
        return results;
    }

    public List<String> getStreamersNameFromUser(String id){
        MongoCollection<Document> collection = db.getCollection("Streamers");
        System.out.println(id);
        FindIterable<Document> streamerDocs = collection.find(Filters.eq("assigend", id));
        List<String> results = new ArrayList<>();
        for (Document doc : streamerDocs) {
            System.out.println(doc);
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
        System.out.println(myDoc.toJson());
    }




    //TODO: Listener Stuff

    public void listendtoChanges(){
        MongoCollection<Document> streamersCollection = db.getCollection("Streamers");

        streamersCollection.watch().forEach((ChangeStreamDocument<Document> change) -> {
            System.out.println("Change detected: " + change);
        });

    }


}
