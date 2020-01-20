package android.example.popularmoviesapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.example.popularmoviesapp.Networking.NetworkUtils;
import android.example.popularmoviesapp.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainScreenAdapter.MyClickListener {

    private static MainScreenAdapter mainScreenAdapter;
    private static ArrayList<String> movieData = new ArrayList<>();
    private static RecyclerView mainScreenSingleView;
    private MovieDatabaseQuery movieDatabaseQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataGenerator(NetworkUtils.GET_TOP_RATED_MOVIES);

        mainScreenSingleView = (RecyclerView) findViewById(R.id.rv_main_screen);

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(this, 2);

        mainScreenSingleView.setLayoutManager(gridLayoutManager);

        mainScreenSingleView.setHasFixedSize(true);

        mainScreenAdapter = new MainScreenAdapter(movieData, this);
        mainScreenSingleView.setAdapter(mainScreenAdapter);

    }

    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("my_extra_data", movieData.get(position));
        startActivity(detailIntent);

    }

    public void dataGenerator(String query){
        movieDatabaseQuery = new MovieDatabaseQuery();
        movieDatabaseQuery.execute(query);
    }


    public static class MovieDatabaseQuery extends AsyncTask<String, Void, ArrayList<String>> {

        private ArrayList<String> data = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            String jsonString = null;
            ArrayList<String> data = null;
            try {
                jsonString = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                data = NetworkUtils.jsonParsing(jsonString);
                for (String d : data) {
                    Log.d("Parsed_data", d);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.data = data;
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<String> data) {
            if (movieData != null && movieData.size() > 0){
                movieData.clear();
            }
            movieData.addAll(data);
            for(String d: movieData){
                Log.d("ON_POST_EXE", d);
            }
            mainScreenAdapter.notifyDataSetChanged();
        }
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
            dataGenerator(NetworkUtils.GET_MOST_POPULAR_MOVIES);
            return true;
        }
        if(id == R.id.action_Rating){
            dataGenerator(NetworkUtils.GET_TOP_RATED_MOVIES);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
