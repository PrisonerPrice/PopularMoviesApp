package android.example.popularmoviesapp.ViewModel;

import android.app.Application;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.Networking.NetworkUtils;
import android.example.popularmoviesapp.R;
import android.example.popularmoviesapp.Repository.DataExchanger;
import android.example.popularmoviesapp.Repository.MainScreenAdapter;
import android.example.popularmoviesapp.View.MainActivity;
import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import static android.example.popularmoviesapp.Networking.NetworkUtils.*;

public class MainScreenViewModel extends AndroidViewModel {

    private static MainScreenViewModel mainScreenViewModel;
    private static final DataExchanger dataExchanger = DataExchanger.getInstance();
    private static final String TAG = MainScreenViewModel.class.getSimpleName();
    private final static long EXPIRATION_TIME = 1000 * 60 * 100; // 1 minute, for the ease of testing

    private List<Movie> cachePopularMovies = new ArrayList<>();
    private List<Movie> cacheHighlyRatedMovies = new ArrayList<>();
    private List<Movie> cacheFavoriteMovies = new ArrayList<>();

    private List<Movie> cacheMovies = new ArrayList<>();

    public static MainScreenViewModel getInstance(Application application){
        if (mainScreenViewModel == null){
            mainScreenViewModel = new MainScreenViewModel(application);
        }
        return mainScreenViewModel;
    }

    public MainScreenViewModel(@NonNull Application application) {
        super(application);
        /*
        if (cachePopularMovies != null && cachePopularMovies.size() > 0) cachePopularMovies.clear();
        cachePopularMovies.addAll(dataExchanger.getPopularMovies());

        if (cacheHighlyRatedMovies != null && cacheHighlyRatedMovies.size() > 0) cacheHighlyRatedMovies.clear();
        cacheHighlyRatedMovies.addAll(dataExchanger.getHighlyRankedMovies());

        if (cacheFavoriteMovies != null && cacheFavoriteMovies.size() > 0) cacheFavoriteMovies.clear();
        cacheFavoriteMovies.addAll(dataExchanger.getFavoriteMovies());

        if (cacheMovies != null && cacheMovies.size() > 0) cacheMovies.clear();
        cacheMovies.addAll(dataExchanger.getAllMovies());

         */
    }

    public void setMoviesDataToAdapter(String query, LifecycleOwner owner){
        Log.i(TAG, ">>>>>" + "Size of popular movies: " + cachePopularMovies.size());
        Log.i(TAG, ">>>>>" + "Size of highly ranked movies: " + cacheHighlyRatedMovies.size());
        Log.i(TAG, ">>>>>" + "Size of favorite movies: " + cacheFavoriteMovies.size());

        if (query.equals(GET_MOST_POPULAR_MOVIES)) {
            dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
            if (cachePopularMovies.size() == 0 || System.currentTimeMillis() - cachePopularMovies.get(0).getUpdatedAt() > EXPIRATION_TIME) {
                Log.i(TAG, "<<<<<" + "Fetch popular movies");
                LiveData<List<Movie>> livePopularMovies = dataExchanger.getLiveMovies(GET_MOST_POPULAR_MOVIES);
                livePopularMovies.observe(owner, movies -> {
                    cachePopularMovies = movies;
                    Log.i(TAG, "<<<<<" + "Save popular movies");
                    dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
                });
            } else {
                Log.i(TAG, "<<<<<" + "No need to update popular movies");
                dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
                Log.i(TAG, "<<<<<" + cachePopularMovies.get(0).getTitle());
            }
        }

        if (query.equals(GET_TOP_RATED_MOVIES)) {
            dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
            if (cacheHighlyRatedMovies.size() == 0 || System.currentTimeMillis() - cacheHighlyRatedMovies.get(0).getUpdatedAt() > EXPIRATION_TIME) {
                Log.i(TAG, "<<<<<" + "Fetch highly ranked movies");
                LiveData<List<Movie>> liveHighlyRankedMovies = dataExchanger.getLiveMovies(GET_TOP_RATED_MOVIES);
                liveHighlyRankedMovies.observe(owner, movies -> {
                    cacheHighlyRatedMovies = movies;
                    Log.i(TAG, "<<<<<" + "Save highly ranked movies");
                    dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
                });
            } else{
                Log.i(TAG, "<<<<<" + "No need to update highly ranked movies");
                dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
                Log.i(TAG, "<<<<<" + cacheHighlyRatedMovies.get(0).getTitle());
            }
        }

        if (query.equals(GET_FAVORITE_MOVIES)) {
            dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
            if (cacheFavoriteMovies.size() == 0 || System.currentTimeMillis() - cacheFavoriteMovies.get(0).getUpdatedAt() > EXPIRATION_TIME) {
                Log.i(TAG, "<<<<<" + "Fetch favorite movies");
                LiveData<List<Movie>> liveFavoriteMovies = dataExchanger.getLiveMovies(GET_FAVORITE_MOVIES);
                liveFavoriteMovies.observe(owner, movies -> {
                    cacheFavoriteMovies = movies;
                    Log.i(TAG, "<<<<<" + "Save favorite movies");
                    dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
                });
            } else {
                Log.i(TAG, "<<<<<" + "No need to update favorite movies");
                dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
                Log.i(TAG, "<<<<<" + cacheFavoriteMovies.get(0).getTitle());
            }
        }
    }

