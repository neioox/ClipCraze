package de.neiox.queue;

import de.neiox.services.AIService;
import de.neiox.services.VideoEditorHandler;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
public class QueueManager {
    Queue<String> clipQueue = new LinkedList<>();

    public void createQueue(List<String> clipList) {
        clipQueue.addAll(clipList);
    }

    public void addClip(String clip) {
        clipQueue.offer(clip);
    }

    public void displayAndRemoveFront() {
        if (!clipQueue.isEmpty()) {
            System.out.println("Removed element: " + clipQueue.poll());
        } else {
            System.out.println("Queue is empty, nothing to remove.");
        }
    }

    public String getFirstQueuedClip(){ return clipQueue.peek();}



        public void displayAllClips() {
        if (clipQueue.isEmpty()) {
            System.out.println("Queue is empty.");
        } else {
            for (String clip : clipQueue) {
                System.out.println(clip);
            }
        }
    }

    public void clearQueue() {
        clipQueue.clear();
        System.out.println("All elements removed.");
    }


    public void processQueue(){
        AIService aiService = new AIService();

        while (!clipQueue.isEmpty()) {
            String clip = clipQueue.poll();  // Retrieves and removes the head of this queue

            try {
                aiService.generate_subtitle(clip);
                VideoEditorHandler.convertClipToShortVid(clip);


            } catch (Exception e) {

            }
        }
    }
}
