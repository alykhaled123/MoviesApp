package com.example.movies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.system.ErrnoException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Random;

public class TvFragment extends Fragment implements MoviesAdapter.OnItemClickListener{
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

    private EditText mSearchText ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tv_fragment,container,false);
        mTrendingList = new ArrayList<>();
        mTopList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager HorizontalLayout;
        LinearLayoutManager TopHorizontalLayout;
        HorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        TopHorizontalLayout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);

        mTrendingView = view.findViewById(R.id.TvTrending_view);
        mTrendingView.setLayoutManager(mLayoutManager);
        mTrendingView.setHasFixedSize(true);
        mTrendingView.setLayoutManager(HorizontalLayout);

        mTopView = view.findViewById(R.id.ShowTop_view);
        mTopView.setLayoutManager(mLayoutManager);
        mTopView.setHasFixedSize(true);
        mTopView.setLayoutManager(TopHorizontalLayout);

        mFirstImage = view.findViewById(R.id.TvBack);
        mTopMovieName = view.findViewById(R.id.topShow_name);
        mTopMovieOverview = view.findViewById(R.id.topShow_overview);

        mTrailer = view.findViewById(R.id.ShowTrailer);
        mTrailer.getSettings().setLoadsImagesAutomatically(true);
        mTrailer.getSettings().setJavaScriptEnabled(true);
        mTrailer.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        mTrailerName = view.findViewById(R.id.TvTrailer);

        mRequestQueue = Volley.newRequestQueue(getContext());

        mMoviePoster = (FrameLayout) view.findViewById(R.id.topShow_poster);
        mMoviePoster.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent detailIntent = new Intent(getContext(), MovieDetailsActivity.class);
                detailIntent.putExtra("id", posterID);
                detailIntent.putExtra("type","tv");
                startActivity(detailIntent);

            }
        });

        mSearchText = view.findViewById(R.id.TvSearchText);
        mSearchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    Intent detailIntent = new Intent(getContext(), SearchActitivty.class);
                    String gg = String.valueOf(mSearchText.getText());
                    detailIntent.putExtra("query", gg);
                    detailIntent.putExtra("type","tv");
                    startActivity(detailIntent);
                    return true;
                }
                return false;
            }
        });

        trendingList();
        topList();
        return view;
    }
    private void trendingList() {
        String url = "https://api.themoviedb.org/3/tv/popular?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
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

                            mTopMovieName.setText(randomhit.getString("name"));
                            mTopMovieOverview.setText(randomhit.getString("overview"));

                            //Set trailer video to random hit
                            trailerVideo(randomhit.getInt("id"),randomhit.getString("name"));

                            //Add movies to the list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("name");
                                String movieCategory = hit.getString("first_air_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String id = hit.getString("id");
                                mTrendingList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }

                            mTrendingAdapter = new MoviesAdapter(getContext(), mTrendingList);
                            mTrendingView.setAdapter(mTrendingAdapter);
                            mTrendingAdapter.setOnItemClickListener(TvFragment.this);

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
        String url = "https://api.themoviedb.org/3/tv/top_rated?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US&page=1";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("name");
                                String movieCategory = hit.getString("first_air_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String id = hit.getString("id");

                                mTopList.add(new MovieItem(id,movieName ,movieCategory, moviePoster,movieBack));
                            }
                            mTopAdapter = new MoviesAdapter(getContext(), mTopList);
                            mTopView.setAdapter(mTopAdapter);
                            mTopAdapter.setOnItemClickListener(TvFragment.this);

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
        String url = "https://api.themoviedb.org/3/tv/"+id+"/videos?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US";
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
        Intent detailIntent = new Intent(getContext(), MovieDetailsActivity.class);
        MovieItem clickedItem = t.get(position);
        detailIntent.putExtra("id", clickedItem.getId());
        detailIntent.putExtra("type", "tv");
        startActivity(detailIntent);
    }
}
