package prisonerprice.example.popularmoviesapp.Repository;

import android.content.Context;
import prisonerprice.example.popularmoviesapp.Database.AppDatabase;
import prisonerprice.example.popularmoviesapp.Database.Movie;
import prisonerprice.example.popularmoviesapp.View.MainActivity;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import prisonerprice.example.popularmoviesapp.Networking.NetworkUtils;
import prisonerprice.example.popularmoviesapp.Utils.ConstantVars;

import static prisonerprice.example.popularmoviesapp.Repository.MainScreenAdapter.*;

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

    public LiveData<List<Movie>> fetchDataFromTheWeb(String query){
        Log.i(TAG, "<<<<<" + "You are fetching data from the web");
        WebMovieQuery webMovieAPIQuery = new WebMovieQuery();
        webMovieAPIQuery.execute(query);
        return appDatabase.movieDao().getAllLiveMovies();
    }

    public LiveData<List<Movie>> fetchDataFromTheDB(String query){
        Log.i(TAG, "<<<<<" + "You are fetching data from the DB");
        DBMovieQuery dbMovieQuery = new DBMovieQuery();
        dbMovieQuery.execute(query);
        return appDatabase.movieDao().getAllLiveMovies();
    }

    public List<Movie> getAllMovies(){
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

    public static class WebMovieQuery extends AsyncTask<String, Void, List<Movie>>{

        String query;

        @Override
        protected List<Movie> doInBackground(String... strings) {
            query = strings[0];
            List<Movie> fetchedMovies = new ArrayList<>();
            fetchedMovies.addAll(fetchData(ConstantVars.GET_MOST_POPULAR_MOVIES));
            fetchedMovies.addAll(fetchData(ConstantVars.GET_TOP_RATED_MOVIES));
            return fetchedMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> fetchedMovies){

            // hide progressbar
            MainActivity.letProgressBarGo();

            // Setup view
            ArrayList<Movie> resultMovies = new ArrayList<>();
            switch (query) {
                case ConstantVars.GET_MOST_POPULAR_MOVIES: {
                    for (Movie movie : fetchedMovies){
                        if (movie.getIsPopular() == 1) resultMovies.add(movie);
                    }
                    break;
                }
                case ConstantVars.GET_TOP_RATED_MOVIES: {
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
                jsonString = NetworkUtils.getResponseFromHttpUrl(query);
                movies = NetworkUtils.jsonParsing(jsonString, query);
                for (Movie m : movies) Log.d(TAG, "<<<<<" + m.encoder());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }
    }

    public static class DBMovieQuery extends AsyncTask<String, Void, List<Movie>> {

        String query;

        @Override
        protected List<Movie> doInBackground(String... strings) {
            query = strings[0];
            List<Movie> fetchedMovies;
            fetchedMovies = appDatabase.movieDao().getAllMovies();
            return fetchedMovies;
        }

        @Override
        protected void onPostExecute(List<Movie> fetchedMovies){

            // hide progressbar
            MainActivity.letProgressBarGo();

            // Setup view
            ArrayList<Movie> resultMovies = new ArrayList<>();
            switch (query) {
                case ConstantVars.GET_MOST_POPULAR_MOVIES: {
                    for (Movie movie : fetchedMovies){
                        if (movie.getIsPopular() == 1) resultMovies.add(movie);
                    }
                    break;
                }
                case ConstantVars.GET_TOP_RATED_MOVIES: {
                    for (Movie movie : fetchedMovies){
                        if (movie.getIsHighlyRanked() == 1) resultMovies.add(movie);
                    }
                    break;
                }
            }
            mainScreenAdapter.setData(resultMovies);
            DataExchanger.insertRetrievedDataIntoDatabase((ArrayList<Movie>) fetchedMovies);
        }
    }
}






