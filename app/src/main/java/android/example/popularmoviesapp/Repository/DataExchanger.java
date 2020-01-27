package android.example.popularmoviesapp.Repository;

import android.content.Context;
import android.example.popularmoviesapp.Database.AppDatabase;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.View.MainActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

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

    public void fetchDataFromTheWeb(String query){
        Log.i(TAG, "<<<<<" + "You are fetching data from the web");
        MovieQuery movieAPIQuery = new MovieQuery();
        movieAPIQuery.execute(query);
    }

    public LiveData<List<Movie>> getLiveMovies(String query){
        fetchDataFromTheWeb(query);
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

        String query;

        @Override
        protected List<Movie> doInBackground(String... urls) {
            query = urls[0];
            List<Movie> fetchedMovies = new ArrayList<>();
            fetchedMovies.addAll(fetchData(GET_MOST_POPULAR_MOVIES));
            fetchedMovies.addAll(fetchData(GET_TOP_RATED_MOVIES));
            return fetchedMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> fetchedMovies){

            // hide progressbar
            MainActivity.letProgressBarGo();

            // Setup view
            ArrayList<Movie> resultMovies = new ArrayList<>();
            switch (query) {
                case GET_MOST_POPULAR_MOVIES: {
                    for (Movie movie : fetchedMovies){
                        if (movie.getIsPopular() == 1) resultMovies.add(movie);
                    }
                    break;
                }
                case GET_TOP_RATED_MOVIES: {
                    for (Movie movie : fetchedMovies){
                        if (movie.getIsHighlyRanked() == 1) resultMovies.add(movie);
                    }
                    break;
                }
            }
            mainScreenAdapter.setData(resultMovies);
            DataExchanger.insertRetrievedDataIntoDatabase((ArrayList<Movie>) fetchedMovies);
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






