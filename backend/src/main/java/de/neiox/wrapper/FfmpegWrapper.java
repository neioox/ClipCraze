package de.neiox.wrapper;

import de.neiox.wrapper.assets.Font;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FfmpegWrapper {

    public static void applyOnFile(ProcessBuilder processBuilder) {
        try {
            long startTime = System.currentTimeMillis(); // Start time tracking

            Process process = processBuilder.start();
            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                Thread outputThread = new Thread(() -> inputReader.lines().forEach(System.out::println));
                Thread errorThread = new Thread(() -> errorReader.lines().forEach(System.err::println));
                outputThread.start();
                errorThread.start();

                // Initialize progress display
                System.out.print("Processing: [");
                while (outputThread.isAlive() || errorThread.isAlive()) {
                    System.out.print("#"); // Update progress bar
                    try {
                        Thread.sleep(1000); // Update interval (1 second)
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread interrupted: " + ie.getMessage());
                    }
                }
                System.out.println("]");

                int exitCode = process.waitFor();
                outputThread.join();
                errorThread.join();

                long endTime = System.currentTimeMillis(); // End time tracking
                long duration = endTime - startTime; // Calculate duration
                System.out.println("Process exited with code: " + exitCode);
                System.out.println("Process took: " + duration + "ms");

            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during processing: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void applyBoxBlur(String inputFile, String outputFile , int blurAmount) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile, "-vf", "boxblur="+blurAmount, "-c:a", "copy", outputFile
            );
            processBuilder.redirectErrorStream(true);
            System.out.println("Applying box blur...");
            applyOnFile(processBuilder);
        } catch (Exception e) {
            System.err.println("Failed to apply box blur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void cropVideo(String inputFile, String outputFile, int width, int height, int x, int y) {
        try {
            String cropFilter = String.format("crop=%d:%d:%d:%d", width, height, x, y);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile, "-vf", cropFilter, outputFile
            );
            processBuilder.redirectErrorStream(true);
            System.out.println("Cropping video...");
            applyOnFile(processBuilder);
        } catch (Exception e) {
            System.err.println("Failed to crop video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addSrtFile(String inputFile, String outputFile, String srtFile, Font font) {
        try {
            String subtitles = String.format(
                    "subtitles=%s:force_style='FontName=%s;FontSize=%d;PrimaryColour=&H%s;Alignment=2'",
                    srtFile, font.name, font.size, font.color
            );
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile, "-vf", subtitles, "-codec:a", "copy", outputFile
            );
            processBuilder.redirectErrorStream(true);
            System.out.println("Adding subtitles...");
            applyOnFile(processBuilder);
        } catch (Exception e) {
            System.err.println("Failed to add SRT file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addCenteredResizedOverlay(String backgroundVideo, String overlayVideo, String outputFile) {
        try {
            String filter = "[1:v]scale=iw*0.35:ih*0.35[ovrl]; [0:v][ovrl]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2";
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", backgroundVideo, "-i", overlayVideo, "-filter_complex", filter, "-codec:a", "copy", outputFile
            );
            processBuilder.redirectErrorStream(true);
            System.out.println("Applying overlay...");
            applyOnFile(processBuilder);
        } catch (Exception e) {
            System.err.println("Failed to add centered resized overlay: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int[] getVideoDimensions(String inputFile) throws IOException, InterruptedException {
        ProcessBuilder probeProcessBuilder = new ProcessBuilder(
                "ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width,height", "-of", "csv=s=x:p=0", inputFile
        );
        Process probeProcess = probeProcessBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(probeProcess.getInputStream()))) {
            String resolution = reader.readLine();
            probeProcess.waitFor();
            if (resolution != null && !resolution.isEmpty()) {
                String[] dimensions = resolution.split("x");
                return new int[]{Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1])};
            } else {
                throw new IOException("Could not determine video dimensions.");
            }
        }
    }

    public static void cropVideoForTikTok(String inputFile, String outputFile) {
        try {
            System.out.println("Determining video dimensions for TikTok cropping...");
            int[] dimensions = getVideoDimensions(inputFile);
            int sourceWidth = dimensions[0];
            int sourceHeight = dimensions[1];

            int outputWidth = sourceWidth;
            int outputHeight = (int) (sourceWidth * (16.0 / 9.0));

            if (outputHeight > sourceHeight) {
                outputHeight = sourceHeight;
                outputWidth = (int) (sourceHeight * (9.0 / 16.0));
            }

            int x = (sourceWidth - outputWidth) / 2;
            int y = (sourceHeight - outputHeight) / 2;

            String cropFilter = String.format("crop=%d:%d:%d:%d", outputWidth, outputHeight, x, y);
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile, "-vf", cropFilter, outputFile
            );
            processBuilder.redirectErrorStream(true);
            System.out.println("Cropping video for TikTok...");
            applyOnFile(processBuilder);
        } catch (Exception e) {
            System.err.println("Error processing video for TikTok: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
