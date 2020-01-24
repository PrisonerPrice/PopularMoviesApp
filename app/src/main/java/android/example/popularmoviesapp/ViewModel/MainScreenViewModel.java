package android.example.popularmoviesapp.ViewModel;

import android.app.Application;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.Networking.NetworkUtils;
import android.example.popularmoviesapp.R;
import android.example.popularmoviesapp.Repository.DataExchanger;
import android.example.popularmoviesapp.Repository.MainScreenAdapter;
import android.example.popularmoviesapp.View.MainActivity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MainScreenViewModel extends AndroidViewModel {

    private static final DataExchanger dataExchanger = DataExchanger.getInstance();

    private static final String TAG = MainActivity.class.getSimpleName();

    private LiveData<List<Movie>> liveMovies;

    public MainScreenViewModel(@NonNull Application application) {
        super(application);
    }

    public int getMovieSize(String query){
        return dataExchanger.getMovieSize(query);
    }

    public void setMoviesDataToAdapter(String query, LifecycleOwner owner){
        dataExchanger.setMoviesDataToAdapter(query, owner);
    }

}
