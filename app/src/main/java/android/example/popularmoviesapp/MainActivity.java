package android.example.popularmoviesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MainScreenAdapter.MyClickListener{

    private MainScreenAdapter mainScreenAdapter;
    private RecyclerView mainScreenSingleView;
    private MovieDatabaseQuery movieDatabaseQuery;
    private static String[] movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieData = dataGenerator(null);

        mainScreenSingleView = (RecyclerView) findViewById(R.id.rv_main_screen);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mainScreenSingleView.setLayoutManager(staggeredGridLayoutManager);

        mainScreenSingleView.setHasFixedSize(true);

        mainScreenAdapter = new MainScreenAdapter(movieData, this);
        mainScreenSingleView.setAdapter(mainScreenAdapter);
    }

    @Override
    public void onItemClick(int position) {
        /*
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("my_extra_data", dataGenerator(null)[position]);
        startActivity(detailIntent);

         */
    }

    public String[] dataGenerator(String query){
        String[] data = new String[20];
        // Generate fake data
        /*
        for(int i = 0; i < data.length; i++){
            data[i] =
                    "https://avatars3.githubusercontent.com/u/15194056?s=400&u=d3288d844f9be0a7ca38a81caf29ce1d67586afd&v=4" +
                            " " + i;
        }

         */

        // Generate real data

        movieDatabaseQuery = new MovieDatabaseQuery();
        movieDatabaseQuery.execute(NetworkUtils.GET_MOST_POPULAR_MOVIES);

        return movieData;
    }

    public static class MovieDatabaseQuery extends AsyncTask<String, Void, String[]> {

        private String[] data;

        @Override
        protected String[] doInBackground(String... urls) {
            String jsonString = null;
            String data[] = new String[20];
            try {
                jsonString = NetworkUtils.getResponseFromHttpUrl(urls[0]);
                data = NetworkUtils.jsonParsing(jsonString);
                for(String d: data){
                    Log.d("Parsed_data", d);
                }
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.data = data;
            return data;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            movieData = strings;
        }

    }
}
