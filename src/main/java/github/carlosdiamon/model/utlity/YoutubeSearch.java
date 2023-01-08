package github.carlosdiamon.model.utlity;

import github.carlosdiamon.model.VideoDAO;
import github.carlosdiamon.model.persistence.Persistence;
import github.carlosdiamon.model.persistence.PropertyControl;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeSearch {
    private static String search;
    private final String YOUTUBE_TOKEN;
    private static final int SEARCH_LIMIT = 10;
    private static List<VideoDAO> videos;
    private static int searchAct;
    public YoutubeSearch(String search) { // El token de la api consume 100 puntos por busqueda (1k puntos)
        YOUTUBE_TOKEN = PropertyControl.getEnv("TOKEN_YOUTUBE");
        YoutubeSearch.search = search;
        YoutubeSearch.searchAct = 1;
    }

    public void search() {
        String urlSearchJSON =  urlJSON();
        if (!statusCode(urlSearchJSON)) {
            System.out.println("NO TOKEN :(");
        }

    }
    private String urlJSON(){
        String searchFinally = search.replace(" ", "%20");
        return "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + searchFinally
                + "&type=video&maxResults=" + YoutubeSearch.SEARCH_LIMIT + "&key=" +YOUTUBE_TOKEN;
    }

    private boolean statusCode(String urlSearchJSON) {
        try {
            URL url = new URL(urlSearchJSON);
            String strJson = Persistence.readURL(url);

            if (strJson.equals("ERROR_403")) return false;

            readJSON(strJson);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void readJSON(String strJson) {
        JSONObject json = new JSONObject(strJson);
        JSONArray items = json.getJSONArray("items");

        videos = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject id = item.getJSONObject("id");
            String videoId = id.getString("videoId");
            JSONObject snippet = item.getJSONObject("snippet");
            String title = snippet.getString("title");
            String channelTitle = snippet.getString("channelTitle");
            String description = snippet.getString("description");
            VideoDAO video = new VideoDAO(videoId, title, channelTitle, description);
            videos.add(video);
        }
    }
    public static String getIdByUrl(String url) { // Regex
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?" +
                "feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return "error";
    }
    public static int getSEARCH_LIMIT() {
        return YoutubeSearch.SEARCH_LIMIT;
    }

    public static String getSearch() {
        return search;
    }

    public static int getSearchAct() {
        return YoutubeSearch.searchAct;
    }
    public static void setSearchAct(int num) {
        YoutubeSearch.searchAct = num;
    }

    public static List<VideoDAO> getVideos() {
        return YoutubeSearch.videos;
    }
}
