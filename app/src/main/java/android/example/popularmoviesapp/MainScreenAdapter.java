package android.example.popularmoviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class MainScreenAdapter extends RecyclerView.Adapter<MainScreenAdapter.MainScreenViewHolder> {

    private String[] data;

    private final MyClickListener myClickListener;

    private int length;

    public MainScreenAdapter(String[] data, MyClickListener myClickListener){
        this.myClickListener = myClickListener;
        this.data = data;
        length = data.length;
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
        String[] temp = data[position].split("  ");
        String url = temp[0];
        String title = temp[1];
        holder.movieTitle.setText(title);
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().
                load(url).placeholder(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).
                into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return length;
    }

}
