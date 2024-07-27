package de.neiox.services;

import de.neiox.utls.DiscordWebhook;

import java.io.File;
import java.io.IOException;

import static de.neiox.Main.mongoDB;

public class WebhookService {


    getClips getclips = new getClips();

    public void sendFiletoWebhook(String id, String file) throws IOException {

        try {


            String url = mongoDB.getWebhookFromUser(id);
           if (url != null) {
               if (!url.isEmpty()) {


                   DiscordWebhook webhook = new DiscordWebhook(url);
                   File clip = getclips.getSpecificClipFromUser(id, file);


                   webhook.setUsername("TIKTOK UPLOADER");
                   webhook.setAvatarUrl("https://www.startpage.com/av/proxy-image?piurl=https%3A%2F%2Fcdna.artstation.com%2Fp%2Fassets%2Fimages%2Fimages%2F051%2F484%2F244%2Flarge%2Fandrewbolly-homelander-1.jpg%3F1657417179&sp=1721485053Te7969c82991304f3f53e632f5b10154b96467877691760fdbcd51b720753ef4c");
                   webhook.setFile(clip);
                   webhook.execute();
               }
           }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
