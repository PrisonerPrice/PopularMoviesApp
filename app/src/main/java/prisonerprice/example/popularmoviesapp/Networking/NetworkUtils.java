package prisonerprice.example.popularmoviesapp.Networking;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import prisonerprice.example.popularmoviesapp.Database.Movie;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import prisonerprice.example.popularmoviesapp.Utils.ConstantVars;

public class NetworkUtils {

    public static String getResponseFromHttpUrl(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try(Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }

    public static String getComments(int id) throws IOException{
        String url_GET_COMMENTS = ConstantVars.GET_COMMENTS.replaceAll(" ", id + "");
        Log.d("GET_COMMENTS", url_GET_COMMENTS);
        return getResponseFromHttpUrl(url_GET_COMMENTS);
    }

    public static String getVideos(int id) throws IOException{
        String url_GET_VIDEOS = ConstantVars.GET_VIDEOS.replaceAll(" ", id + "");
        Log.d("GET_VIDEOS", url_GET_VIDEOS);
        return getResponseFromHttpUrl(url_GET_VIDEOS);
    }

    public static ArrayList<Movie> jsonParsing(String jsonString, String query) throws JSONException, IOException {
        if (jsonString == null || jsonString.length() == 0) return null;

        ArrayList<Movie> data = new ArrayList<>();
        JSONObject root = new JSONObject(jsonString);
        JSONArray movieArray = root.getJSONArray("results");

        for(int i = 0; i < 20; i++){
            JSONObject movie = movieArray.getJSONObject(i);
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
                trailerUrl1 = ConstantVars.VIDEO_PREFIX + trailerArray.getJSONObject(0).getString("key");
            } catch (Exception e){
                trailerUrl1 = "Fail to get the URL";
            }
            try{
                trailerUrl2 = ConstantVars.VIDEO_PREFIX + trailerArray.getJSONObject(1).getString("key");
            } catch (Exception e){
                trailerUrl2 = "Fail to get the URL";
            }

            int isPopular = 0;
            int isHighlyRanked = 0;

            if (query.equals(ConstantVars.GET_MOST_POPULAR_MOVIES)){
                isPopular = 1;
                isHighlyRanked = 0;
            }

            if (query.equals(ConstantVars.GET_TOP_RATED_MOVIES)){
                isPopular = 0;
                isHighlyRanked = 1;
            }

            Movie record = new Movie(id,
                    title,
                    posterUrl,
                    userRating,
                    Integer.parseInt(releaseYear),
                    description,
                    comment,
                    trailerUrl1,
                    trailerUrl2,
                    0,
                    isPopular,
                    isHighlyRanked,
                    System.currentTimeMillis());
            data.add(record);
        }
        return data;
    }

}
