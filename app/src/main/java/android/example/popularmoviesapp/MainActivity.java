package android.example.popularmoviesapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;

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

        dataGenerator(null);

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
        movieDatabaseQuery.execute(NetworkUtils.GET_MOST_POPULAR_MOVIES);
    }


    public static class MovieDatabaseQuery extends AsyncTask<String, Void, ArrayList<String>> {

        private ArrayList<String> data;

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
            movieData.addAll(data);
            for(String d: movieData){
                Log.d("ON_POST_EXE", d);
            }
            mainScreenAdapter.notifyDataSetChanged();
        }
    }
}
