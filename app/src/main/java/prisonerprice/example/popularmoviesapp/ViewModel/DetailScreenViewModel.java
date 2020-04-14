package prisonerprice.example.popularmoviesapp.ViewModel;

import android.app.Application;
import prisonerprice.example.popularmoviesapp.Database.Movie;
import prisonerprice.example.popularmoviesapp.Repository.DataExchanger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class DetailScreenViewModel extends AndroidViewModel {

    private static final String TAG = DetailScreenViewModel.class.getSimpleName();
    private Movie movie;
    private int position;
    private int state;
    private static DetailScreenViewModel detailScreenViewModel;
    private static DataExchanger dataExchanger = DataExchanger.getInstance();
    private Application application;

    public DetailScreenViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public static DetailScreenViewModel getInstance(@NonNull Application application){
        if (detailScreenViewModel == null){
            detailScreenViewModel = new DetailScreenViewModel(application);
        }
        return detailScreenViewModel;
    }

    public void setMovie(int position) {
        this.position = position;
        this.movie = dataExchanger.mainScreenAdapter.getDetailMovie(position);
        this.state = dataExchanger.mainScreenAdapter.getCurrState();
    }

    public Movie getMovie() {
        return movie;
    }

    public int getState() {
        return state;
    }

    public void setBackInformation(Movie movie) {
        if (this.movie.getIsLiked() != movie.getIsLiked()){
            Log.d(TAG, "<<<<< " + movie.getTitle() + " " + "isLiked information updates");
            dataExchanger.updateMovieFavorite(movie);
            dataExchanger.mainScreenAdapter.setMovieFavorite(position, movie);
            MainScreenViewModel.getInstance(application).notifyDataChanged(movie);
        }
    }
}
