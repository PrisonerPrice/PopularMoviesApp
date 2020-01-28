package android.example.popularmoviesapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.R;
import android.example.popularmoviesapp.ViewModel.DetailScreenViewModel;
import android.example.popularmoviesapp.ViewModel.MainScreenViewModel;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private final static String TAG = DetailActivity.class.getSimpleName();

    private ImageView moviePoster;
    private TextView movieTitle;
    private TextView movieUserRating;
    private TextView movieReleaseYear;
    private ImageButton favoriteButton;
    private TextView movieDesc;
    private TextView movieComments;
    private ImageButton trailerButton1;
    private ImageButton trailerButton2;
    private TextView textViewTrailer1;
    private TextView textViewTrailer2;
    private static DetailScreenViewModel detailScreenViewModel;
    private Movie movie;
    private int state;

    private boolean isSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailScreenViewModel = DetailScreenViewModel.getInstance(getApplication());

        movie = new Movie(detailScreenViewModel.getMovie());
        state = detailScreenViewModel.getState();

        Log.i(TAG, "<<<<<" + "Current state is: " + state);

        String posterUrl = movie.getPosterUrl();
        String title = movie.getTitle();
        String userRating = movie.getUserRating();
        String releaseYear = movie.getReleaseYear() + "";
        String description = movie.getDescription();
        String comment = movie.getComment();
        final String url1 = movie.getTrailerUrl1();
        String url2 = movie.getTrailerUrl2();
        int isLiked = movie.getIsLiked();

        movieTitle = (TextView) findViewById(R.id.detail_tv_original_title);
        movieTitle.setText(title);

        movieUserRating = (TextView) findViewById(R.id.detail_tv_user_rating);
        movieUserRating.setText("User Rating: " + userRating + " / 10");

        movieReleaseYear = (TextView) findViewById(R.id.detail_tv_release_year);
        movieReleaseYear.setText("Release year: "+ releaseYear);

        movieDesc = (TextView) findViewById(R.id.detail_tv_synopsis);
        movieDesc.setText(description);

        moviePoster = (ImageView) findViewById(R.id.detail_iv_poster);
        Picasso.get()
                .load(posterUrl).centerCrop().resize(375, 600)
                .placeholder(R.mipmap.place_holder_image_foreground).centerCrop().resize(375, 600)
                .into(moviePoster);

        favoriteButton = (ImageButton) findViewById(R.id.favorite_btn);

        if (isLiked == 1){
            isSelected = true;
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_36px);
        } else{
            isSelected = false;
            favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_36px);
        }

        favoriteButton.setOnClickListener(v -> {
            Log.d("prev state: ", isSelected + "");
            if (!isSelected){
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_36px);
                isSelected = true;
            } else{
                favoriteButton.setBackgroundResource(R.drawable.ic_favorite_border_36px);
                isSelected = false;
            }
            Log.d("curr state: ", isSelected + "");
        });

        movieComments = (TextView) findViewById(R.id.detail_tv_comments);
        movieComments.setText(comment);

        textViewTrailer1 = (TextView) findViewById(R.id.detail_tv_trailer1);
        trailerButton1 = (ImageButton) findViewById(R.id.detail_btn_trailer1);
        if (url1.substring(0,4).equals("Fail")){
            textViewTrailer1.setVisibility(View.INVISIBLE);
            trailerButton1.setVisibility(View.INVISIBLE);
        } else{
            textViewTrailer1.setOnClickListener(v -> openUrl(url1));
            trailerButton1.setOnClickListener(v -> openUrl(url1));
        }

        textViewTrailer2 = (TextView) findViewById(R.id.detail_tv_trailer2);
        trailerButton2 = (ImageButton) findViewById(R.id.detail_btn_trailer2);
        if (url2.substring(0,4).equals("Fail")){
            textViewTrailer2.setVisibility(View.INVISIBLE);
            trailerButton2.setVisibility(View.INVISIBLE);
        } else{
            textViewTrailer2.setOnClickListener(v -> openUrl(url2));
            trailerButton2.setOnClickListener(v -> openUrl(url2));
        }
    }

    public void openUrl(String url){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (isSelected) movie.setIsLiked(1);
        if (!isSelected) movie.setIsLiked(0);
        detailScreenViewModel.setBackInformation(movie);
        Log.i(TAG, "  <-  " + "Curr state is: " + state);
        MainScreenViewModel.getInstance(getApplication()).setMoviesDataToAdapter(state);
        finish();
    }
}
