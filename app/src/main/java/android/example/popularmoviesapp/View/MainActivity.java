package android.example.popularmoviesapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.example.popularmoviesapp.R;
import android.example.popularmoviesapp.Repository.DataExchanger;
import android.example.popularmoviesapp.Repository.MainScreenAdapter;
import android.example.popularmoviesapp.ViewModel.DetailScreenViewModel;
import android.example.popularmoviesapp.ViewModel.MainScreenViewModel;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

import static android.example.popularmoviesapp.Utils.ConstantVars.*;

public class MainActivity extends AppCompatActivity implements MainScreenAdapter.MyClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static RecyclerView mainScreenSingleView;
    private static DataExchanger dataExchanger;
    private static MainScreenViewModel viewModel;
    private static String query = GET_MOST_POPULAR_MOVIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        dataExchanger = new DataExchanger(getApplicationContext(), this);

        viewModel = MainScreenViewModel.getInstance(getApplication());

        mainScreenSingleView = (RecyclerView) findViewById(R.id.rv_main_screen);
        mainScreenSingleView.setBackgroundResource(R.color.colorPrimaryDark);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mainScreenSingleView.setLayoutManager(gridLayoutManager);

        mainScreenSingleView.setHasFixedSize(true);

        setView(query);
        mainScreenSingleView.setAdapter(dataExchanger.mainScreenAdapter);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
