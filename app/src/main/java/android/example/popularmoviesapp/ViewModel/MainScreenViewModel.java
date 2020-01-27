package android.example.popularmoviesapp.ViewModel;

import android.app.Application;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.Repository.DataExchanger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import static android.example.popularmoviesapp.Utils.ConstantVars.*;

public class MainScreenViewModel extends AndroidViewModel {

    private static MainScreenViewModel mainScreenViewModel;
    private static final DataExchanger dataExchanger = DataExchanger.getInstance();
    private static final String TAG = MainScreenViewModel.class.getSimpleName();

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
    }

    public void setMoviesDataToAdapter2(String query, LifecycleOwner owner) {
        if (cacheMovies.size() == 0 || (System.currentTimeMillis() - cacheMovies.get(0).getUpdatedAt()) > EXPIRATION_TIME) {
            Log.i(TAG, ">>>>>" + "Fetching data...");
            LiveData<List<Movie>> liveMovies = dataExchanger.getLiveMovies();
            liveMovies.observe(owner, movies -> {
                Log.i(TAG, ">>>>>" + "Data changes and the query is: " + query);
                Log.i(TAG, "<<<<<" + "Size of PopMovies: " + cachePopularMovies.size());
                Log.i(TAG, "<<<<<" + "Size of TopMovies: " + cacheHighlyRatedMovies.size());
                Log.i(TAG, "<<<<<" + "Size of FavMovies: " + cacheFavoriteMovies.size());
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
            });
        } else {
            Log.i(TAG, "<<<<<" + "No need to fetch data");
        }
        setMoviesDataToAdapter(query);
    }

    public void setMoviesDataToAdapter(String query) {
        Log.i(TAG, "<<<<<" + "Adapter data is set1");
        switch (query) {
            case GET_MOST_POPULAR_MOVIES: dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
                break;
            case GET_TOP_RATED_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
                break;
            case GET_FAVORITE_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
                break;
        }
    }

    public void setMoviesDataToAdapter(int state) {
        Log.i(TAG, "<<<<<" + "Adapter data is set2");
        switch (state) {
            case STATE_POP: dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
            break;
            case STATE_HIGH: dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
            break;
            case STATE_FAV: dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
            break;
        }
    }

    public void notifyDataChanged(Movie movie) {
        if (movie.getIsLiked() == 1) {
            int currId = movie.getId();
            boolean flag = true;
            for (Movie m : cacheFavoriteMovies) {
                if (currId == m.getId()) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                cacheFavoriteMovies.add(movie);
            }
        }
        else cacheFavoriteMovies.remove(movie);
    }

    public void deleteAllMovies() {
        dataExchanger.deleteAllData();
        cacheMovies.clear();
        cachePopularMovies.clear();
        cacheHighlyRatedMovies.clear();
        cacheFavoriteMovies.clear();
        Log.i(TAG, "<<<<<" + "You are truncating the database");
        setMoviesDataToAdapter(GET_MOST_POPULAR_MOVIES);
    }

}
