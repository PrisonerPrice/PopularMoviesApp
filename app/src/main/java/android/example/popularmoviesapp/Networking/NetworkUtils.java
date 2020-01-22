package android.example.popularmoviesapp.Networking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String API_KEY = "You need to use your own";

    public static final String GET_MOST_POPULAR_MOVIES = "https://api.themoviedb.org/3/movie/popular?api_key=" +
            API_KEY + "&language=en-US&page=1";

    public static final String GET_TOP_RATED_MOVIES = "https://api.themoviedb.org/3/movie/top_rated?api_key=" +
            API_KEY + "&language=en-US&page=1";

    public static final String GET_VIDEOS = "https://api.themoviedb.org/3/movie/" + " " + "/videos?api_key=" +
            API_KEY;

    public static final String GET_COMMENTS = "https://api.themoviedb.org/3/movie/" + " " + "/reviews?api_key=" +
            API_KEY;

    public static final String VIDEO_PREFIX = "https://www.youtube.com/watch?v=";

    public static String getResponseFromHttpUrl(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try(Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }

    public static String getComments(int id) throws IOException{
        String url_GET_COMMENTS = GET_COMMENTS.replaceAll(" ", id + "");
        Log.d("GET_COMMENTS", url_GET_COMMENTS);
        return getResponseFromHttpUrl(url_GET_COMMENTS);
    }

    public static String getVideos(int id) throws IOException{
        String url_GET_VIDEOS = GET_VIDEOS.replaceAll(" ", id + "");
        Log.d("GET_VIDEOS", url_GET_VIDEOS);
        return getResponseFromHttpUrl(url_GET_VIDEOS);
    }

    public static ArrayList<String> jsonParsing(String jsonString) throws JSONException, IOException {
        if (jsonString == null || jsonString.length() == 0) return null;

        ArrayList<String> data = new ArrayList<>();
        JSONObject root = new JSONObject(jsonString);
        JSONArray movieArray = root.getJSONArray("results");
        for(int i = 0; i < 20; i++){
            JSONObject movie = movieArray.getJSONObject(i);
            String record = null;
            String posterUrl = "https://image.tmdb.org/t/p/w780";
            String title = null;
            String userRating = null;
            String releaseYear = null;
            String description = null;
            int id;
            String comment = null;
            String trailerUrl1 = null;
            String trailerUrl2 = null;

            posterUrl += movie.getString("poster_path");
            title = movie.getString("title");
            userRating = movie.getString("vote_average");
            releaseYear = movie.getString("release_date").substring(0,4);
            description = movie.getString("overview");
            id = movie.getInt("id");

            JSONObject movieComments = new JSONObject(getComments(id));
            JSONArray commentsArray = movieComments.getJSONArray("results");
            try{
                comment = "User comment: " + commentsArray.getJSONObject(0).getString("content")
                        .replaceAll("\n", "").replaceAll("  ", " ");
                if (comment.length() > 300) comment = comment.substring(0, 300) + " ...";
                comment += "\n" + "by " + commentsArray.getJSONObject(0).getString("author");
            } catch (Exception e){
                comment = "No comment yet";
            }

            JSONObject movieTrailers = new JSONObject(getVideos(id));
            JSONArray trailerArray = movieTrailers.getJSONArray("results");
            try{
                trailerUrl1 = VIDEO_PREFIX + trailerArray.getJSONObject(0).getString("key");
            } catch (Exception e){
                trailerUrl1 = "Fail to get the URL";
            }
            try{
                trailerUrl2 = VIDEO_PREFIX + trailerArray.getJSONObject(1).getString("key");
            } catch (Exception e){
                trailerUrl2 = "Fail to get the URL";
            }

            record = posterUrl + "  "
                    + title + "  "
                    + userRating + "  "
                    + releaseYear + "  "
                    + description + "  "
                    + comment + "  "
                    + trailerUrl1 + "  "
                    + trailerUrl2;
            data.add(record);
        }
        return data;
    }

}
