package com.example.movies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class TopMoviesAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<MovieItem> movies;
    private MoviesAdapter.OnItemClickListener mListener;

    public void setOnItemClickListener(MoviesAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public TopMoviesAdapter(Context context, ArrayList<MovieItem> movies) {
        this.context = context;
        this.movies = movies;

    }
    public int getCount() {
        return movies.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.top_movies_slider, null);
        ImageView imageView = view.findViewById(R.id.topmovie_back);
        Picasso.get().load(movies.get(position).getBack()).fit().centerInside().into(imageView);
        TextView movieName = view.findViewById(R.id.topmovie_name);
        movieName.setText(movies.get(position).getName());
        TextView movieOverview = view.findViewById(R.id.topmovie_overview);
        movieOverview.setText(movies.get(position).getCategory());
        TextView movieRate = view.findViewById(R.id.rateTxtPoster);
        movieRate.setText(movies.get(position).getPoster());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(position,movies);
            }
        });
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }


    public interface OnItemClickListener {
        void onItemClick(int position,ArrayList<MovieItem> t);
    }
}
