package com.moskaoud.movieapp.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moskaoud.movieapp.popularmovies.utilities.MovieJsonUtils;
import com.moskaoud.movieapp.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private String SORT_BY_POPULAR = "popular";
    private String SORT_BY_TOP_RATED = "top_rated";

//TODO DON'NT FORGET TO REMOVE HARDCODED
    //IMPORTANT: Make sure not to forget to move all the hardcoded
    // Strings in your project to the strings.xml file. If you are unsure how to do this,
    // revisit this video from Advanced Android Apps.
    // https://www.youtube.com/watch?time_continue=3&v=PRVXiM7v3Ds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        int numberOfColumns = calculateNoOfColumns(this);
        Log.e("MainActivity ", numberOfColumns + "  column");
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, MainActivity.this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //mInternetConnection =(TextView)




        if (isOnline()) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String sortBy = sharedPref.getString("SORT_CRITERION_KEY", SORT_BY_POPULAR);
            loadMovieData(sortBy);

        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }

    // to make default load popular even if app closed top_rated
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("SORT_CRITERION_KEY", SORT_BY_POPULAR);
        editor.commit();

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if (noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    private void loadMovieData(String sortBy) {

        if(isOnline())
        {
            new FetchMovieTask().execute(sortBy);
        }
        else
        {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movies The weather for the day that was clicked
     */
    @Override
    public void onClick(MovieData movies) {
        Context context = this;
        Class destinationClass = MovieDetail.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movies.getId());
        startActivity(intentToStartDetailActivity);
    }

    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    /*
    public class CustomImageView extends AppCompatImageView {
        public CustomImageView(Context context) {
            super(context);
        }

        public CustomImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }



        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            setMeasuredDimension(getMeasuredWidth(),(((getMeasuredWidth()*3)/2)));
        }
    }*/

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

// for what

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movies, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.most_popular) {

            if (isOnline()) {
                {
                    loadMovieData(SORT_BY_POPULAR);
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("SORT_CRITERION_KEY", SORT_BY_POPULAR);
                    editor.commit();
                }

            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }

            return true;
        }


        if (id == R.id.top_rated) {
            if (isOnline()) {
                loadMovieData(SORT_BY_TOP_RATED);

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("SORT_CRITERION_KEY", SORT_BY_TOP_RATED);
                editor.commit();

            } else {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
            return true;
        }

        if (id == R.id.favorite_movies) {
            //Toast.makeText(this, "Favorite Movies Clicked", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,FavoriteMoviesNew.class));



        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, List<MovieData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<MovieData> doInBackground(String... str) {

            URL movieRequestUrl = NetworkUtils.buildUrl(str[0]);
            List<MovieData> moviesPosters;
            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                moviesPosters = MovieJsonUtils.getPosterPath(MainActivity.this, jsonMovieResponse);


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return moviesPosters;

        }

        @Override
        protected void onPostExecute(List<MovieData> result) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (result.size() > 0) {
                showMovieDataView();
                mMovieAdapter.setMovieData(result);
            } else {
                showErrorMessage();
            }
        }
    }
}
