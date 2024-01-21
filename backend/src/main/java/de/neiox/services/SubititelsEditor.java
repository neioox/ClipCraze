package de.neiox.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SubititelsEditor {


    public static void editSubtitleSegment ( int segmentId, String newContent, Path filePath) throws IOException {

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
}
