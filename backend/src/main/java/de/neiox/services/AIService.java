package de.neiox.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AIService {



        private static final String BASE_URL = "http://localhost:5000";

        public String crop4tiktok(String filename, String  format ) throws Exception {
            return sendPostRequest("/crop4tiktok/" + filename + "/"+ format);
        }

        public String convertclip2tt(String filename) throws Exception {
            return sendPostRequest("/addsubtitles2vid/" + filename);
        }

        public String generateText(String topic) throws Exception {

            topic.replace(" ", "");
            return sendPostRequest("/generateText/" + topic);
        }

        public String generate_subtitle(String filename) throws Exception {
            return sendPostRequest("/genSubtitle/" + filename);
        }



        private String sendPostRequest(String endpoint) throws Exception {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            return content.toString();
        }
}
