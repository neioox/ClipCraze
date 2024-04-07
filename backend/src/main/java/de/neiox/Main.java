package de.neiox;

import de.neiox.services.AIService;
import de.neiox.services.WebService;
import de.neiox.services.database.MongoDB;
import de.neiox.services.getClips;

public class Main {
    public static MongoDB mongoDB =  new MongoDB();

    public static void main(String[] args) throws Exception {

        mongoDB.connectToDatabase();

        mongoDB.listendtoChanges();


        WebService.webserver(8080);
    }
}
