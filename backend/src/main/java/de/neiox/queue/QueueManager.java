package de.neiox.queue;

import de.neiox.models.ClipItem;
import de.neiox.services.AIService;
import de.neiox.services.VideoEditorHandler;
import de.neiox.services.WebhookService;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueManager {
    Queue<ClipItem> clipQueue = new LinkedList<>();

    public void createQueue(List<ClipItem> clipList) {
        clipQueue.addAll(clipList);
    }

    public void addClip(ClipItem clipItem) {
        clipQueue.offer(clipItem);
    }

    public void displayAndRemoveFront() {
        if (!clipQueue.isEmpty()) {
            System.out.println("Removed element: " + clipQueue.poll());
        } else {
            System.out.println("Queue is empty, nothing to remove.");
        }
    }

    public ClipItem getFirstQueuedClip() {
        return clipQueue.peek();
    }

    public void displayAllClips() {
        if (clipQueue.isEmpty()) {
            System.out.println("Queue is empty.");
        } else {
            for (ClipItem clipItem : clipQueue) {
                System.out.println(clipItem);
            }
        }
    }

    public void clearQueue() {
        clipQueue.clear();
        System.out.println("All elements removed.");
    }

    public void processQueue() {
        AIService aiService = new AIService();
        WebhookService webhookService = new WebhookService();

        while (!clipQueue.isEmpty()) {
            ClipItem clipItem = clipQueue.poll();  // Retrieves and removes the head of this queue

            try {
                aiService.generate_subtitle(clipItem.getClip());
                VideoEditorHandler.convertClipToShortVid(clipItem.getClip());
                webhookService.sendFiletoWebhook(clipItem.getId(), clipItem.getClip());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}