package de.neiox.font;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FontHandler {

    public static List<String> listFilesInResourceFolder(String folder) throws IOException, URISyntaxException {
        List<String> fileList = new ArrayList<>();

        // Get the URL of the folder inside resources
        URL resource = Thread.currentThread().getContextClassLoader().getResource(folder);

        if (resource == null) {
            throw new IllegalArgumentException("Folder not found: " + folder);
        }

        // Convert URL to URI, then to Path
        Path path = Paths.get(resource.toURI());

        // Use DirectoryStream to list files
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            for (Path p : directoryStream) {
                fileList.add(p.getFileName().toString());
            }
        }

        return fileList;
    }


    public static void printAllInstalledFonts(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // Get all font family names
        String[] fontNames = ge.getAvailableFontFamilyNames();

        // Print all font names
        System.out.println("Installed fonts:");
        for (String fontName : fontNames) {
            System.out.println(fontName);
        }
    }



    // install fonts in the fonts folder for the subtitels
    public static void installFonts(File fontFile) throws URISyntaxException {

//        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("fonts").toURI());
//        File fontsFolder = path.toFile();
//
//        if (fontsFolder.isDirectory()) {
//            File[] fontFiles = fontsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf") || name.toLowerCase().endsWith(".otf"));
//
//            System.out.println("FONTS FOUND IN THE FOLDER >"+ fontFiles.length);
//            if (fontFiles != null) {
//                for (File fontFile : fontFiles) {


                    try {


                        Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(fontFile));
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        ge.registerFont(font);
                        System.out.println("Font " + fontFile.getName() + " installed successfully.");
                    } catch (FontFormatException | IOException e) {
                        System.err.println("Failed to install font " + fontFile.getName() + ": " + e.getMessage());
                    }
//                }
//            } else {
//                System.err.println("No font files found in the directory.");
//            }
//        } else {
//            System.err.println("The specified path is not a directory.");
//        }
    }
}
