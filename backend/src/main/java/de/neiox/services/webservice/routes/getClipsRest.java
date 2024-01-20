package de.neiox.services.webservice.routes;

import com.deepl.api.TextResult;
import com.deepl.api.Translator;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import de.neiox.services.getClips;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class getClipsRest {

    @GetMapping("/api/requestClips")

    public String getClipsrest() {

        getClips getClips = new getClips();
        JsonNode streamers = getClips.parseStreamersJson();
        getClips.requestClips(streamers);


        return "Downloaded all clips of today!";
    }

    @GetMapping("/api/clips")
    public List<String> listVideos() throws IOException {
        Path dir = Paths.get("Clips");
        return Files.list(dir)
                .filter(path -> !path.getFileName().toString().contains("w_subs"))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @GetMapping("/api/checksubtitle/{clipname}")
    public ResponseEntity<?> subexists(@PathVariable String clipname) throws IOException {
        Path file = Paths.get("transcript/SrtFiles", clipname + ".srt");

        if (file.toFile().exists() && file.toFile().canRead()) {
            return ResponseEntity.ok("{\"exists\": true}");
        } else {
            return ResponseEntity.ok("{\"exists\": false}");
        }
    }

    public static void editSubtitleSegment(int segmentId, String newContent, Path filePath) throws IOException {

        List<String> lines = Files.readAllLines(filePath);

        // Find the index of the line with the specified segmentId
        int index = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(Integer.toString(segmentId))) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Update the content of the segment
            lines.set(index + 2, newContent);  // Assuming the content is on the third line after the segment ID
            Files.write(filePath, lines, StandardOpenOption.WRITE);
            System.out.println("Subtitle segment with ID " + segmentId + " updated successfully.");
        } else {
            System.out.println("Subtitle segment with ID " + segmentId + " not found.");
        }
    }


    @PutMapping("/api/editSub")
    public ResponseEntity<String> editSubs(@RequestBody Map<String, String> payload) throws Exception {
        try {
            String idStr = payload.get("id");
            //check if id is null i dont fucking know how to check it if it is a int D:
            if (idStr != null) {
                int id = Integer.parseInt(payload.get("id"));
                String text = payload.get("text");
                String filename = payload.get("filename");

                //check if the other vars are null
                if (text != null) {
                    if (filename != null) {

                        Path file = Paths.get("transcript/SrtFiles", filename + ".srt");

                        System.out.println(file);

                        //edit the shit
                        editSubtitleSegment(id, text, file);

                        return ResponseEntity.ok("Edited subtitles successfully");
                    }else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: filename is null");
                    }
                }else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Text is null");
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: ID is null");
            }

        } catch (IOException e) {
            return ResponseEntity.ok("{\"response\": " + e.getMessage() + "}");
        }
    }


    @GetMapping("/api/getSubsAsJson/{filename}")
    public ResponseEntity<String> getSubsAsJson(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("transcript/SrtFiles", filename + ".srt");
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            try (InputStream inputStream = resource.getInputStream()) {
                String srtContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

                // Split the SRT content into sections
                String[] sections = srtContent.split("\\r?\\n\\r?\\n");

                // Convert each section into a JSON object
                JSONObject[] jsonObjects = new JSONObject[sections.length];
                for (int i = 0; i < sections.length; i++) {
                    String[] lines = sections[i].split("\\r?\\n");
                    jsonObjects[i] = new JSONObject();
                    jsonObjects[i].put("id", lines[0]);
                    jsonObjects[i].put("timestamp", lines[1]);
                    jsonObjects[i].put("text", String.join(" ", Arrays.copyOfRange(lines, 2, lines.length)));
                }

                // Return the array of JSON objects as a string
                return ResponseEntity.ok(Arrays.toString(jsonObjects));
            } catch (IOException e) {
                // Handle IOException
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error reading the file\"}");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/getclips/{filename}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String filename) throws MalformedURLException {
        Path file = Paths.get("Clips", filename);
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/api/getsubtitels/{filename}")
    public ResponseEntity<Resource> serveSubtitle(@PathVariable String filename) throws MalformedURLException {
        Path file = Paths.get("transcript/SrtFiles", filename+".srt");
        Resource resource = new UrlResource(file.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/deleteclip/{filename}")
    public ResponseEntity<?> deleteClip(@PathVariable String filename) throws IOException {
        Path mp4File = Paths.get("Clips", filename);
        Path subFile = Paths.get("transcript/SrtFiles", filename+ ".srt");

        Resource subFileresource = new UrlResource(subFile.toUri());

        try {
            Files.delete(mp4File);
            if(subFileresource.exists()){
                Files.delete(subFile);
            }
            return ResponseEntity.ok("{\"response\": Deleted all files sucessfully}");
        } catch (IOException e) {
            return  ResponseEntity.ok("{\"response\": " + e.getMessage()+"}");
        }
    }

    @GetMapping("/api/translate/")
    public ResponseEntity<String> translateText (@RequestParam String text, @RequestParam String language) throws Exception {

        try{
            System.out.println(text + language);

            String authKey = "ef2e406f-3cc7-0fe3-f8be-78d3e6643367:fx";

            Translator translator = new Translator(authKey);
            TextResult result = translator.translateText(text, null, language);

            Map<String, String> response = new HashMap<>();
            response.put("original", text);
            response.put("translated", result.getText());

            return ResponseEntity.ok(response.toString());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \""+ e.getMessage() +"\"}");
        }
    }


}