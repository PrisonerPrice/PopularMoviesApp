package android.example.popularmoviesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieUserRating;
    private TextView movieReleaseYear;
    private TextView movieDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String message = intent.getStringExtra("my_extra_data");

        String[] temp = message.split(" ");
        String url = temp[0];
        String title = temp[1];

        movieTitle = (TextView) findViewById(R.id.detail_tv_original_title);
        movieTitle.setText(title);

        movieUserRating = (TextView) findViewById(R.id.detail_tv_user_rating);
        movieUserRating.setText(title);

        movieReleaseYear = (TextView) findViewById(R.id.detail_tv_release_year);
        movieReleaseYear.setText(title);

        movieDesc = (TextView) findViewById(R.id.detail_tv_synopsis);
        movieDesc.setText(title);

        Picasso.get().
                load(url).placeholder(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).
                into(moviePoster);

    }
}
