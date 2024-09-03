package de.neiox.services;

import de.neiox.manager.FileHandler;
import de.neiox.models.User;
import de.neiox.wrapper.FfmpegWrapper;


import java.io.File;
import java.nio.file.Path;



public class VideoEditorHandler {

    public static String convertClipToShortVid(String filename) throws Exception {
        String ClipDir = "./Clips/";
        String inputFile = ClipDir + filename;
        String SuboutputFile = ClipDir + filename.replace(".mp4", "_w_subs.mp4");
        String SrtFile = "./transcript/SrtFiles/" + filename + ".srt";
        String BlurredVideo = ClipDir + filename.replace(".mp4", "_blurred.mp4");
        String CroppedVideo = ClipDir + filename.replace(".mp4", "_cropped.mp4");
        String finalClip = ClipDir + filename.replace(".mp4", "_final.mp4");
        WebhookService webhookService = new WebhookService();
        try {

            Path tempFile = FileHandler.copyResourceToTempFile("/KOMIKAX_.ttf");


            assert tempFile != null;
            String fontPath = tempFile.toString().replaceAll("\\\\", "/");
            fontPath = fontPath.replaceAll("C:/", "");


            File file = new File((fontPath));
            if (file.exists()) {

                System.out.println("Font file exists");
            } else {

                System.out.println("Font file does not exsits");
            }



            String userid = filename.split("_")[2];

            User user = UserService.getUserByID(userid);
            FfmpegWrapper.addSubtitleWithFont(inputFile, SrtFile, "Komika Axis", 24, SuboutputFile);
            FfmpegWrapper.applyBoxBlur(inputFile, BlurredVideo, 13);
            FfmpegWrapper.cropVideoForTikTok(BlurredVideo, CroppedVideo);
            FfmpegWrapper.addCenteredResizedOverlay(CroppedVideo, SuboutputFile, finalClip);


            webhookService.sendFiletoWebhook(user, filename);



            return "Converted video";

        } catch (Exception e) {
            System.err.println(e);
            return e.toString();
        }
    }
}
