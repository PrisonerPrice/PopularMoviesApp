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

    public static ArrayList<String> cacheData = new ArrayList<>();
    public static MainScreenAdapter mainScreenAdapter;

    private static Context context;
    private static MyClickListener listener;

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
        MovieAPIQuery movieAPIQuery = new MovieAPIQuery();
        movieAPIQuery.execute(query);
    }

    public static void insertRetrievedDataIntoDatabase(ArrayList<String> data, String query){
        appExecutors.getNetworkIO().execute(() -> {
            switch (query){
                case GET_MOST_POPULAR_MOVIES: {
                    for (String d: data){
                        Movie movie = new Movie(0, 1, 0, d, new Date());
                        appDatabase.movieDao().insertMovie(movie);
                    }
                }
                break;
                case GET_TOP_RATED_MOVIES: {
                    for (String d: data){
                        Movie movie = new Movie(0, 0, 1, d, new Date());
                        appDatabase.movieDao().insertMovie(movie);
                    }
                }
                break;
            }
        });
    }

    public void markMovieAsFavorite(String dataString){
        appExecutors.getDiskIO().execute(() ->{
            Movie movie = appDatabase.movieDao().getMovieByDataString(dataString);
            movie.setIsLiked(1);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void cancelMovieAsFavorite(String dataString){
        appExecutors.getDiskIO().execute(() ->{
            Movie movie = appDatabase.movieDao().getMovieByDataString(dataString);
            movie.setIsLiked(0);
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void showFavoriteMovies(){
        MovieDatabaseQuery movieDatabaseQuery = new MovieDatabaseQuery();
        movieDatabaseQuery.execute();
    }

    public LiveData<List<Movie>> getPopularMovies(){
        return appDatabase.movieDao().getPopularMovies();
    }

    public LiveData<List<Movie>> getHighlyRankedMovies(){
        return appDatabase.movieDao().getHighlyRankedMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies(){
        return appDatabase.movieDao().getFavoriteMovies();
    }

    /*
    public LiveData<Movie> getMovie(int id){
        return appDatabase.movieDao().loadMovieById(id);
    }

    public LiveData<List<Movie>> getMovieList(){
        return appDatabase.movieDao().loadMovieList();
    }

    public void getMovieDataFromAPI(String jsonString){

    }

    public void insertMovie(Movie movie){
        appDatabase.movieDao().insertMovie(movie);
    }

    public void deleteMovie(int id){
        appExecutors.getDiskIO().execute(() -> {
            Movie movie = appDatabase.movieDao().loadMovieById(id).getValue();
            appDatabase.movieDao().deleteMovie(movie);
        });
    }

     */

    public static class MovieAPIQuery extends AsyncTask<String, Void, ArrayList<String>>{

        private ArrayList<String> data = new ArrayList<>();
        private String query;

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            query = urls[0];
            String jsonString = null;
            ArrayList<String> data = null;
            try{
                jsonString = getResponseFromHttpUrl(urls[0]);
                data = jsonParsing(jsonString);
                for (String d : data) Log.d("Parsed_data", d);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.data = data;
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<String> data){
            if (cacheData != null && cacheData.size() > 0){
                cacheData.clear();
            }
            cacheData.addAll(data);
            mainScreenAdapter.setData(cacheData);
            DataExchanger.insertRetrievedDataIntoDatabase(data, query);
        }
    }

    public static class MovieDatabaseQuery extends AsyncTask<Void, Void, ArrayList<String>>{

        private ArrayList<String> data = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            List<Movie> movies = appDatabase.movieDao().getFavoriteMovies().getValue();
            ArrayList<String> data = new ArrayList<>();
            for (Movie movie : movies){
                data.add(movie.getDataString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (cacheData != null && cacheData.size() > 0){
                cacheData.clear();
            }
            cacheData.addAll(data);
            mainScreenAdapter.setData(cacheData);
        }
    }

}
