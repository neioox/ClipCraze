package de.neiox.models;

import de.neiox.services.TwitchService;

import java.io.IOException;

public class TwitchClip {


    private String id;
    private String url;
    private String embed_url;
    private String broadcaster_id;
    private String broadcaster_name;
    private String creator_id;
    private String creator_name;
    private String video_id;
    private String game_id;
    private String donwlaodurl;
    private String language;
    private String title;
    private int view_count;
    private String created_at;
    private String thumbnail_url;


    public TwitchClip(String clipurl, String id ,  String embededURL, String broadcasterID, String broadcaster, String createrID, String creator, String videoID, String gameID, String language, String title, int views, String created, String thumbnail) throws IOException {

        this.url = clipurl;
        this.id = id;
        this.embed_url = embededURL;
        this.broadcaster_id = broadcasterID;
        this.broadcaster_name = broadcaster;
        this.creator_id = createrID;
        this.creator_name = creator;
        this.video_id = videoID;
        this.game_id = gameID;
        this.language = language;
        this.title = title;
        this.view_count = views;
        this.created_at = created;
        this.thumbnail_url = thumbnail;

        this.donwlaodurl = TwitchService.buildDownloadURL(id, false);

    }


    public String getDonwlaodurl() {
        return donwlaodurl;
    }


    public void setDonwlaodurl(String donwlaodurl) {
        this.donwlaodurl = donwlaodurl;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmbed_url() {
        return embed_url;
    }

    public void setEmbed_url(String embed_url) {
        this.embed_url = embed_url;
    }

    public String getBroadcaster_id() {
        return broadcaster_id;
    }

    public void setBroadcaster_id(String broadcaster_id) {
        this.broadcaster_id = broadcaster_id;
    }

    public String getBroadcaster_name() {
        return broadcaster_name;
    }

    public void setBroadcaster_name(String broadcaster_name) {
        this.broadcaster_name = broadcaster_name;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }


public void printTwitchClip(){
        System.out.println("Clip ID: " + id);
        System.out.println("Clip URL: " + url);
        System.out.println("Embed URL: " + embed_url);
        System.out.println("Broadcaster ID: " + broadcaster_id);
        System.out.println("Broadcaster Name: " + broadcaster_name);
        System.out.println("Creator ID: " + creator_id);
        System.out.println("Creator Name: " + creator_name);
        System.out.println("Video ID: " + video_id);
        System.out.println("Game ID: " + game_id);
        System.out.println("Language: " + language);
        System.out.println("Title: " + title);
        System.out.println("View Count: " + view_count);
        System.out.println("Created At: " + created_at);
        System.out.println("Thumbnail URL: " + thumbnail_url);
        System.out.println("Download URL: " + donwlaodurl);

}


}
