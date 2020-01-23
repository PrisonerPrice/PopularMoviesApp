package android.example.popularmoviesapp.View;

import android.content.Context;
import android.example.popularmoviesapp.Database.Movie;
import android.example.popularmoviesapp.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder> {

    private ArrayList<Movie> data;

    private final MyClickListener myClickListener;

    public MainScreenAdapter(MyClickListener myClickListener){
        this.myClickListener = myClickListener;
    }

    public void setData(ArrayList<Movie> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    class MainScreenViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView moviePoster;
        TextView movieTitle;

        public MainScreenViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.main_screen_image_view);
            movieTitle = (TextView) itemView.findViewById(R.id.main_screen_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface MyClickListener{
        void onItemClick(int position);
    }

    @Override
    public MainScreenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForSingleView = R.layout.main_screen_view_holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForSingleView, parent, shouldAttachToParentImmediately);
        MainScreenViewHolder mainScreenViewHolder = new MainScreenViewHolder(view);

        return mainScreenViewHolder;
    }

    @Override
    public void onBindViewHolder(MainScreenViewHolder holder, int position) {
        if(data != null && data.size() != 0){
            Movie movie = data.get(position);
            String url = movie.getPosterUrl();
            String title = movie.getTitle();
            holder.movieTitle.setText(title);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().
                    load(url).networkPolicy(NetworkPolicy.OFFLINE).centerCrop().resize(750, 1200).placeholder(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).
                    into(holder.moviePoster);
        } else{
            Picasso.get().
                    load("https://google.com").placeholder(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).
                    into(holder.moviePoster);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) return 0;
        return data.size();
    }

}