    public void setMoviesDataToAdapter2(String query, LifecycleOwner owner) {
        if (cacheMovies.size() > 0){
            Log.i(TAG, "<<<<<" + "Size of Movies: " + cacheMovies.size());
            Long timeDelta = System.currentTimeMillis() - cacheMovies.get(0).getUpdatedAt();
            Log.i(TAG, "<<<<<" + "SystemTime is: " + System.currentTimeMillis());
            Log.i(TAG, "<<<<<" + "timeDelta is: " + timeDelta);
            Log.i(TAG, "<<<<<" + "updatedTime is: " + cacheMovies.get(0).getUpdatedAt());
        }
        if (cacheMovies.size() == 0 || System.currentTimeMillis() - cacheMovies.get(0).getUpdatedAt() > EXPIRATION_TIME){
            Log.i(TAG, ">>>>>" + "Fetching data...");
            LiveData<List<Movie>> liveMovies = dataExchanger.getLiveMovies();
            liveMovies.observe(owner, movies -> {
                Log.i(TAG, ">>>>>" + "Data changes");
                cacheMovies.clear();
                cachePopularMovies.clear();
                cacheHighlyRatedMovies.clear();
                cacheFavoriteMovies.clear();
                cacheMovies.addAll(movies);
                Log.i(TAG, "<<<<<" + "Size of Movies: " + cacheMovies.size());
                for (Movie movie : cacheMovies){
                    if (movie.getIsPopular() == 1) cachePopularMovies.add(movie);
                    if (movie.getIsHighlyRanked() == 1) cacheHighlyRatedMovies.add(movie);
                    if (movie.getIsLiked() == 1) cacheFavoriteMovies.add(movie);
                }
                setMoviesDataToAdapter(query);
            });

        } else {
            Log.i(TAG, "<<<<<" + "No need to fetch data");
            Log.i(TAG, "<<<<<" + "Size of PopMovies: " + cachePopularMovies.size());
            Log.i(TAG, "<<<<<" + "Size of TopMovies: " + cacheHighlyRatedMovies.size());
            Log.i(TAG, "<<<<<" + "Size of FavMovies: " + cacheFavoriteMovies.size());
            setMoviesDataToAdapter(query);
        }
    }

    public void setMoviesDataToAdapter(String query) {
        switch (query){
            case GET_MOST_POPULAR_MOVIES: dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
                break;
            case GET_TOP_RATED_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
                break;
            case GET_FAVORITE_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
                break;
        }
    }

    public void deleteAllMovies() {
        dataExchanger.deleteAllData(cacheMovies);
        cacheMovies.clear();
        cachePopularMovies.clear();
        cacheHighlyRatedMovies.clear();
        cacheFavoriteMovies.clear();
        setMoviesDataToAdapter(GET_MOST_POPULAR_MOVIES);
    }

}
