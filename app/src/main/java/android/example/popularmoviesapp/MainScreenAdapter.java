package android.example.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class MainScreenAdapter extends RecyclerView.Adapter {

    private String[] data;

    private int length;

    public MainScreenAdapter(String[] data){
        this.data = data;
        length = data.length;
    }

    class MainScreenViewHolder extends RecyclerView.ViewHolder{

        ImageView moviePoster;
        TextView movieTitle;


        public MainScreenViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.main_screen_image_view);
            movieTitle = (TextView) itemView.findViewById(R.id.main_screen_text_view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForSingleView = R.layout.main_screen_view_holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForSingleView, parent, shouldAttachToParentImmediately);
        MainScreenViewHolder mainScreenViewHolder = new MainScreenViewHolder(view);

        return mainScreenViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainScreenViewHolder convertedHolder= (MainScreenViewHolder) holder;
        String[] temp = data[position].split("<+>");
        String url = temp[0];
        String title = temp[1];
        convertedHolder.movieTitle.setText(title);
        Picasso.get().load(url).into(convertedHolder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return length;
    }


}
