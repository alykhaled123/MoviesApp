package com.alykhaled.Movibes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class HomeFragment extends Fragment implements MoviesAdapter.OnItemClickListener,TopMoviesAdapter.OnItemClickListener{
    private String posterID;

    private RecyclerView mTrendingView;
    private RecyclerView mTopView;

    private WebView mTrailer;
    private TextView mTrailerName;

    private RequestQueue mRequestQueue;
    private ArrayList<MovieItem> mTrendingList;
    private MoviesAdapter mTrendingAdapter;

    private ArrayList<MovieItem> mTopList;
    private MoviesAdapter mTopAdapter;
    private ViewPager viewPager;

    private JSONObject publicresponse;

    private ArrayList<MovieItem> mTopFiveMovies;

    private EditText mSearchText ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);

        createView(view);
        trendingList();
        topList();
        return view;
    }

    private void createView(View view) {
        mTrendingList = new ArrayList<>();
        mTopList = new ArrayList<>();
        mTopFiveMovies = new ArrayList<>();

        //Make horizontal layout in RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        TopHorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        //Popular View
        mTrendingView = view.findViewById(R.id.trending_view);
        mTrendingView.setLayoutManager(mLayoutManager);
        mTrendingView.setHasFixedSize(true);
        mTrendingView.setLayoutManager(HorizontalLayout);

        //Top Rated View
        mTopView = view.findViewById(R.id.top_view);
        mTopView.setLayoutManager(mLayoutManager);
        mTopView.setHasFixedSize(true);
        mTopView.setLayoutManager(TopHorizontalLayout);

        //Trailer Video
        mTrailer = view.findViewById(R.id.trailer);
        mTrailer.getSettings().setLoadsImagesAutomatically(true);
        mTrailer.getSettings().setJavaScriptEnabled(true);
        mTrailer.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mTrailerName = view.findViewById(R.id.movieTrailer);
        mTrailerName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent detailIntent = new Intent(getContext(), MovieDetailsActivity.class);
                detailIntent.putExtra("id", posterID);
                detailIntent.putExtra("type","movie");
                startActivity(detailIntent);

            }
        });

        //First five movies in popular view
        viewPager = (ViewPager) view.findViewById(R.id.topMoviesSlider);

        //Search Bar
        mSearchText = view.findViewById(R.id.searchText);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Intent detailIntent = new Intent(getContext(), SearchActitivty.class);
                    String gg = String.valueOf(mSearchText.getText());
                    detailIntent.putExtra("query", gg);
                    detailIntent.putExtra("type","movie");
                    startActivity(detailIntent);
                    return true;
                }
                return false;
            }
        });

        mRequestQueue = Volley.newRequestQueue(getContext());

    }

    private void trailerRandomHit(JSONObject response) {
        try {
            Random rand = new Random();
            JSONArray jsonArray = response.getJSONArray("results");

            //Get random movie
            JSONObject randomhit = jsonArray.getJSONObject(rand.nextInt(jsonArray.length()-1));
            posterID = randomhit.getString("id");

            //Set trailer video to random hit
            trailerVideo(randomhit.getInt("id"),randomhit.getString("title"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void trendingList() {

        String url = "https://api.themoviedb.org/3/movie/popular?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");


                            trailerRandomHit(response);

                            //Add movies to the list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.isNull("release_date") ? "" :  hit.getString("release_date");
                                String moviePoster = hit.isNull("poster_path") ? "" :  "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String id = hit.getString("id");
                                mTrendingList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,""));
                            }

                            //Add first five movies to Slider List
                            for (int i = 0; i < 5; i++)
                            {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.getString("overview");
                                String moviePoster = String.valueOf(hit.getDouble("vote_average"));
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String id = hit.getString("id");
                                mTopFiveMovies.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            //Set top movies slider
                            TopMoviesAdapter viewPagerAdapter = new TopMoviesAdapter(getContext(),mTopFiveMovies);
                            viewPager.setAdapter(viewPagerAdapter);
                            viewPagerAdapter.setOnItemClickListener(HomeFragment.this);

                            //Set top movies list
                            mTrendingAdapter = new MoviesAdapter(getContext(), mTrendingList);
                            mTrendingView.setAdapter(mTrendingAdapter);
                            mTrendingAdapter.setOnItemClickListener(HomeFragment.this);

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

                            //Add movies to the list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                String movieCategory = hit.getString("release_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String id = hit.getString("id");

                                mTopList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            mTopAdapter = new MoviesAdapter(getContext(), mTopList);
                            mTopView.setAdapter(mTopAdapter);
                            mTopAdapter.setOnItemClickListener(HomeFragment.this);

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

    private void trailerVideo(int id, final String name) {
        String url = "https://api.themoviedb.org/3/movie/"+id+"/videos?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US";
        posterID = String.valueOf(id);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            //Check if the current movie has a trailer
                            if (jsonArray.length() == 0)
                            {
                                trailerRandomHit(publicresponse);
                            }
                            else
                            {
                                JSONObject firsthit = jsonArray.getJSONObject(0);
                                String movieTrailer= "https://www.youtube.com/embed/" + firsthit.getString("key") + "?rel=0&modestbranding=1&autohide=1&showinfo=0&controls=0";
                                mTrailer.loadUrl(movieTrailer);
                                mTrailerName.setText(name + " Trailer");
                            }

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
        Intent detailIntent = new Intent(getContext(), MovieDetailsActivity.class);
        MovieItem clickedItem = t.get(position);
        detailIntent.putExtra("id", clickedItem.getId());
        detailIntent.putExtra("type","movie");
        startActivity(detailIntent);
    }
}
