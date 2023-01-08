package github.carlosdiamon.model;

public class VideoDAO {

    private String videoId, title, ChannelTitle, description;

    public VideoDAO(String videoId, String title, String ChannelTitle, String description) {
        this.videoId = videoId;
        this.title = title;
        this.ChannelTitle = ChannelTitle;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public String getVideoId() { return videoId;}
    public String getDescription() {return description;}
    public String getChannelTitle() {return ChannelTitle;}
}
