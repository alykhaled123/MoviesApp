package com.alykhaled.Movibes;

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
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position,ArrayList<MovieItem> t);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


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
        String movieName = currentItem.getName();
        String movieCategory = currentItem.getCategory();
        holder.mMovieName.setText(movieName);
        holder.mMovieCategory.setText(movieCategory);
        Picasso.get().load(posterurl).fit().centerInside().into(holder.mMoviePoster);
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
            mMovieName = itemView.findViewById(R.id.movie_name);
            mMovieCategory = itemView.findViewById(R.id.movie_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position,mExampleList);
                        }
                    }
                }
            });
        }
    }
}