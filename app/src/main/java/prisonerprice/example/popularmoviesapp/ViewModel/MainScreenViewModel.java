package prisonerprice.example.popularmoviesapp.ViewModel;

import android.app.Application;
import prisonerprice.example.popularmoviesapp.Database.Movie;
import prisonerprice.example.popularmoviesapp.Repository.DataExchanger;
import prisonerprice.example.popularmoviesapp.View.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import prisonerprice.example.popularmoviesapp.Utils.ConstantVars;

public class MainScreenViewModel extends AndroidViewModel {

    private static MainScreenViewModel mainScreenViewModel;
    private static final DataExchanger dataExchanger = DataExchanger.getInstance();
    private static final String TAG = MainScreenViewModel.class.getSimpleName();

    private List<Movie> cachePopularMovies = new ArrayList<>();
    private List<Movie> cacheHighlyRatedMovies = new ArrayList<>();
    private List<Movie> cacheFavoriteMovies = new ArrayList<>();
    private List<Movie> cacheMovies = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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
        sharedPreferences = getApplication().getSharedPreferences("LAST_UPDATED", Context.MODE_PRIVATE);
        if (cacheMovies.size() == 0) {
            MainActivity.letProgressBarCome();
            Log.i(TAG, ">>>>>" + "Fetching data...");
            long lastUpdated = sharedPreferences.getLong("LAST_UPDATED", 0);
            Log.i(TAG, "Last updated at: " + lastUpdated);
            LiveData<List<Movie>> liveMovies;
            if (System.currentTimeMillis() - lastUpdated > ConstantVars.EXPIRATION_TIME || lastUpdated == 0) {
                editor = sharedPreferences.edit();
                editor.putLong("LAST_UPDATED", System.currentTimeMillis());
                editor.commit();
                liveMovies = dataExchanger.fetchDataFromTheWeb(query);
                Toast.makeText(getApplication(), "Fetching Data from the web...", Toast.LENGTH_SHORT).show();
            } else {
                liveMovies = dataExchanger.fetchDataFromTheDB(query);
            }
            liveMovies.observe(owner, movies -> {
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
            Log.i(TAG, ">>>>>" + "Data changes and the query is: " + query);
            Log.i(TAG, "<<<<<" + "Size of PopMovies: " + cachePopularMovies.size());
            Log.i(TAG, "<<<<<" + "Size of TopMovies: " + cacheHighlyRatedMovies.size());
            Log.i(TAG, "<<<<<" + "Size of FavMovies: " + cacheFavoriteMovies.size());
        }
        setMoviesDataToAdapter(query);
    }

    public void setMoviesDataToAdapter(String query) {
        Log.i(TAG, "<<<<<" + "Adapter data is set1");
        switch (query) {
            case ConstantVars.GET_MOST_POPULAR_MOVIES: dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
                break;
            case ConstantVars.GET_TOP_RATED_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
                break;
            case ConstantVars.GET_FAVORITE_MOVIES: dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
                break;
        }
    }

    public void setMoviesDataToAdapter(int state) {
        Log.i(TAG, "<<<<<" + "Adapter data is set2");
        switch (state) {
            case ConstantVars.STATE_POP: dataExchanger.setMoviesDataToAdapter(cachePopularMovies);
            break;
            case ConstantVars.STATE_HIGH: dataExchanger.setMoviesDataToAdapter(cacheHighlyRatedMovies);
            break;
            case ConstantVars.STATE_FAV: dataExchanger.setMoviesDataToAdapter(cacheFavoriteMovies);
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
        sharedPreferences = getApplication().getSharedPreferences("LAST_UPDATED", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong("LAST_UPDATED", 0);
        editor.commit();
        dataExchanger.deleteAllData();
        cacheMovies.clear();
        cachePopularMovies.clear();
        cacheHighlyRatedMovies.clear();
        cacheFavoriteMovies.clear();
        Log.i(TAG, "<<<<<" + "You are truncating the database");
        setMoviesDataToAdapter(ConstantVars.GET_MOST_POPULAR_MOVIES);
    }

}
