package prisonerprice.example.popularmoviesapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.example.popularmoviesapp.R;
import prisonerprice.example.popularmoviesapp.Repository.DataExchanger;
import prisonerprice.example.popularmoviesapp.Repository.MainScreenAdapter;
import prisonerprice.example.popularmoviesapp.ViewModel.DetailScreenViewModel;
import prisonerprice.example.popularmoviesapp.ViewModel.MainScreenViewModel;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.stetho.Stetho;

import static prisonerprice.example.popularmoviesapp.Utils.ConstantVars.*;

public class MainActivity extends AppCompatActivity implements MainScreenAdapter.MyClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static RecyclerView mainScreenSingleView;
    private static DataExchanger dataExchanger;
    private static MainScreenViewModel viewModel;
    private static String query = GET_MOST_POPULAR_MOVIES;

    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "<<<<<" + "onCreate() is called");

        dataExchanger = new DataExchanger(getApplicationContext(), this);

        viewModel = MainScreenViewModel.getInstance(getApplication());

        mainScreenSingleView = (RecyclerView) findViewById(R.id.rv_main_screen);
        mainScreenSingleView.setBackgroundResource(R.color.colorPrimaryDark);

        progressBar = (ProgressBar) findViewById(R.id.progress_circular);
        letProgressBarGo();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns());

        mainScreenSingleView.setLayoutManager(gridLayoutManager);

        mainScreenSingleView.setHasFixedSize(true);

        setView(query);
        mainScreenSingleView.setAdapter(dataExchanger.mainScreenAdapter);
    }

    public static void letProgressBarCome() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void letProgressBarGo() {
        progressBar.setVisibility(View.GONE);
    }

    private void setView(String query) {
        viewModel.setMoviesDataToAdapter2(query, this);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        DetailScreenViewModel.getInstance(getApplication()).setMovie(position);
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_popularity){
            query = GET_MOST_POPULAR_MOVIES;
            setView(GET_MOST_POPULAR_MOVIES);
            dataExchanger.setAdapterState(STATE_POP);
            return true;
        }
        if(id == R.id.action_Rating){
            query = GET_TOP_RATED_MOVIES;
            setView(GET_TOP_RATED_MOVIES);
            dataExchanger.setAdapterState(STATE_HIGH);
            return true;
        }
        if(id == R.id.action_Favorite){
            query = GET_FAVORITE_MOVIES;
            setView(GET_FAVORITE_MOVIES);
            dataExchanger.setAdapterState(STATE_FAV);
            return true;
        }
        if (id == R.id.action_Clear_cache){
            viewModel.deleteAllMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int nColumns = width / 400;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
