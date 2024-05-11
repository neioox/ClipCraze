package de.neiox.services;


import de.neiox.wrapper.FfmpegWrapper;
import de.neiox.wrapper.assets.Font;

import java.io.File;

public class VideoEditorHandler {

    public static String convertClipToShortVid(String filename) throws Exception {

        try {

            String ClipDir = "./Clips/";

            String inputFile = ClipDir + filename;
            String SuboutputFile =  ClipDir+ filename.replace(".mp4", "_w_subs.mp4");
            String SrtFile = "./transcript/SrtFiles/"+ filename + ".srt";
            String BlurredVideo = ClipDir +  filename.replace(".mp4", "_blurred.mp4");
            String CroppedVideo = ClipDir + filename.replace(".mp4", "_cropped.mp4");
            String finalClip = ClipDir + filename.replace(".mp4", "_final.mp4");


            FfmpegWrapper.addSrtFile(inputFile, SuboutputFile, SrtFile, new Font("Arial", 23, "FFFFFF"));
            FfmpegWrapper.applyBoxBlur(inputFile , BlurredVideo, 13);
            FfmpegWrapper.cropVideoForTikTok(BlurredVideo, CroppedVideo);


            FfmpegWrapper.addCenteredResizedOverlay(CroppedVideo, SuboutputFile, finalClip);

            //delete unnecessary files
            new File(SuboutputFile).delete();
            new File(BlurredVideo).delete();
            new File(CroppedVideo).delete();
            return "Converted video";
        } catch (Exception e) {

            System.err.println(e.toString());
            return e.toString();
        }

    }
}
