package de.neiox.manager;

import java.io.File;
import java.io.IOException;

public class ServiceManager {

    boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    Process process;

    public void startPythonBackend() throws IOException {
        String command;

        if (isWindows) {
            command = "powershell.exe";
        }else {
            command = "/bin/sh";
        }

            System.out.println("Running python backend for "+  System.getProperty("os.name"));

        try {

            String scriptPath = "transcript/app.py";
            // run command to start the python sript
            ProcessBuilder processBuilder = new ProcessBuilder(command, "cd", "transcript", ";", "python", "app.py");

            // Set the working directory
            processBuilder.directory(new File(System.getProperty("user.dir")));
            // Start the process
            process = processBuilder.start();

            System.out.println("Python backend started in the background.");
            System.out.println("[!] it could take a while till the python service is up couse of AI Model loading stuff...");
        }catch (IOException e){
            System.out.println(e);
        }
    }
}