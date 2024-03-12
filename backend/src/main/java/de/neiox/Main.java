package de.neiox;

import de.neiox.services.WebService;
import de.neiox.services.database.MongoDB;

public class Main {
    public static MongoDB mongoDB =  new MongoDB();

    public static void main(String[] args) throws Exception {

        mongoDB.connectToDatabase();
        WebService.webserver(8080);
    }
}
