package android.example.popularmoviesapp;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MoviesDataLoader extends AsyncTaskLoader<ArrayList<String>> {

    private String url;

    public MoviesDataLoader(Context context, String mUrl) {
        super(context);
        url = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<String> loadInBackground() {
        if (url == null){
            return null;
        }
        String jsonString = null;
        ArrayList<String> data = null;
        try {
            jsonString = NetworkUtils.getResponseFromHttpUrl(url);
            data = NetworkUtils.jsonParsing(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
