package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mTrendingView;
    private RecyclerView mTopView;
    private ImageView mFirstImage;
    private TextView mTopMovieName;

    private WebView mTrailer;
    private TextView mTrailerName;

    private RequestQueue mRequestQueue;
    private ArrayList<MovieItem> mTrendingList;
    private MoviesAdapter mTrendingAdapter;

    private ArrayList<MovieItem> mTopList;
    private MoviesAdapter mTopAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));

        mTrendingList = new ArrayList<>();
        mTopList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        TopHorizontalLayout
                = new LinearLayoutManager(
                MainActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);

        mTrendingView = findViewById(R.id.trending_view);
        mTrendingView.setLayoutManager(mLayoutManager);
        mTrendingView.setHasFixedSize(true);
        mTrendingView.setLayoutManager(HorizontalLayout);

        mTopView = findViewById(R.id.top_view);
        mTopView.setLayoutManager(mLayoutManager);
        mTopView.setHasFixedSize(true);
        mTopView.setLayoutManager(TopHorizontalLayout);

        mFirstImage = findViewById(R.id.back);
        mTopMovieName = findViewById(R.id.topmovie_name);

        mTrailer = findViewById(R.id.trailer);
        mTrailer.getSettings().setLoadsImagesAutomatically(true);
        mTrailer.getSettings().setJavaScriptEnabled(true);
        mTrailer.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mTrailerName = findViewById(R.id.movieTrailer);

        mRequestQueue = Volley.newRequestQueue(this);
        trendingList();
        //mRequestQueue.add(trendingList("",R.id.recycler_view));
        topList();

    }
    private void trendingList() {
        String url = "https://api.themoviedb.org/3/movie/popular?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            JSONObject firsthit = jsonArray.getJSONObject(0);
                            String movieBack= "https://image.tmdb.org/t/p/original" + firsthit.getString("backdrop_path");
                            mTopMovieName.setText(firsthit.getString("title"));
                            trailerVideo(firsthit.getInt("id"),firsthit.getString("title"));
                            Picasso.get().load(movieBack).fit().centerInside().into(mFirstImage);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.getString("release_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");


                                int id = hit.getInt("id");
                                mTrendingList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            mTrendingAdapter = new MoviesAdapter(MainActivity.this, mTrendingList);
                            mTrendingView.setAdapter(mTrendingAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    private void topList() {
        String url = "https://api.themoviedb.org/3/movie/top_rated?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.getString("release_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");

                                int id = hit.getInt("id");
                                mTopList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }
                            mTopAdapter = new MoviesAdapter(MainActivity.this, mTopList);
                            mTopView.setAdapter(mTopAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
    private void trailerVideo(int id, String name) {
        final String n = name;
        String url = "https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            JSONObject firsthit = jsonArray.getJSONObject(0);
                            String movieTrailer= "https://www.youtube.com/embed/" + firsthit.getString("key") + "?rel=0&modestbranding=1&autohide=1&showinfo=0&controls=0";
                            mTrailer.loadUrl(movieTrailer);
                            mTrailerName.setText(n + " Trailer");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }
}
