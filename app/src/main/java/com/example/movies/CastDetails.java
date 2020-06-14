package com.example.movies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Calendar;

public class CastDetails extends AppCompatActivity implements MoviesAdapter.OnItemClickListener {

    private RequestQueue mRequestQueue;

    private ArrayList<MovieItem> mSimilarList;
    private MoviesAdapter mSimilarAdapter;

    private TextView castName;
    private TextView castOverview;
    private TextView castBirth;
    private TextView castPlace;

    private ImageView moviePoster;
    ViewPager viewPager;
    ArrayList<String> moviePosterUrl;
    private RecyclerView mCastMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_details);
        moviePosterUrl = new ArrayList<>();
        Window window = CastDetails.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(CastDetails.this, R.color.colorAccent));

        castName = (TextView) findViewById(R.id.castname);
        castOverview = (TextView) findViewById(R.id.castdesc);
        moviePoster = (ImageView) findViewById(R.id.cast_pic);
        mCastMovies =findViewById(R.id.cast_movies);
        castBirth = findViewById(R.id.castbirth);
        castPlace = findViewById(R.id.castplace);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CastDetails.this);
        LinearLayoutManager HorizontalLayout;
        HorizontalLayout
                = new LinearLayoutManager(
                CastDetails.this,
                LinearLayoutManager.HORIZONTAL,
                false);


        mCastMovies.setLayoutManager(mLayoutManager);
        mCastMovies.setHasFixedSize(true);
        mCastMovies.setLayoutManager(HorizontalLayout);

        mSimilarList = new ArrayList<>();




        Intent intent = getIntent();
        mRequestQueue = Volley.newRequestQueue(this);
        getDetails(intent.getStringExtra("id"));
        viewPager = (ViewPager) findViewById(R.id.viewPager);


        getCastMovies(intent.getStringExtra("id"));


    }

    private void getDetails(String id) {
        String url = "https://api.themoviedb.org/3/person/"+id+"?api_key=2d3edd3500f7064e849c3694f8e0327c&append_to_response=videos,images";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            castName.setText(response.getString("name"));
                            castOverview.setText(response.getString("biography"));

                            String birth = response.getString("birthday") == "null" ? "" : String.valueOf(Calendar.getInstance().get(Calendar.YEAR)- Integer.parseInt(response.getString("birthday").substring(0, 4)));
                            castBirth.setText(response.getString("birthday") == "null" ? "Birthday: No data available" : "Born: " + response.getString("birthday") + " (" + birth + " years old )" );
                            castPlace.setText(response.getString("place_of_birth") == "null" ? "Place of Birth: No data available" :"Born: " + response.getString("place_of_birth"));
                            JSONObject images = response.getJSONObject("images");
                            JSONArray imagesArray = images.getJSONArray("profiles");

                            for (int i = 0; i < imagesArray.length(); i++)
                            {
                                JSONObject hit = imagesArray.getJSONObject(i);
                                moviePosterUrl.add("https://image.tmdb.org/t/p/original" + hit.getString("file_path")) ;
                            }
                            ImageSliderAdapter viewPagerAdapter = new ImageSliderAdapter(CastDetails.this,moviePosterUrl);

                            viewPager.setAdapter(viewPagerAdapter);


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

    private void getCastMovies(String id) {
        String url = "https://api.themoviedb.org/3/person/"+id+"/movie_credits?api_key=2d3edd3500f7064e849c3694f8e0327c&language=en-US\n";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("cast");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);
                                String movieName = hit.getString("title");
                                //String movieCategory = hit.getString("release_date");
                                String moviePoster = "https://image.tmdb.org/t/p/original" + hit.getString("poster_path");
                                //String movieBack= "https://image.tmdb.org/t/p/original" + hit.getString("backdrop_path");
                                String id = hit.getString("id");

                                mSimilarList.add(new MovieItem(id,movieName ,"", moviePoster,""));
                            }
                            mSimilarAdapter = new MoviesAdapter(CastDetails.this, mSimilarList);
                            mCastMovies.setAdapter(mSimilarAdapter);
                            mSimilarAdapter.setOnItemClickListener(CastDetails.this);
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
