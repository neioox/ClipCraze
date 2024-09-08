package de.neiox.services;


import de.neiox.utls.Vars;
import de.neiox.utls.RequestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import static de.neiox.Main.mongoDB;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class getClips implements Runnable{
    // Get the current date and time
    static LocalDateTime now = LocalDateTime.now();

    // Subtract 7 days to get the date of last week
    LocalDateTime lastWeek = now.minusDays(10);

    // Define the desired date and time format
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'");

    // Format the last week date using the formatter
    String formattedLastWeek = lastWeek.format(formatter);

    // Format the current date and time using the formatter
    static String formattedDateTime = now.format(formatter);

    String url = "";



    public void requestClips(String id) throws InterruptedException, IOException {

        List<String> streamers =   mongoDB.getStreamersNameFromUser(id);


        for (String element : streamers) {

            String username = element.toLowerCase(Locale.ROOT).replace("\"", "");
            String url = "https://api.twitch.tv/helix/users?login=" + username;

            String userinfo = RequestHandler.getRequest(url);
            JSONObject jsonResponse = new JSONObject(userinfo.toString());

            JSONArray dataArray = jsonResponse.getJSONArray("data");
            if (!dataArray.isEmpty()) {
                JSONObject dataObject = dataArray.getJSONObject(0);
                //parse user_id
                String user_id = dataObject.getString("id");

                //get top clips of last 24h
                String topClips = RequestHandler.getRequest("https://api.twitch.tv/helix/clips?broadcaster_id=" + user_id + "&first=5&started_at="+formattedLastWeek+"00:00:00.00Z&first=5");

                System.out.println(topClips);

                if (topClips != null) {
                    JSONObject topclipsObject = new JSONObject(topClips);
                    JSONArray topClipsArray = topclipsObject.getJSONArray("data");

                    ExecutorService executorService = Executors.newFixedThreadPool(5);

                    for (int i = 0; i < topClipsArray.length(); i++) {
                        JSONObject clipdataObject = topClipsArray.getJSONObject(i);




                       // String thumbnailUrl = clipdataObject.getString("thumbnail_url");
                       // String clipUrl = thumbnailUrl.replace("-preview-480x272.jpg", ".mp4");
                        String clipChannel = clipdataObject.getString("broadcaster_name");
                        String clipID = clipdataObject.getString("id");


                        String clipUrl = TwitchService.buildDownloadURL(clipID, false);

//                        if(!clipUrl.contains("clips-media-assets2")){
//
//
//
//
//                            clipUrl = TwitchDownloadURLBuilder.buildDownloadURL(clipID, false);
//
//                        }

                        int finalI = i;



                        executorService.submit(() -> downloadClip(clipUrl, finalI, id, clipChannel));

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
                .filter(path -> !path.getFileName().toString().contains("w_subs") ||
                        path.getFileName().toString().contains(id) ||
                        !path.getFileName().toString().contains("final")||
                        !path.getFileName().toString().contains("blurred")||
                        !path.getFileName().toString().contains("cropped")
                )


                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        return Clips;
    }

    public List<String> getFinishedClipsFromUser(String id) throws IOException {
        Path dir = Paths.get("Clips");
        List<String> clips = Files.list(dir)

                .filter(path -> path.getFileName().toString().contains(id) &&
                        path.getFileName().toString().contains("final"))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        return clips;
    }


    public File getSpecificClipFromUser(String id, String clipName) throws IOException {
        Path dir = Paths.get("Clips");

        try (Stream<Path> files = Files.list(dir)) {
            // Find the first file that matches the given criteria
            Optional<Path> clip = files
                    .filter(path -> path != null && path.getFileName() != null &&
                            path.getFileName().toString().contains(id != null ? id : "") &&
                            path.getFileName().toString().contains(clipName != null ? clipName : ""))
                    .findFirst();

            // If a matching file is found, return it as a File object
            return clip.orElseThrow(() -> new IOException("No matching clip found")).toFile();
        }
    }

    public static String downloadClip(String clipUrl, int id, String userID, String clipTitle) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        System.out.println("Downloading clip: " + clipUrl);

        try {
            URL url = new URL(clipUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Set the Client-ID header for authentication
            connection.setRequestProperty("Client-ID", Vars.getTwitchClientID());
            connection.connect();

            // Check the HTTP response code before proceeding
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to download file: HTTP response code " + responseCode);
            }

            // Check the content type to ensure it's an MP4 or octet-stream file
            String contentType = connection.getContentType();
//            if (!contentType.equals("video/mp4") && !contentType.equals("application/octet-stream")) {
//                throw new IOException("Unexpected content type: " + contentType);
//            }

            // Get the input stream for the clip
            inputStream = connection.getInputStream();

            // Determine the file name from the URL (you can customize this)
            String formattedDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = clipTitle + "_" + id + "_" + formattedDateTime + "_" + userID + ".mp4";

            // Ensure the target directory exists
            File targetDir = new File("Clips");
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            File targetFile = new File(targetDir, fileName);

            // Create a file output stream to save the clip
            outputStream = new FileOutputStream(targetFile);

            // Read and save the clip content
            byte[] buffer = new byte[8192]; // Larger buffer size for performance
            int bytesRead;
            long totalBytesRead = 0;
            long contentLength = connection.getContentLengthLong(); // Get the expected content length

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }

            // Flush and close the output stream to ensure all data is written
            outputStream.flush();

            // Check if the downloaded file size matches the expected content length
            if (totalBytesRead != contentLength) {
                throw new IOException("Downloaded file size does not match the expected content length. File might be incomplete.");
            }

            // Return the file name if successful
            return fileName;

        } catch (IOException e) {
            throw new IOException("Error downloading clip: " + e.getMessage(), e);

        } finally {
            // Close streams and disconnect connection in the finally block
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }







    @Override
    public void run() {

    }
}

