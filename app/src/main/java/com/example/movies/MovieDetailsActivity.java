package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

public class MovieDetailsActivity extends AppCompatActivity {
    private RequestQueue mRequestQueue;

    private ArrayList<MovieItem> mSimilarList;
    private MoviesAdapter mSimilarAdapter;

    private ArrayList<CastMember> mCastList;
    private CastAdapter mCastAdapter;

    private TextView movieName;
    private TextView movieOverview;
    private ImageView moviePoster;
    private RecyclerView mSimilarMovies;
    private RecyclerView mCastView;

    private WebView mTrailerVideo;
    private FrameLayout mTrailerFrame;

    private ScrollView childScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Window window = MovieDetailsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MovieDetailsActivity.this, R.color.colorAccent));
        
        movieName = (TextView) findViewById(R.id.moviename);
        movieOverview = (TextView) findViewById(R.id.moviedesc);
        moviePoster = (ImageView) findViewById(R.id.poster);
        mSimilarMovies =  findViewById(R.id.similar_movies_view);
        mCastView =findViewById(R.id.cast_view);

        childScrollView = findViewById(R.id.descScrollView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this);
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout
                = new LinearLayoutManager(
                MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);

        TopHorizontalLayout
                = new LinearLayoutManager(
                MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);

        mSimilarMovies.setLayoutManager(mLayoutManager);
        mSimilarMovies.setHasFixedSize(true);
        mSimilarMovies.setLayoutManager(HorizontalLayout);

        mCastView.setLayoutManager(mLayoutManager);
        mCastView.setHasFixedSize(true);
        mCastView.setLayoutManager(TopHorizontalLayout);

        mSimilarList = new ArrayList<>();
        mCastList = new ArrayList<>();

        mTrailerVideo = findViewById(R.id.movie_trailer);
        mTrailerVideo.getSettings().setLoadsImagesAutomatically(true);
        mTrailerVideo.getSettings().setJavaScriptEnabled(true);
        mTrailerVideo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mTrailerFrame = findViewById(R.id.trailerFrame);

        childScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        /* Disallow ScrollView to intercept touch events. */
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return false;
            }
        });


        Intent intent = getIntent();
        mRequestQueue = Volley.newRequestQueue(this);
        getDetails(intent.getStringExtra("id"));
        getSimilarMovies(intent.getStringExtra("id"));
        getCast(intent.getStringExtra("id"));

    }

    private void getDetails(String id) {
        String url = "https://api.themoviedb.org/3/movie/"+id+"?api_key=2d3edd3500f7064e849c3694f8e0327c&append_to_response=videos,images";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            movieName.setText(response.getString("title"));
                            movieOverview.setText(response.getString("overview"));


                            String movieCategory = response.getString("release_date");
                            String moviePosterUrl = "https://image.tmdb.org/t/p/original" + response.getString("poster_path");
                            String movieBack= "https://image.tmdb.org/t/p/original" + response.getString("backdrop_path");
                            Picasso.get().load(moviePosterUrl).fit().centerInside().into(moviePoster);
                            JSONObject videosArray = response.getJSONObject("videos");

                            JSONArray jsonArray = videosArray.getJSONArray("results");
                            if (jsonArray.length() != 0)
                            {
                                JSONObject firsthit = jsonArray.getJSONObject(0);
                                String movieTrailer= "https://www.youtube.com/embed/" + firsthit.getString("key") + "?rel=0&modestbranding=1&autohide=1&showinfo=0&controls=0";
                                mTrailerVideo.loadUrl(movieTrailer);

                            }
                            else
                            {
                                mTrailerFrame.setVisibility(View.INVISIBLE);
                                mTrailerFrame.setLayoutParams(new LinearLayout.LayoutParams(mTrailerFrame.getWidth(),0));
                            }



                            //String id = hit.getString("id");

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
    private void getSimilarMovies(String id) {
        String url = "https://api.themoviedb.org/3/movie/"+id+"/recommendations?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
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
                                String id = hit.getString("id");

                                mSimilarList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }
                            mSimilarAdapter = new MoviesAdapter(MovieDetailsActivity.this, mSimilarList);
                            mSimilarMovies.setAdapter(mSimilarAdapter);
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
    private void getCast(String id) {
        String url = "https://api.themoviedb.org/3/movie/"+id+"/credits?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("cast");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("character");
                                String movieCategory = hit.getString("name");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("profile_path");
                                String id = hit.getString("id");

                                mCastList.add(new CastMember(id,movieName ,movieCategory, moviePoster));
                            }
                            mCastAdapter = new CastAdapter(MovieDetailsActivity.this, mCastList);
                            mCastView.setAdapter(mCastAdapter);
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
