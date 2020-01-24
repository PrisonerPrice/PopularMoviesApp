package android.example.popularmoviesapp.Repository;

import android.content.Context;
import android.example.popularmoviesapp.Database.AppDatabase;
import android.example.popularmoviesapp.Database.Movie;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.example.popularmoviesapp.Networking.NetworkUtils.*;
import static android.example.popularmoviesapp.Repository.MainScreenAdapter.*;

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

    private final static long EXPIRATION_TIME = 1000 * 60 * 10; // ten minute, for the ease of testing
    private final static String TAG = DataExchanger.class.getSimpleName();

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

    public void setMoviesDataToAdapter(String query, LifecycleOwner owner){
        LiveData<List<Movie>> liveMovies = getLiveMovies(query);
        liveMovies.observe(owner, movies -> {
            Log.d(TAG, "try fetch data from DB");
            if (cacheData != null && cacheData.size() > 0) cacheData.clear();
            cacheData = (ArrayList<Movie>) movies;
            mainScreenAdapter.setData(cacheData);
        });
        Log.d(TAG, "size = 0, fetch data from the web and update DB");
        dataGenerator(query);

    }

    public void dataGenerator(String query){
        MovieQuery movieAPIQuery = new MovieQuery();
        movieAPIQuery.execute(query);
    }

    public static void insertRetrievedDataIntoDatabase(ArrayList<Movie> movies){
        appExecutors.getDiskIO().execute(() -> {
            for (Movie currMovie : movies) {
                Movie prevMovie = appDatabase.movieDao().getMovieById(currMovie.getId());
                if (prevMovie == null) appDatabase.movieDao().insertMovie(currMovie);
                else{
                    if (prevMovie.getIsPopular() == 1) currMovie.setIsPopular(1);
                    if (prevMovie.getIsHighlyRanked() == 1) currMovie.setIsHighlyRanked(1);
                    if (prevMovie.getIsLiked() == 1) currMovie.setIsLiked(1);
                    currMovie.setUpdatedAt(prevMovie.getUpdatedAt());
                    appDatabase.movieDao().updateMovie(currMovie);
                }
            }
        });
    }

    public void markMovieAsFavorite(int id){
        appExecutors.getDiskIO().execute(() -> {
            Movie movie = appDatabase.movieDao().getMovieById(id);
            movie.setIsLiked(1);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void cancelMovieAsFavorite(int id){
        appExecutors.getDiskIO().execute(() -> {
            Movie movie = appDatabase.movieDao().getMovieById(id);
            movie.setIsLiked(0);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void showFavoriteMovies(){
        MovieQuery movieDatabaseQuery = new MovieQuery();
        movieDatabaseQuery.execute(GET_FAVORITE_MOVIES);
    }

    public int getMovieSize(String query){
        final int[] size = new int[1];
        appExecutors.getDiskIO().execute(() -> {
            if (query.equals(GET_MOST_POPULAR_MOVIES)){
                size[0] = appDatabase.movieDao().getPopularMoviesSize();
            }
            if (query.equals(GET_TOP_RATED_MOVIES)){
                size[0] = appDatabase.movieDao().getHighlyRankedMoviesSize();
            }
            if (query.equals(GET_FAVORITE_MOVIES)){
                size[0] = appDatabase.movieDao().getFavoriteMoviesSize();
            }
        });
        return size[0];
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

    private LiveData<List<Movie>> getLiveMovies(String query){
        if (query.equals(GET_MOST_POPULAR_MOVIES)) return appDatabase.movieDao().getLivePopularMovies();
        if (query.equals(GET_TOP_RATED_MOVIES)) return appDatabase.movieDao().getLiveHighlyRankedMovies();
        if (query.equals(GET_FAVORITE_MOVIES)) return appDatabase.movieDao().getLiveFavoriteMovies();
        return null;
    }

    public static class MovieQuery extends AsyncTask<String, Void, List<Movie>>{

        String query;

        @Override
        protected List<Movie> doInBackground(String... urls) {
            query = urls[0];
            List<Movie> movies = new ArrayList<>();
            if (query.equals(GET_FAVORITE_MOVIES)){
                movies = dataExchanger.getFavoriteMovies();
                ArrayList<Movie> data = new ArrayList<>();
                for (Movie movie : movies){
                    Log.d("Loading favorites: ", movie.getTitle());
                    data.add(movie);
                }
                return movies;
            } else {
                if (query.equals(GET_MOST_POPULAR_MOVIES)) movies = dataExchanger.getPopularMovies();
                if (query.equals(GET_TOP_RATED_MOVIES)) movies = dataExchanger.getHighlyRankedMovies();
                if (movies != null && movies.size() > 0
                        && System.currentTimeMillis() - movies.get(movies.size() - 1).getUpdatedAt() < EXPIRATION_TIME){
                    return movies;
                } else {
                    String jsonString = null;
                    try{
                        jsonString = getResponseFromHttpUrl(query);
                        movies = jsonParsing(jsonString, query);
                        for (Movie m : movies) Log.d("Parsed_data", m.encoder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    insertRetrievedDataIntoDatabase((ArrayList<Movie>) movies);
                    return movies;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies){
            if (cacheData != null && cacheData.size() > 0){
                cacheData.clear();
            }
            cacheData.addAll(movies);
            mainScreenAdapter.setData(cacheData);
            if (!query.equals(GET_FAVORITE_MOVIES)) DataExchanger.insertRetrievedDataIntoDatabase((ArrayList<Movie>) movies);
        }
    }
}






