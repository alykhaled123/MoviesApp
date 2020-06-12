package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

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

public class SearchActitivty extends AppCompatActivity implements MoviesAdapter.OnItemClickListener {

    private RecyclerView mSearchView;

    private RequestQueue mRequestQueue;
    private ArrayList<MovieItem> mTrendingList;
    private MoviesAdapter mTrendingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_actitivty);

        Window window = SearchActitivty.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(SearchActitivty.this, R.color.colorAccent));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout
                = new LinearLayoutManager(
                SearchActitivty.this,
                LinearLayoutManager.HORIZONTAL,
                false);
        TopHorizontalLayout
                = new LinearLayoutManager(
                SearchActitivty.this,
                LinearLayoutManager.HORIZONTAL,
                false);

        final GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mSearchView = findViewById(R.id.search_view);
        mSearchView.setLayoutManager(layoutManager);

        mTrendingList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        trendingList(intent.getStringExtra("query"));

    }

    private void trendingList(String query) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&query="+query+"&page=1&include_adult=true";
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
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String moviePoster = null;

                                if (hit.getString("poster_path" ) == "null")
                                {
                                    moviePoster = "https://www.hertrack.com/wp-content/uploads/2018/10/no-image.jpg";
                                }
                                else
                                {
                                    moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");

                                }
                                String id = hit.getString("id");
                                mTrendingList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            mTrendingAdapter = new MoviesAdapter(SearchActitivty.this, mTrendingList);
                            mSearchView.setAdapter(mTrendingAdapter);
                            mTrendingAdapter.setOnItemClickListener(SearchActitivty.this);

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
