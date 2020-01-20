package android.example.popularmoviesapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.popularmoviesapp.R;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
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

        String[] data = message.split("  ");
        String posterUrl = data[0];
        String title = data[1];
        String userRating = data[2];
        String releaseYear = data[3];
        String description = data[4];

        movieTitle = (TextView) findViewById(R.id.detail_tv_original_title);
        movieTitle.setText(title);

        movieUserRating = (TextView) findViewById(R.id.detail_tv_user_rating);
        movieUserRating.setText("User Rating: " + userRating + " / 10");

        movieReleaseYear = (TextView) findViewById(R.id.detail_tv_release_year);
        movieReleaseYear.setText("Release year: "+ releaseYear);

        movieDesc = (TextView) findViewById(R.id.detail_tv_synopsis);
        movieDesc.setText(description);

        moviePoster = (ImageView) findViewById(R.id.detail_iv_poster);
        Picasso.get().
                load(posterUrl).networkPolicy(NetworkPolicy.OFFLINE)
                .centerCrop().resize(375, 600).placeholder(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).
                into(moviePoster);

    }
}
