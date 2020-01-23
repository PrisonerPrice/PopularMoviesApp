package android.example.popularmoviesapp.Repository;

import android.content.Context;
import android.example.popularmoviesapp.Database.AppDatabase;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.Networking.NetworkUtils;
import android.example.popularmoviesapp.View.MainScreenAdapter;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.example.popularmoviesapp.Networking.NetworkUtils.*;
import static android.example.popularmoviesapp.View.MainScreenAdapter.*;

public class DataExchanger {

    private static final Object LOCK = new Object();
    private static AppDatabase appDatabase;
    private static final AppExecutors appExecutors = AppExecutors.getInstance();
    private static DataExchanger dataExchanger;
    private static int DEFAULT_MOVIE_ID = -1;

    public static ArrayList<Movie> cacheData = new ArrayList<>();
    public static MainScreenAdapter mainScreenAdapter;

    private static Context context;
    private static MyClickListener listener;

    private final static long EXPIRATION_TIME = 1000 * 3600 * 24; // one day

    public DataExchanger(Context context, MyClickListener listener) {
        this.context = context;
        appDatabase = AppDatabase.getInstance(context);
        this.listener = listener;
        this.mainScreenAdapter = new MainScreenAdapter(listener);
    }

    public static DataExchanger getInstance(){
        if(dataExchanger == null){
            synchronized (LOCK){
                dataExchanger = new DataExchanger(context, listener);
            }
        }
        return dataExchanger;
    }

    public void dataGenerator(String query){
        MovieQuery movieAPIQuery = new MovieQuery();
        movieAPIQuery.execute(query);
    }

    public static void insertRetrievedDataIntoDatabase(ArrayList<Movie> data){
        appExecutors.getDiskIO().execute(() -> {
            for (Movie m : data) {
                appDatabase.movieDao().updateMovie(m);
            }
        });
    }

    public void markMovieAsFavorite(int id){
        appExecutors.getDiskIO().execute(() ->{
            Movie movie = appDatabase.movieDao().getMovieById(id);
            movie.setIsLiked(1);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void cancelMovieAsFavorite(int id){
        appExecutors.getDiskIO().execute(() ->{
            Movie movie = appDatabase.movieDao().getMovieById(id);
            movie.setIsLiked(0);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void showFavoriteMovies(){
        MovieQuery movieDatabaseQuery = new MovieQuery();
        movieDatabaseQuery.execute(GET_FAVORITE_MOVIES);
    }

    public List<Movie> getPopularMovies(){
        return appDatabase.movieDao().getPopularMovies();
    }

    public List<Movie> getHighlyRankedMovies(){
        return appDatabase.movieDao().getHighlyRankedMovies();
    }

    public List<Movie> getFavoriteMovies(){
        return appDatabase.movieDao().getFavoriteMovies();
    }


    public static class MovieQuery extends AsyncTask<String, Void, ArrayList<Movie>>{

        String query;

        @Override
        protected ArrayList<Movie> doInBackground(String... urls) {
            query = urls[0];
            if (query.equals(GET_FAVORITE_MOVIES)){
                List<Movie> movies = appDatabase.movieDao().getFavoriteMovies();
                ArrayList<Movie> data = new ArrayList<>();
                for (Movie movie : movies){
                    Log.d("Loading favorites: ", movie.getTitle());
                    data.add(movie);
                }
                return data;
            } else {
                ArrayList<Movie> movies = new ArrayList<>();
                if (query.equals(GET_MOST_POPULAR_MOVIES)) movies = (ArrayList<Movie>) appDatabase.movieDao().getPopularMovies();
                if (query.equals(GET_TOP_RATED_MOVIES)) movies = (ArrayList<Movie>) appDatabase.movieDao().getHighlyRankedMovies();
                if (movies != null && movies.size() > 0 && (System.currentTimeMillis() - movies.get(movies.size() - 1).getUpdatedAt() < EXPIRATION_TIME)) return movies;
                else {
                    String jsonString = null;
                    ArrayList<Movie> data = null;
                    try{
                        jsonString = getResponseFromHttpUrl(query);
                        data = jsonParsing(jsonString, query);
                        for (Movie m : data) Log.d("Parsed_data", m.encoder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return data;
                }
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> data){
            if (cacheData != null && cacheData.size() > 0){
                cacheData.clear();
            }
            cacheData.addAll(data);
            mainScreenAdapter.setData(cacheData);
            if (!query.equals(GET_FAVORITE_MOVIES)) DataExchanger.insertRetrievedDataIntoDatabase(data);
        }
    }
}
