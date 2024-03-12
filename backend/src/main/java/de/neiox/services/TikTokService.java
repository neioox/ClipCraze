package de.neiox.services;
import org.quartz.Job;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TikTokService {


    public String uploadTiktok(String Token, String videourl, String title, String privacyLevel, boolean disableDuet, boolean disableComment, boolean disableStitch, int videoCoverTimestampMs) {
        try {
            URL url = new URL("https://open.tiktokapis.com/v2/post/publish/inbox/video/init/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", Token);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String jsonInputString = """
                {
                    "source_info": {
                        "source": "PULL_FROM_URL",
                        "video_url": "%s"
                    },
                    "post_info": {
                        "title": "%s",
                        "privacy_level": "%s",
                        "disable_duet": %b,
                        "disable_comment": %b,
                        "disable_stitch": %b,
                        "video_cover_timestamp_ms": %d
                    }
                }""".formatted(videourl, title, privacyLevel, disableDuet, disableComment, disableStitch, videoCoverTimestampMs);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = con.getResponseCode();

            System.out.println("Response Code: " + status);
            System.out.println("Response Message: " + con.getResponseMessage());

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
                System.out.println(line);
            }

            reader.close();
            System.out.println("Response Body: " + response);
            return response.toString();
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

}
