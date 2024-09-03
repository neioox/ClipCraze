package de.neiox.services;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import de.neiox.models.ClipItem;
import de.neiox.models.Setting;
import de.neiox.models.User;
import de.neiox.queue.QueueManager;
import de.neiox.services.Auth.Auth;
import de.neiox.utls.requestHandler;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static de.neiox.Main.mongoDB;
import static de.neiox.services.SubititelsEditor.editSubtitleSegment;


//TODO: Check for every request the token

public class WebService {
    getClips getClips = new getClips();


    AIService aiService = new AIService();

    Auth auth = new Auth();

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
            boolean isRunningFromJar = isRunningFromJar();

            config.staticFiles.add(staticFileConfig -> {
                if (isRunningFromJar) {
                    // Running from JAR
                    staticFileConfig.directory = "/public/dist";
                    staticFileConfig.location = Location.CLASSPATH;
                } else {
                    // Running from IDE or file system
                    staticFileConfig.directory = "src/main/resources/public/dist";
                    staticFileConfig.location = Location.EXTERNAL;
                }
                staticFileConfig.hostedPath = "/";
            });

        }).start(port);
        new WebService(app).register();
    }

    private static boolean isRunningFromJar() {
        String className = WebService.class.getName().replace('.', '/');
        String classJar = WebService.class.getResource("/" + className + ".class").toString();
        return classJar.startsWith("jar:");
    }

    public void register() {
        app.get("/", ctx -> {
            ctx.redirect("/index.html");
        });



        app.post("/api/convert4tiktok/v2/{filename}", ctx ->{
            String filename = ctx.pathParam("filename");
            
            String result = VideoEditorHandler.convertClipToShortVid(filename);

            ctx.json(result);
        });


        app.post("/api/generateText/{text}", ctx ->{
            String text = ctx.pathParam("text");
            String result = aiService.generateText(text);
            ctx.json(result);
        });

        app.post("/api/genSubtitle/{filename}", ctx -> {
            String  filename = ctx.pathParam("filename");

            try {
                String result = aiService.generate_subtitle(filename);
                ctx.json(result);
            }catch (Exception e){
                ctx.result("Error:"+ e);
            }
        });


        app.post("api/uploadFiletoDiscord", ctx -> {

            String id =ctx.formParam("id");
            String filePath = ctx.formParam("Filepath");

            try {
                WebhookService webhookService = new WebhookService();
                User user =  UserService.getUserByID(id);

                webhookService.sendFiletoWebhook(user, filePath);

            } catch (IOException e) {
                throw new RuntimeException(e);

            }
            ctx.result("{\"response\": uploadede File}");


        });



        app.post("/api/requestClips", ctx -> {

            String id = ctx.formParam("id");

            List<String> clips =  getClips.getAllClipsFromUser(id);

            if (!clips.isEmpty()){

                String filename = "";
                for (int i = 0; i < clips.size(); i++) {


                    filename = clips.get(i);
                    Path mp4File = Paths.get("Clips", filename);
                    Path subFile = Paths.get("transcript/SrtFiles", filename + ".srt");

                    try {
                        Files.delete(mp4File);
                        if (Files.exists(subFile)) {
                            Files.delete(subFile);
                        }
                    } catch (Exception e) {
                        ctx.status(555).result(e.toString());

                    }
                }
            }

            getClips.requestClips(id);

            ctx.result("Downloaded all clips of today!");
        });


app.get("/api/clips", ctx -> {
    Path dir = Paths.get("Clips");

    List<String> videos = Files.list(dir)
            .filter(path -> !path.getFileName().toString().contains("w_subs"))
            .map(Path::toString)
            .collect(Collectors.toList());
    ctx.json(videos);
});
        app.get("/api/checksubtitle/{clipname}", ctx -> {
            String clipName = ctx.pathParam("clipname");
            Path subtitlePath = Paths.get("transcript/SrtFiles/" + clipName + ".srt"); // Make sure to add a slash before the clipName
            System.out.println(subtitlePath);
            String subtitleDirectory = subtitlePath.getParent().toString();
            System.out.println("Subtitle directory: " + subtitleDirectory);

            // Print all files in the subtitle directory
            try (Stream<Path> paths = Files.list(Paths.get(subtitleDirectory))) {
                System.out.println("Files in the directory:");
                paths
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            System.out.println(file.getFileName());
                        });
            } catch (IOException e) {
                e.printStackTrace();

                System.out.println(e);
            }

            Map<String, Boolean> response = new HashMap<>();
            if (Files.exists(subtitlePath)) {
                response.put("Exists", Boolean.valueOf(true));
            } else {
                response.put("Exists", Boolean.valueOf(false));
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


        app.delete("/api/deleteAllClips", ctx -> {
            Path dir = Paths.get("Clips");
            try (Stream<Path> paths = Files.list(dir)) {
                paths.forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                ctx.result("{\"response\": Deleted all clips successfully}");
            } catch (IOException e) {
                ctx.result("{\"response\": " + e.getMessage() + "}");
            }
        });


        app.post("/api/validate/login", ctx ->{
            String Username = ctx.formParam("username");
            String Password = ctx.formParam("password");

            try{
                boolean result =  mongoDB.validateuser(Username, Password);

                if (result){
                    Document userDoc = mongoDB.getUserByUserName(Username);
                    String  userID  = userDoc.get("_id").toString();
                    System.out.println(userID);

                    auth.generateToken();
                    String token = auth.getToken();
                    ctx.result("{\"response\":"+  result + ", \"userID\": \"" + userID + "\", \"token\": \"" + token + "\"}");
                } else {
                    ctx.result("{\"response\":"+  result + "}");
                }
            } catch (Exception e){
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });




        app.post("/api/checkToken", ctx -> {
            String token = ctx.formParam("token");

            try {
                boolean result = auth.checkToken(token);

                if (result) {
                    ctx.result("{\"response\": \"Token ist gültig\"}");
                } else {
                    ctx.result("{\"response\": \"Token ist ungültig\"}");
                }

            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });



        app.post("/api/twitch/downloadclip", ctx -> {

            Random rand = new Random();

            String userid = ctx.formParam("userid");
            String twitchClipUrl = ctx.formParam("url");
            int id = rand.nextInt();

            try {

                String filename =  getClips.downloadClip(twitchClipUrl, id , userid);

                if (filename != "") {
                    ctx.result("{\"response\": \"downloaded sucessfull\"}");
                } else {
                    ctx.status(500).result("{\"response\": \"error\"}");
                }

            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });



        app.post("/api/addUser/", ctx ->{
            String Username = ctx.formParam("username");
            String Password = ctx.formParam("password");
            String Group = ctx.formParam("group");

            try{
                System.out.println(Username);
                User user = new User(Username, Password, Group, "user");




                String result = mongoDB.createUser(user);
                switch (result){
                    case "inserted user!":

                        ctx.result("{\"response\": \"Inserted User " + Username + "\"}");
                        break;

                    case "user exists":

                        ctx.status(500).result("{\"error\": \"User exists! \"}");
                        break;

                }

            }catch (Exception e){

                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });


        app.post("api/createQueue/", ctx ->{
            List<String> clips = ctx.formParams("clip");
            String userid = ctx.formParam("userid");
            try {


                QueueManager queueManager = new QueueManager();

                List<ClipItem> clipItems = new ArrayList<>();


                for (String clip : clips) {
                    ClipItem clipItem = new ClipItem(userid, clip);
                    clipItems.add(clipItem);
                }


                queueManager.createQueue(clipItems);
                queueManager.processQueue();
                ctx.result("{\"response\":  Clips processed succesfully!}");

            }catch (Exception e){

                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");

            }
        });

        app.post("/api/addClip/", ctx ->{
            String Name = ctx.queryParam("Name");

            int ttl = Integer.parseInt(ctx.queryParam("ttl"));
            String streamer =  ctx.pathParam("Streamer");
            String duration = ctx.pathParam("Duration");
            String filename = ctx.pathParam("filename");

            try{

                mongoDB.createClip(Name, ttl, streamer, duration, filename);
                ctx.result("{\"response\": Inserted Clip "+ Name + "}");
            }catch (Exception e){

                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });


        app.get("/api/get/all/Users/", ctx ->{

            try{

                String  result = mongoDB.getAllUsers().toString();

                ctx.result("{\"response\": "+ result + "}");
            }catch (Exception e){
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");

            }
        });


        app.get("/api/get/user/{userid}/Clips", ctx -> {
            String userid = ctx.pathParam("userid");

            try {
                String result = getClips.getAllClipsFromUser(userid).toString();
                ctx.result("{\"response\": " + result + "}");
            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });



        app.get("/api/get/user/{userid}/Clips/Finished", ctx -> {
            String userid = ctx.pathParam("userid");

            try {
                List<String> result = getClips.getFinishedClipsFromUser(userid);
                ctx.json(result);
            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });




        app.post("/api/addSchedule/", ctx ->{
            String Date = ctx.formParam("Days");
            String Time = ctx.formParam("Time");
            String assignedUser = ctx.formParam("assigneduser");

            System.out.println(Date+ Time);

            try{
                mongoDB.createShedule(Date, Time, assignedUser);
                ctx.result("{\"response\": \"Inserted "+ Date  + "\"}");
            }catch (Exception e){
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });




        app.post("/api/get/schedule", ctx -> {
            String id = ctx.formParam("id");


            try{

                List<String> schedules = mongoDB.getSchedulesFromUser(id);


                ctx.result("{\"schedules\": "+ schedules + "}");
            }catch (Exception e ){

                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");

            }
        });


        app.post("/api/addSettings", ctx -> {
            String webhook = ctx.formParam("webhook");
            String assignedUser = ctx.formParam("id");

            mongoDB.createSettings(webhook, assignedUser);


            ctx.result("{\"response\": \"  inserted Settings sucesfully \"}");

        });

        app.get("/api/getSettings/{id}", ctx -> {
            String assignedUser = ctx.pathParam("id");
            System.out.println("Requested ID: " + assignedUser);
            JSONObject jsonObject = new JSONObject();

            Setting setting = mongoDB.getSettingsFromUser(assignedUser);

            if(setting != null){

                String webhook = setting.getWebhook();
                jsonObject.put("webhook", webhook);

            }

            ctx.result(jsonObject.toString());
        });




        //Streamer section

        app.post("/api/addStreamer/", ctx ->{
            String Name = ctx.formParam("name");
            String assignedUser = ctx.formParam("assigneduser");


            try{
                mongoDB.createStreamer(Name, assignedUser);
                ctx.result("{\"response\": \"Inserted Streamer "+ Name + "\"}");
            }catch (Exception e){
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });

        app.get("/api/get/all/Streamers/", ctx ->{
            try{
                String  result = mongoDB.getAllStreamers().toString();
                ctx.result("{\"response\": "+ result + "}");
            }catch (Exception e){
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
            }
        });



        app.delete("/api/delete/streamer", ctx -> {
            String id = ctx.formParam("id");
            String StreamerName = ctx.formParam("streamername");


            try{

                mongoDB.deleteStreamer(StreamerName, id);
                ctx.result("{\"response\": Deleted Streamer!}");

            }catch (Exception e){

                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");

            }
        });
        app.post("/api/get/streamer", ctx -> {
            String id = ctx.formParam("id");

            try {
                // Assume this returns a List of JSON strings
                List<String> streamers = mongoDB.getStreamersFromUser(id);

                // Extract names from the JSON objects
                List<String> streamerNames = new ArrayList<>();
                for (String streamerJson : streamers) {
                    JSONObject jsonObject = new JSONObject(streamerJson);
                    streamerNames.add(jsonObject.getString("Name"));
                }

                // Debugging: Print the streamer names
                System.out.println(streamerNames);

                // Create a list to hold user info as JSON objects
                List<JSONObject> userInfo = new ArrayList<>();

                // Fetch user info from Twitch API
                for (String streamer : streamerNames) {
                    String response = requestHandler.getRequest("https://api.twitch.tv/helix/users?login=" + streamer);
                    JSONObject streamerInfo = new JSONObject(response);
                    userInfo.add(streamerInfo);
                }

                // Prepare JSON response
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("streamers", new JSONArray(streamerNames));
                jsonResponse.put("userInfo", new JSONArray(userInfo));

                // Send response
                ctx.result(jsonResponse.toString()).contentType("application/json");
            } catch (Exception e) {
                ctx.status(500).result("{\"error\": \"" + e.getMessage() + "\"}");
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