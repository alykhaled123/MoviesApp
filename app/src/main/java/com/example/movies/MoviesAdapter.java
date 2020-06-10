package com.example.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<MovieItem> mExampleList;
    public MoviesAdapter(Context context, ArrayList<MovieItem> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        MovieItem currentItem = mExampleList.get(position);
        String posterurl = currentItem.getPoster();
        String backurl = currentItem.getBack();
        String movieName = currentItem.getName();
        String movieCategory = currentItem.getCategory();
        holder.mMovieName.setText(movieName);
        holder.mMovieCategory.setText(movieCategory);
        Picasso.get().load(posterurl).fit().centerInside().into(holder.mMoviePoster);
        //Picasso.get().load(backurl).fit().centerInside().into(holder.mMovieBack);
//j
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mMoviePoster;
        public ImageView mMovieBack;
        public TextView mMovieName;
        public TextView mMovieCategory;
        public ExampleViewHolder(View itemView) {
            super(itemView);
            mMoviePoster = itemView.findViewById(R.id.movie_poster);
            mMovieBack = itemView.findViewById(R.id.back);
            mMovieName = itemView.findViewById(R.id.movie_name);
            mMovieCategory = itemView.findViewById(R.id.movie_category);
        }
    }
}