package android.example.popularmoviesapp.Repository;

import android.content.Context;
import android.example.popularmoviesapp.Database.AppDatabase;
import android.example.popularmoviesapp.Database.Movie;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

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

    private final static long EXPIRATION_TIME = 1000 * 60 * 100; // ten minute, for the ease of testing
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

    public void setMoviesDataToAdapter(List<Movie> movies){
        mainScreenAdapter.setData((ArrayList<Movie>) movies);
    }

    public void setLiveMoviesDataToAdapter(String query, LifecycleOwner owner){
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
        Log.i(TAG, "<<<<<You are now in dataGenerator");
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

    public boolean moviesIsEmpty(String query){
        final boolean[] isEmpty = new boolean[1];
        appExecutors.getDiskIO().execute(() -> {
            if (query.equals(GET_MOST_POPULAR_MOVIES)){
                int size = appDatabase.movieDao().getPopularMoviesSize();
                if (size > 0) isEmpty[0] = false;
                else  isEmpty[0] = true;
            }
            if (query.equals(GET_TOP_RATED_MOVIES)){
                int size = appDatabase.movieDao().getHighlyRankedMoviesSize();
                if (size > 0) isEmpty[0] = false;
                else  isEmpty[0] = true;
            }
            if (query.equals(GET_FAVORITE_MOVIES)){
                int size = appDatabase.movieDao().getFavoriteMoviesSize();
                if (size > 0) isEmpty[0] = false;
                else  isEmpty[0] = true;
            }
            isEmpty[0] = false;
        });
        return isEmpty[0];
    }

    public void deleteAllData(List<Movie> movies){
        appExecutors.getDiskIO().execute(() -> {
            for (Movie movie : movies){
                appDatabase.movieDao().deleteMovie(movie);
            }
        });
    }

    public List<Movie> getPopularMovies(){
        final ArrayList<Movie> movies = new ArrayList<>();
        appExecutors.getDiskIO().execute(() -> {
            movies.addAll(appDatabase.movieDao().getPopularMovies());
        });
        return movies;
    }

    public List<Movie> getHighlyRankedMovies(){
        final ArrayList<Movie> movies = new ArrayList<>();
        appExecutors.getDiskIO().execute(() -> {
            movies.addAll(appDatabase.movieDao().getHighlyRankedMovies());
        });
        return movies;
    }

    public List<Movie> getFavoriteMovies(){
        final ArrayList<Movie> movies = new ArrayList<>();
        appExecutors.getDiskIO().execute(() -> {
            movies.addAll(appDatabase.movieDao().getFavoriteMovies());
        });
        return movies;
    }

    public LiveData<List<Movie>> getLiveMovies(String query){
        if (query.equals(GET_MOST_POPULAR_MOVIES)) {
            dataGenerator(query);
            return appDatabase.movieDao().getLivePopularMovies();
        }
        if (query.equals(GET_TOP_RATED_MOVIES)) {
            dataGenerator(query);
            return appDatabase.movieDao().getLiveHighlyRankedMovies();
        }
        if (query.equals(GET_FAVORITE_MOVIES)) {
            dataGenerator(query);
            return appDatabase.movieDao().getLiveFavoriteMovies();
        }
        return null;
    }

    public LiveData<List<Movie>> getLiveMovies(){
        return appDatabase.movieDao().getAllMovies();
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
                if (query.equals(GET_MOST_POPULAR_MOVIES)) movies = appDatabase.movieDao().getPopularMovies();
                if (query.equals(GET_TOP_RATED_MOVIES)) movies = appDatabase.movieDao().getHighlyRankedMovies();
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
                    return movies;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies){
            mainScreenAdapter.setData((ArrayList<Movie>) movies);
            if (!query.equals(GET_FAVORITE_MOVIES)) {
                DataExchanger.insertRetrievedDataIntoDatabase((ArrayList<Movie>) movies);
            }
        }
    }
}






