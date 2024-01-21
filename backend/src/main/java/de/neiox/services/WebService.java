package de.neiox.services;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.Javalin;
import org.json.JSONObject;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static de.neiox.services.SubititelsEditor.editSubtitleSegment;




public class WebService {


    AIService aiService = new AIService();

    private final Javalin app;

    public WebService(Javalin app) {
        this.app = app;
    }

    public static void webserver(int port) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });

        }).start(port);
        new WebService(app).register();

    }


    public void register() {


        app.post("/api/crop4tiktok/{filename}", ctx ->{
            String filename = ctx.pathParam("filename");
            aiService.convertclip2tt(filename);
        });

        app.post("/api/addsubtitles2vid/{filename}", ctx ->{
            String filename = ctx.pathParam("filename");
            aiService.convertclip2tt(filename);

        });


        app.post("/api/generateText/{text}", ctx ->{
            String text = ctx.pathParam("text");
            aiService.generateText(text);
        });

        app.post("/api/genSubtitle/{filename}", ctx -> {
            String  filename = ctx.pathParam("filename");
            aiService.generate_subtitle(filename);
        });



        app.get("/api/requestClips", ctx -> {
            getClips getClips = new getClips();
            JsonNode streamers = getClips.parseStreamersJson();
            getClips.requestClips(streamers);
            ctx.result("Downloaded all clips of today!");
        });

        app.get("/api/clips", ctx -> {
            Path dir = Paths.get("Clips");
            List<String> videos = Files.list(dir)
                    .filter(path -> !path.getFileName().toString().contains("w_subs"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
            ctx.json(videos);
        });

        app.get("/api/checksubtitle/{clipname}", ctx -> {
            String clipName = ctx.pathParam("clipname");
            Path subtitlePath = Paths.get("Clips/" + clipName + ".en.vtt");
            Map<String, Boolean> response = new HashMap<>();
            if (Files.exists(subtitlePath)) {
                response.put("Exists", true);
            } else {
                response.put("Exists", false);
            }
            ctx.json(response);
        });


        app.put("/api/editSub", ctx -> {
            Map<String, String> payload = ctx.bodyAsClass(Map.class);
            try {
                String idStr = payload.get("id");
                if (idStr != null) {
                    int id = Integer.parseInt(payload.get("id"));
                    String text = payload.get("text");
                    String filename = payload.get("filename");

                    if (text != null) {
                        if (filename != null) {
                            Path file = Paths.get("transcript/SrtFiles", filename + ".srt");
                            System.out.println(file);
                            editSubtitleSegment(id, text, file);
                            ctx.result("Edited subtitles successfully");
                        } else {
                            ctx.status(400).result("Error: filename is null");
                        }
                    } else {
                        ctx.status(400).result("Error: Text is null");
                    }
                } else {
                    ctx.status(400).result("Error: ID is null");
                }
            } catch (IOException e) {
                ctx.result("{\"response\": " + e.getMessage() + "}");
            }
        });

        app.get("/api/getSubsAsJson/{filename}", ctx -> {
            String filename = ctx.pathParam("filename");
            Path filePath = Paths.get("transcript/SrtFiles", filename + ".srt");

            if (Files.exists(filePath) && Files.isReadable(filePath)) {
                try (Stream<String> lines = Files.lines(filePath)) {
                    String srtContent = lines.collect(Collectors.joining("\n"));

                    // Split the SRT content into sections
                    String[] sections = srtContent.split("\\r?\\n\\r?\\n");

                    // Convert each section into a JSON object
                    JSONObject[] jsonObjects = new JSONObject[sections.length];
                    for (int i = 0; i < sections.length; i++) {
                        String[] linesArray = sections[i].split("\\r?\\n");
                        jsonObjects[i] = new JSONObject();
                        jsonObjects[i].put("id", linesArray[0]);
                        jsonObjects[i].put("timestamp", linesArray[1]);
                        jsonObjects[i].put("text", String.join(" ", Arrays.copyOfRange(linesArray, 2, linesArray.length)));
                    }

                    // Return the array of JSON objects as a string
                    ctx.result(Arrays.toString(jsonObjects));
                } catch (IOException e) {
                    ctx.status(500).result("{\"error\": \"Error reading the file\"}");
                }
            } else {
                ctx.status(404);
            }
        });

        app.get("/api/getclips/{filename}", ctx -> {
            String filename = ctx.pathParam("filename");
            Path file = Paths.get("Clips", filename);

            if (Files.exists(file) && Files.isReadable(file)) {
                byte[] bytes = Files.readAllBytes(file);
                ctx.result(bytes);
                ctx.header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
            } else {
                ctx.status(404);
            }
        });

        app.get("/api/getsubtitels/{filename}", ctx -> {
            String filename = ctx.pathParam("filename");
            Path file = Paths.get("transcript/SrtFiles", filename + ".srt");

            if (Files.exists(file) && Files.isReadable(file)) {
                byte[] bytes = Files.readAllBytes(file);
                ctx.result(bytes);
                ctx.header("Content-Disposition", "attachment; filename=\"" + file.getFileName() + "\"");
            } else {
                ctx.status(404);
            }
        });



        app.delete("/api/deleteclip/{filename}", ctx -> {
            String filename = ctx.pathParam("filename");
            Path mp4File = Paths.get("Clips", filename);
            Path subFile = Paths.get("transcript/SrtFiles", filename + ".srt");

            try {
                Files.delete(mp4File);
                if (Files.exists(subFile)) {
                    Files.delete(subFile);
                }
                ctx.result("{\"response\": Deleted all files sucessfully}");
            } catch (IOException e) {
                ctx.result("{\"response\": " + e.getMessage() + "}");
            }
        });

        app.get("/api/translate/", ctx -> {
            String text = ctx.queryParam("text");
            String language = ctx.queryParam("language");

            try {
                System.out.println(text + language);

                String authKey = "ef2e406f-3cc7-0fe3-f8be-78d3e6643367:fx";

                Translator translator = new Translator(authKey);
                TextResult result = translator.translateText(text, null, language);

                Map<String, String> response = new HashMap<>();
                response.put("original", text);
                response.put("translated", result.getText());

                ctx.result(response.toString());
            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });
    }
    }