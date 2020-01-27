package android.example.popularmoviesapp.Repository;

import android.content.Context;
import android.example.popularmoviesapp.Database.AppDatabase;
import android.example.popularmoviesapp.Database.Movie;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.example.popularmoviesapp.Networking.NetworkUtils.*;
import static android.example.popularmoviesapp.Repository.MainScreenAdapter.*;
import static android.example.popularmoviesapp.Utils.ConstantVars.*;

public class DataExchanger {

    private final static String TAG = DataExchanger.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static AppDatabase appDatabase;
    private static final AppExecutors appExecutors = AppExecutors.getInstance();
    private static DataExchanger dataExchanger;

    public static ArrayList<Movie> cacheData = new ArrayList<>();
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

    public void setMoviesDataToAdapter(List<Movie> movies){
        mainScreenAdapter.setData((ArrayList<Movie>) movies);
    }

    public void setAdapterState(int state){
        mainScreenAdapter.setCurrState(state);
    }

    public void fetchDataFromTheWeb(){
        Log.i(TAG, "<<<<<" + "You are fetching data from the web");
        MovieQuery movieAPIQuery = new MovieQuery();
        movieAPIQuery.execute();
    }

    public LiveData<List<Movie>> getLiveMovies(){
        fetchDataFromTheWeb();
        return appDatabase.movieDao().getAllMovies();
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

    public void updateMovieFavorite(Movie movie){
        appExecutors.getDiskIO().execute(() -> {
            appDatabase.movieDao().updateMovie(movie);
        });
    }

    public void deleteAllData(){
        appExecutors.getDiskIO().execute(() -> {
            appDatabase.movieDao().deleteMovie();
        });
    }

    public static class MovieQuery extends AsyncTask<String, Void, List<Movie>>{

        @Override
        protected List<Movie> doInBackground(String... urls) {
            List<Movie> movies = new ArrayList<>();
            movies.addAll(fetchData(GET_MOST_POPULAR_MOVIES));
            movies.addAll(fetchData(GET_TOP_RATED_MOVIES));
            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies){

            // Setup default view -- popular movies
            ArrayList<Movie> defaultMovies = new ArrayList<>();
            for (Movie movie : movies){
                if (movie.getIsPopular() == 1) defaultMovies.add(movie);
            }
            mainScreenAdapter.setData(defaultMovies);

            DataExchanger.insertRetrievedDataIntoDatabase((ArrayList<Movie>) movies);
        }

        private List<Movie> fetchData(String query){
            List<Movie> movies = new ArrayList<>();
            String jsonString = null;
            try{
                jsonString = getResponseFromHttpUrl(query);
                movies = jsonParsing(jsonString, query);
                for (Movie m : movies) Log.d(TAG, "<<<<<" + m.encoder());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }
    }
}






