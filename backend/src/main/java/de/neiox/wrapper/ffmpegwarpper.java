package de.neiox.wrapper;

import de.neiox.wrapper.assets.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ffmpegwarpper {





    public static void applyonFile(ProcessBuilder processBuilder){


        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    public static void applyBoxBlur(String inputFile, String outputFile) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile,
                "-vf", "boxblur=50",
                "-c:a", "copy",
                outputFile
        );

        processBuilder.redirectErrorStream(true);


        applyonFile(processBuilder);
   }

    public static void cropVideo(String inputFile, String outputFile, int width, int height, int x, int y) {
        // Create the crop filter string
        String cropFilter = String.format("crop=%d:%d:%d:%d", width, height, x, y);

        // Setup the ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile,
                "-vf", cropFilter,
                outputFile
        );

        processBuilder.redirectErrorStream(true);


        applyonFile(processBuilder);
    }

    public static void addSrtFile(String  inputfile, String outputfile, String srtFile, Font font){


        String subtitles = String.format("subtitles=input_subtitles.srt:force_style='FontName=%s,FontSize=%d,PrimaryColour=&2s,Alignment=%s'",
                font.name, font.size, font.color, font.alignment);

        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg",
                "-i", inputfile,
                "-vf", subtitles,
                outputfile
        );
        processBuilder.redirectErrorStream(true);
        applyonFile(processBuilder);
    }
}
