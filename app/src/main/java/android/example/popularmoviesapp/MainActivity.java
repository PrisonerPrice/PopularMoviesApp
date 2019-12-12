package android.example.popularmoviesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private MainScreenAdapter mainScreenAdapter;
    private RecyclerView mainScreenSingleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScreenSingleView = (RecyclerView) findViewById(R.id.rv_main_screen);

        // Generate fake data
        String[] fakeData = new String[25];
        for(int i = 0; i < fakeData.length; i++){
            fakeData[i] =
                    "https://avatars3.githubusercontent.com/u/15194056?s=400&u=d3288d844f9be0a7ca38a81caf29ce1d67586afd&v=4" +
                    " " + i;
        }

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mainScreenSingleView.setLayoutManager(staggeredGridLayoutManager);

        mainScreenSingleView.setHasFixedSize(true);

        mainScreenAdapter = new MainScreenAdapter(fakeData);
        mainScreenSingleView.setAdapter(mainScreenAdapter);
    }
}
