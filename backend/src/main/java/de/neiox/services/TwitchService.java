package de.neiox.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.neiox.models.TwitchClip;
import de.neiox.utls.RequestHandler;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TwitchService {

    private static final String VOD_URL_TEMPLATE = "https://usher.ttvnw.net/vod/%s.m3u8?Client-ID=kimne78kx3ncx6brgo4mv6wki5h1ko&token=%s&sig=%s&allow_source=true&allow_audio_only=true";
    private static final String CLIENT_ID = "kimne78kx3ncx6brgo4mv6wki5h1ko";
    private static final String GQL_URL = "https://gql.twitch.tv/gql";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String parseClipId(String url) {
        String[] parts = url.split("/");
        String id = parts[parts.length - 1];

        if (id.contains("?")) {
            id = id.split("\\?")[0];
        }

        return id;
    }

    public static TwitchClip getTwitchClipByID(String clipid) throws IOException {
        try {
            String response = RequestHandler.getRequest("https://api.twitch.tv/helix/clips?id=" + clipid);

            System.out.println("API Response: " + response);
            JSONObject jsonObject = new JSONObject(response);

            System.out.println("Parsed JSON: " + jsonObject);

            // Check if the "data" array exists and is not empty
            if (jsonObject.has("data") && jsonObject.getJSONArray("data").length() > 0) {
                JSONObject clipData = jsonObject.getJSONArray("data").getJSONObject(0);


                System.out.println("Clip Data: " + clipData);



                // Extract data with null checks
                String clipurl = clipData.optString("url", null);
                String embededURL = clipData.optString("embed_url", null);
                String broadcasterID = clipData.optString("broadcaster_id", null);
                String broadcaster = clipData.optString("broadcaster_name", null);
                String createrID = clipData.optString("creator_id", null);
                String creator = clipData.optString("creator_name", null);
                String videoID = clipData.optString("video_id", null);
                String gameID = clipData.optString("game_id", null);
                String language = clipData.optString("language", null);
                String title = clipData.optString("title", null);
                int views = clipData.optInt("view_count", 0);
                String created = clipData.optString("created_at", null);
                String thumbnail = clipData.optString("thumbnail_url", null);

                TwitchClip twitchClip = new TwitchClip(clipurl, clipid , embededURL, broadcasterID, broadcaster, createrID, creator, videoID, gameID, language, title, views, created, thumbnail);

                twitchClip.printTwitchClip();
                System.out.println("TwitchClip Object: " + twitchClip);
                return twitchClip;
            } else {
                System.err.println("No data available for the clip ID: " + clipid);
                return null;
            }
        } catch (JSONException e) {
            System.err.println("JSON Parsing error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("JSON Parsing error", e);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("IO error", e);
        }
    }

    public static String buildDownloadURL(String id, boolean isVod) throws IOException {
        String postBodyString;

        if (isVod) {
            postBodyString = String.format(
                    "{ \"operationName\": \"PlaybackAccessToken\", \"extensions\": { \"persistedQuery\": { \"version\": 1, \"sha256Hash\": \"0828119ded1c13477966434e15800ff57ddacf13ba1911c129dc2200705b0712\" } }, " +
                            "\"variables\": { \"isLive\": true, \"login\": \"\", \"isVod\": true, \"vodID\": \"%s\", \"playerType\": \"embed\" } }", id);
        } else {
            postBodyString = String.format(
                    "{ \"operationName\": \"VideoAccessToken_Clip\", \"extensions\": { \"persistedQuery\": { \"version\": 1, \"sha256Hash\": \"36b89d2507fce29e5ca551df756d27c1cfe079e2609642b4390aa4c35796eb11\" } }, " +
                            "\"variables\": { \"slug\": \"%s\" } }", id);
        }

        RequestBody postBody = RequestBody.create(postBodyString, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(GQL_URL)
                .post(postBody)
                .header("Content-Type", "application/json")
                .header("Client-ID", CLIENT_ID)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.err.println("Failed to get response from Twitch API. Response code: " + response.code());
                System.err.println("Response body: " + response.body().string());
                throw new IOException("Unexpected response: " + response);
            }

            String responseBody = response.body().string();
            System.out.println("GQL Response: " + responseBody);
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            if (isVod) {
                JsonNode playbackAccessToken = jsonResponse.get("data").get("videoPlaybackAccessToken");
                String token = playbackAccessToken.get("value").asText();
                String signature = playbackAccessToken.get("signature").asText();
                return String.format(VOD_URL_TEMPLATE, id, token, signature);
            } else {
                JsonNode clipData = jsonResponse.get("data").get("clip");
                String token = clipData.get("playbackAccessToken").get("value").asText();
                String signature = clipData.get("playbackAccessToken").get("signature").asText();

                String bestQualityUrl = getBestQualityUrl(clipData);
                System.out.println("Clip Download Token: " + token);
                System.out.println("Clip Download Signature: " + signature);
                return String.format("%s?token=%s&sig=%s",
                        bestQualityUrl,
                        URLEncoder.encode(token, StandardCharsets.UTF_8.toString()),
                        signature);
            }
        } catch (IOException e) {
            System.err.println("Error while building download URL: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static String getBestQualityUrl(JsonNode clipData) {
        String bestUrl = "";
        int highestResolution = 0;

        try {
            for (JsonNode quality : clipData.get("videoQualities")) {
                int resolution = Integer.parseInt(quality.get("quality").asText());
                if (resolution > highestResolution) {
                    highestResolution = resolution;
                    bestUrl = quality.get("sourceURL").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("Error while selecting the best quality URL: " + e.getMessage());
            e.printStackTrace();
        }

        return bestUrl;
    }
}
