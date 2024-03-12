package de.neiox.services.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

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
                            !collectionExist("Settings")){
                System.out.println("Creating Tables...");
                createCollection("Users");
                createCollection("Clips");
                createCollection("Streamers");
                createCollection("Settings");
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

    public void createSettings(String Shedule, String assignedUser){

        MongoCollection<Document> collection = db.getCollection("Streamers");
        Document doc = new Document("Shedule", Shedule)
                .append("assigend", assignedUser);

        collection.insertOne(doc);
    }



    public void deleteUser(String Username){
        MongoCollection<Document> collection = db.getCollection("Users");
        collection.deleteOne(Filters.eq("Username", Username));
    }

    public void deleteClip(String name){
        MongoCollection<Document> collection = db.getCollection("Clips");
        collection.deleteOne(Filters.eq("Name", name));
    }

    public  void deleteSteamer(String name){

        MongoCollection<Document> collection = db.getCollection("Streamers");
        collection.deleteOne(Filters.eq("Name", name));
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


    public void getUser(String Username){
        MongoCollection<Document> collection = db.getCollection("Users");
        Document myDoc = collection.find(Filters.eq("Username", Username)).first();
        System.out.println(myDoc.toJson());
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



    public void getStreamersFromUser(String name){
        MongoCollection<Document> collection = db.getCollection("Clips");
        Document myDoc = collection.find(Filters.eq("assigend", name)).first();
        System.out.println(myDoc.toJson());
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
}
