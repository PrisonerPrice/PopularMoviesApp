package android.example.popularmoviesapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String API_KEY = "You need to define your";

    public static final String GET_MOST_POPULAR_MOVIES = "https://api.themoviedb.org/3/movie/popular?api_key=" +
            API_KEY + "&language=en-US&page=1";

    public static final String GET_TOP_RATED_MOVIES = "https://api.themoviedb.org/3/movie/top_rated?api_key=" +
            API_KEY + "&language=en-US&page=1";


    public static String getResponseFromHttpUrl(String url) throws IOException{
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try(Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    }

    public static ArrayList<String> jsonParsing(String jsonString) throws JSONException {
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
            posterUrl += movie.getString("poster_path");
            title = movie.getString("title");
            userRating = movie.getString("vote_count");
            releaseYear = movie.getString("release_date").substring(0,4);
            description = movie.getString("overview");
            record = posterUrl + "  " +
                     title + "  " +
                     userRating + "  " +
                     releaseYear + "  " +
                     description;
            data.add(record);
        }
        return data;
    }

}
