package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.OnItemClickListener  {

    private String posterID;

    private RecyclerView mTrendingView;
    private RecyclerView mTopView;

    private ImageView mFirstImage;
    private TextView mTopMovieName;
    private TextView mTopMovieOverview;

    private WebView mTrailer;
    private TextView mTrailerName;

    private RequestQueue mRequestQueue;
    private ArrayList<MovieItem> mTrendingList;
    private MoviesAdapter mTrendingAdapter;

    private ArrayList<MovieItem> mTopList;
    private MoviesAdapter mTopAdapter;
    
    private FrameLayout mMoviePoster;

    private EditText mSearchText;


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
        mTopMovieOverview = findViewById(R.id.topmovie_overview);


        mTrailer = findViewById(R.id.trailer);
        mTrailer.getSettings().setLoadsImagesAutomatically(true);
        mTrailer.getSettings().setJavaScriptEnabled(true);
        mTrailer.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mTrailerName = findViewById(R.id.movieTrailer);

        mRequestQueue = Volley.newRequestQueue(this);
        
        mMoviePoster = (FrameLayout) findViewById(R.id.topmovie_poster);
        mMoviePoster.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent detailIntent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                detailIntent.putExtra("id", posterID);
                startActivity(detailIntent);
            }
        });

        mSearchText = findViewById(R.id.searchText);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Intent detailIntent = new Intent(MainActivity.this, SearchActitivty.class);
                    String gg = String.valueOf(mSearchText.getText());
                    detailIntent.putExtra("query", gg);
                    startActivity(detailIntent);
                    return true;
                }
                return false;
            }
        });
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
                            Random rand = new Random();
                            JSONArray jsonArray = response.getJSONArray("results");

                            //Get random movie
                            JSONObject randomhit = jsonArray.getJSONObject(rand.nextInt(jsonArray.length()-1));

                            //Add the poster and name of the random hit
                            String movieBack= "https://image.tmdb.org/t/p/original" + randomhit.getString("backdrop_path");
                            Picasso.get().load(movieBack).fit().centerInside().into(mFirstImage);

                            mTopMovieName.setText(randomhit.getString("title"));
                            mTopMovieOverview.setText(randomhit.getString("overview"));

                            //Set trailer video to random hit
                            trailerVideo(randomhit.getInt("id"),randomhit.getString("title"));

                            //Add movies to the list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.getString("release_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String id = hit.getString("id");
                                mTrendingList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            mTrendingAdapter = new MoviesAdapter(MainActivity.this, mTrendingList);
                            mTrendingView.setAdapter(mTrendingAdapter);
                            mTrendingAdapter.setOnItemClickListener(MainActivity.this);

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
                                String id = hit.getString("id");

                                mTopList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }
                            mTopAdapter = new MoviesAdapter(MainActivity.this, mTopList);
                            mTopView.setAdapter(mTopAdapter);
                            mTopAdapter.setOnItemClickListener(MainActivity.this);

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
        posterID = String.valueOf(id);
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

    public void onItemClick(int position,ArrayList<MovieItem> t ) {
        Intent detailIntent = new Intent(this, MovieDetailsActivity.class);
        MovieItem clickedItem = t.get(position);
        detailIntent.putExtra("id", clickedItem.getId());
        startActivity(detailIntent);
    }
}
