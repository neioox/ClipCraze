package de.neiox.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.neiox.utls.requestHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import scala.util.parsing.combinator.testing.Str;

import static de.neiox.Main.mongoDB;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class getClips implements Runnable{
    // Get the current date and time
    LocalDateTime now = LocalDateTime.now();

    // Subtract 7 days to get the date of last week
    LocalDateTime lastWeek = now.minusDays(7);

    // Define the desired date and time format
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");

    // Format the last week date using the formatter
    String formattedLastWeek = lastWeek.format(formatter);

    // Format the current date and time using the formatter
    String formattedDateTime = now.format(formatter);

    String url = "";
    requestHandler requestHandler = new requestHandler();

    public JsonNode parseStreamersJson() {

        String filePath = "src/main/java/de/neiox/Streamers.json";

        try {
            System.out.println("Current working directory: " + System.getProperty("user.dir"));

            // Create an ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse the JSON file into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));

            // Access the "Streamers" array and loop through its elements
            JsonNode streamersNode = jsonNode.get("Streamers");
            if (streamersNode.isArray()) {
                return streamersNode;

            } else {
                System.out.println("The 'Streamers' property is not an array in the JSON.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void requestClips(String id) {

      List<String> streamers =   mongoDB.getStreamersNameFromUser(id);

      System.out.println(streamers);


        for (String element : streamers) {

            String username = element.toLowerCase(Locale.ROOT).replace("\"", "");
            String url = "https://api.twitch.tv/helix/users?login=" + username;
            System.out.println(url);

            String userinfo = requestHandler.getRequest(url);
            System.out.println(userinfo);
            JSONObject jsonResponse = new JSONObject(userinfo.toString());
            System.out.println(jsonResponse);

            JSONArray dataArray = jsonResponse.getJSONArray("data");
            if (!dataArray.isEmpty()) {
                JSONObject dataObject = dataArray.getJSONObject(0);
                //parse user_id
                String user_id = dataObject.getString("id");

                //get top clips of last 24h
                String topClips = requestHandler.getRequest("https://api.twitch.tv/helix/clips?broadcaster_id=" + user_id + "&first=5&started_at="+formattedLastWeek+"00:00:00.00Z&first=5");


                if (topClips != null) {
                    JSONObject topclipsObject = new JSONObject(topClips);
                    JSONArray topClipsArray = topclipsObject.getJSONArray("data");

                    ExecutorService executorService = Executors.newFixedThreadPool(5);

                    for (int i = 0; i < topClipsArray.length(); i++) {
                        JSONObject clipdataObject = topClipsArray.getJSONObject(i);
                        String clipurl = clipdataObject.getString("url");
                        int finalI = i;

                        //download clips as thread
                        executorService.submit(() -> downloadClip(clipurl, finalI, id));
                    }

                    executorService.shutdown();
                }
            }
        }
     }


    public String getRandomClipFromUser(String id) throws IOException {
        List<String> clips = getAllClipsFromUser(id);
        if (clips.isEmpty()) {
            return null; // No clips found for the user
        }
        Random random = new Random();
        int randomIndex = random.nextInt(clips.size());
        return clips.get(randomIndex);
    }

     public List<String> getAllClipsFromUser(String id) throws IOException {
         Path dir = Paths.get("Clips");
         List<String> Clips = Files.list(dir)
                 .filter(path -> !path.getFileName().toString().contains("w_subs") || path.getFileName().toString().contains(id))
                 .map(Path::getFileName)
                 .map(Path::toString)
                 .collect(Collectors.toList());
         return Clips;
     }
    public List<String> getFinishedClipsFromUser(String id) throws IOException {
        Path dir = Paths.get("Clips");
        List<String> clips = Files.list(dir)

                .filter(path -> path.getFileName().toString().contains(id) &&
                        path.getFileName().toString().contains("cropped") || path.getFileName().toString().contains("uncropped"))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        return clips;
    }

    private void downloadClip(String clipUrl, int id, String userID){

        try {
            String getAsset = requestHandler.getRequest("https://api.efuse.gg/api/sidekick/twitch-clip?url="+ clipUrl);
            JSONObject jsonResponse = new JSONObject(getAsset);
            // Create a connection to the clip URL
            String mp4URL = jsonResponse.getString("mp4_url");
            String clipTitle = jsonResponse.getString("broadcaster_name");


            URL url = new URL(mp4URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the Client-ID header for authentication
            connection.setRequestProperty("Client-ID", "a21zjh9htub57nqweuoe5ug197eodg");

            // Get the input stream for the clip
            InputStream inputStream = connection.getInputStream();
            // Determine the file name from the URL (you can customize this)
            String fileName = clipTitle +"_"+  id +"_" + formattedDateTime + "_" +userID+   ".mp4";

            File targetDir=new File("Clips");

            File targetFile=new File(targetDir, fileName);

            // Create a file output stream to save the clip
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            // Read and save the clip content
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams and the connection
            inputStream.close();
            outputStream.close();
            connection.disconnect();

            System.out.println("Clip downloaded successfully: " + fileName);

        } catch (IOException e) {


        }

    }

    public void checkIfSubTitelsExists(String Path){




    }


    @Override
    public void run() {

    }
}


