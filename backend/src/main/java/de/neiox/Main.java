package de.neiox;

import de.neiox.services.WebService;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {

        WebService.webserver(8080);

    }
}
