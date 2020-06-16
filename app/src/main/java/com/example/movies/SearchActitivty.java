package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

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
    private EditText mSearchText;


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
        trendingList(intent.getStringExtra("query"),intent.getStringExtra("type"));

        mSearchText = findViewById(R.id.searchTextPage);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                Intent intent = getIntent();
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    trendingList(mSearchText.getText().toString(),intent.getStringExtra("type"));
                    return true;
                }
                return false;
            }
        });

    }

    private void trendingList(String query, final String type) {
        mTrendingList = new ArrayList<>();
        String url = "https://api.themoviedb.org/3/search/"+type+"?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&query="+query+"&page=1&include_adult=true";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray jsonArray = response.getJSONArray("results");

                            //Add movies to the list
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
        detailIntent.putExtra("type","movie");

        startActivity(detailIntent);
    }
}
