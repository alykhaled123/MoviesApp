package com.alykhaled.Movibes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements CastAdapter.OnItemClickListener , MoviesAdapter.OnItemClickListener {
    private RequestQueue mRequestQueue;

    private ArrayList<MovieItem> mSimilarList;
    private MoviesAdapter mSimilarAdapter;

    private ArrayList<CastMember> mCastList;
    private CastAdapter mCastAdapter;

    private ArrayList<RaingDetails> mRateList;
    private RatingAdapter mRateAdapter;

    private TextView movieName;
    private TextView movieOverview;
    private TextView movieRate;
    private TextView movieVotes;
    private TextView movieTime;
    private TextView movieSeaasons;
    private ImageView moviePoster;
    private RecyclerView mSimilarMovies;
    private RecyclerView mCastView;
    private RecyclerView mRateView;

    private WebView mTrailerVideo;
    private FrameLayout mTrailerFrame;

    private ScrollView childScrollView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.movieDetailsAds);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Window window = MovieDetailsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MovieDetailsActivity.this, R.color.colorAccent));
        
        movieName = (TextView) findViewById(R.id.moviename);
        movieOverview = (TextView) findViewById(R.id.moviedesc);
        movieRate = findViewById(R.id.rateTxt);
        movieVotes = findViewById(R.id.votesTxt);
        movieTime = findViewById(R.id.movieTime);
        movieSeaasons = findViewById(R.id.movieSeasons);
        moviePoster = (ImageView) findViewById(R.id.poster);
        mSimilarMovies =  findViewById(R.id.similar_movies_view);
        mCastView = findViewById(R.id.cast_view);
        mRateView = findViewById(R.id.ratesList);

        childScrollView = findViewById(R.id.descScrollView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MovieDetailsActivity.this);
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout
                = new LinearLayoutManager(
                MovieDetailsActivity.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        LinearLayoutManager RateLayout;
        RateLayout
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

        mRateView.setLayoutManager(mLayoutManager);
        mRateView.setHasFixedSize(true);
        mRateView.setLayoutManager(RateLayout);

        mSimilarList = new ArrayList<>();
        mCastList = new ArrayList<>();
        mRateList = new ArrayList<>();

        mTrailerVideo = findViewById(R.id.movie_trailer);
        mTrailerVideo.getSettings().setLoadsImagesAutomatically(true);
        mTrailerVideo.getSettings().setJavaScriptEnabled(true);
        mTrailerVideo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mTrailerFrame = findViewById(R.id.trailerFrame);


        Intent intent = getIntent();
        mRequestQueue = Volley.newRequestQueue(this);
        getDetails(intent.getStringExtra("id"),intent.getStringExtra("type"));
        getSimilarMovies(intent.getStringExtra("id"),intent.getStringExtra("type"));
        getCast(intent.getStringExtra("id"),intent.getStringExtra("type"));

    }
    private void getRating(String id) {
        String url = "https://www.omdbapi.com/?i="+id+"&apikey=ac852b3";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray ratings = response.getJSONArray("Ratings");
                            movieRate.setText(response.getString("imdbRating"));
                            movieVotes.setText(response.getString("imdbVotes")+" votes");
                            for (int i = 0; i < ratings.length(); i++) {
                                JSONObject hit = ratings.getJSONObject(i);
                                String rateSource = hit.getString("Source");
                                String rataValue = hit.getString("Value");

                                mRateList.add(new RaingDetails(rateSource,rataValue));
                            }
                            mRateAdapter = new RatingAdapter(MovieDetailsActivity.this,mRateList);
                            mRateView.setAdapter(mRateAdapter);

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
    private void getDetails(final String id, final String type) {
        String url = "https://api.themoviedb.org/3/"+type+"/"+id+"?api_key=2d3edd3500f7064e849c3694f8e0327c&append_to_response=videos,images,external_ids";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (type.equals("tv"))
                            {
                                movieName.setText(response.getString("name"));
                                movieOverview.setText(response.getString("overview"));
                                String episodesTxt= "Number of episodes: "+String.valueOf(response.getInt("number_of_episodes"));
                                movieTime.setText(episodesTxt);
                                String seasonsTxt= "Number of episodes: "+String.valueOf(response.getInt("number_of_seasons"));
                                movieSeaasons.setText(seasonsTxt);


                            }
                            else
                            {
                                int time = response.getInt("runtime");
                                int hours = 0, minutes = 0;
                                while(time >= 60)
                                {
                                    time -= 60;
                                    hours++;
                                }
                                NumberFormat myFormat = NumberFormat.getInstance();
                                myFormat.setGroupingUsed(true);
                                int budget;
                                minutes = time;
                                movieName.setText(response.getString("title"));
                                movieOverview.setText(response.getString("overview"));
                                movieTime.setText(String.valueOf(hours) + "h" + " " + String.valueOf(time) + "m");
                                movieSeaasons.setText("Budget: $" + myFormat.format(response.getInt("budget"))+"\n\nRevenue: $"+myFormat.format(response.getInt("revenue")));

                            }


                            String moviePosterUrl = "https://image.tmdb.org/t/p/original" + response.getString("poster_path");
                            //String movieBack= "https://image.tmdb.org/t/p/original" + response.getString("backdrop_path");
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

                            JSONObject ids = response.getJSONObject("external_ids");
                            String imdbId = ids.getString("imdb_id");
                            getRating(imdbId);
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
    private void getSimilarMovies(String id, final String type) {
        String url = "https://api.themoviedb.org/3/"+type+"/"+id+"/recommendations?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName ;
                                String movieCategory;
                                if (type.equals("tv"))
                                {
                                    movieName = hit.getString("name");
                                    movieCategory = hit.getString("first_air_date");
                                }
                                else
                                {
                                    movieName = hit.getString("title");
                                    movieCategory = hit.getString("release_date");
                                }

                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String id = hit.getString("id");

                                mSimilarList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }
                            mSimilarAdapter = new MoviesAdapter(MovieDetailsActivity.this, mSimilarList);
                            mSimilarMovies.setAdapter(mSimilarAdapter);
                            mSimilarAdapter.setOnItemClickListener(MovieDetailsActivity.this);

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
    private void getCast(String id, String type) {
        String url = "https://api.themoviedb.org/3/"+type+"/"+id+"/credits?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
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
                            mCastAdapter.setOnItemClickListener(MovieDetailsActivity.this);

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

    public void onItemClickCast(int position, ArrayList<CastMember> t ) {
        Intent detailIntent = new Intent(this, CastDetails.class);
        CastMember clickedItem = t.get(position);
        detailIntent.putExtra("id", clickedItem.getId());
        startActivity(detailIntent);
    }

    @Override
    public void onItemClick(int position, ArrayList<MovieItem> t) {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        Intent detailIntent = new Intent(this, MovieDetailsActivity.class);
        MovieItem clickedItem = t.get(position);
        detailIntent.putExtra("id", clickedItem.getId());
        detailIntent.putExtra("type",type);
        startActivity(detailIntent);
    }
}
