package com.moskaoud.movieapp.popularmovies;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moskaoud.movieapp.popularmovies.data.FavoriteMovieDbHelper;
import com.moskaoud.movieapp.popularmovies.data.MovieContract;
import com.moskaoud.movieapp.popularmovies.utilities.MovieJsonUtils;
import com.moskaoud.movieapp.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

//TODO HAVE I MAKE IT IN FAVORITEMOVIENEW
public class MovieDetail extends AppCompatActivity /*implements LoaderManager.LoaderCallbacks<Cursor>*/{
    private int mMovieId;
    private TextView originalTitle;
    private TextView overview;
    private TextView voteAverage;
    private TextView releaseDate;
    private ImageView thumbnail;
    private ImageView favoriteIcon;//TODO i have to save is this movie favorited or not
    private String TRAILERS="videos";
    private String REVIEWS="reviews";
    private MovieData moviesDetails;
    private List<MovieTrailer> movieTrailers;
    private List<MovieReviews> mMovieReview;
    private SQLiteDatabase mDb;
    private Context mContext;
    //###
    private static final String TAG = MovieDetail.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;


    //
    //private FavoriteMoviesAdapter mFavoriteMovieAdapter; this adapter we use in FavoriteMovieNew Activity
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        originalTitle = (TextView) findViewById(R.id.tv_original_title);
        voteAverage = (TextView) findViewById(R.id.tv_vote_average);
        releaseDate = (TextView) findViewById(R.id.tv_release_date);
        overview = (TextView) findViewById(R.id.tv_overview);
        thumbnail = (ImageView) findViewById(R.id.iv_image_poster);
        favoriteIcon = (ImageView) findViewById(R.id.iv_favorite_icon);
        favoriteIcon.setPadding(34,34,34,34);


    //    RecyclerView favoriteMovieRecyclerView;
  //      favoriteMovieRecyclerView = (RecyclerView) this.findViewById(R.id.rv_favorite_movies);
//        favoriteMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this)); //TODO problem1 is here

        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        //Cursor cursor = getAllFavoriteMovies();// i think i shouldn't do this as no need i have to save and show asterisk only

//        mFavoriteMovieAdapter = new FavoriteMoviesAdapter(this,cursor);
//        favoriteMovieRecyclerView.setAdapter(mFavoriteMovieAdapter);//TODO problem 2 in recycler view

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieId = intentThatStartedThisActivity.getIntExtra(Intent.EXTRA_TEXT, -1);

                if(isFavoriteMovie(mMovieId))
                {
                    favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
                }
                else
                {
                    favoriteIcon.setImageResource(android.R.drawable.btn_star_big_off);
                }

                if (isOnline())
                {
                    new FetchMovieDetails().execute(Integer.toString(mMovieId));

                    favoriteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if(!isFavoriteMovie(mMovieId))
                            {
                                favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
                                moviesDetails.setFavorite(true);

                                addFavoriteMovieUsingContentProvider(originalTitle.getText().toString(),mMovieId);

                                //TODO HOW CAN I GET IT'S NAME TO ADD IT TO FAVORITE LIST
//                                 long mostafa = addFavoriteMovie(originalTitle.getText().toString(),mMovieId);
//                                Log.e("Movie favorite",mostafa+"");
                                //                mFavoriteMovieAdapter.swapCursor(getAllFavoriteMovies());
                            }
                            else
                            {
                                favoriteIcon.setImageResource(android.R.drawable.btn_star_big_off);
                                moviesDetails.setFavorite(false);//
                                //TODO Removing movie
                                 removeFavoriteMovie(mMovieId);

                  //              mFavoriteMovieAdapter.swapCursor(getAllFavoriteMovies());
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }

                if(isOnline())
                {
                    new FetchMovieTrailer().execute(Integer.toString(mMovieId),TRAILERS);
                    new FetchMovieReview().execute(Integer.toString(mMovieId),REVIEWS);
                }
                else
                {
                    Toast.makeText(this, "No Internet Connection To fetch Trailer", Toast.LENGTH_SHORT).show();

                }

            }
        }
    }
    /**
     * Adds a new guest to the mDb including the party count and the current timestamp
     *
     * @param movieName  Movie's name
     * @param movieId Movie ID in party
     * @return id of new record added
     */
    private long addFavoriteMovie(String movieName, int movieId)
    {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieFavorite.COLUMN_MOVIE_NAME,movieName);
        cv.put(MovieContract.MovieFavorite.COLUMN_MOVIE_ID,movieId);
        return mDb.insert(MovieContract.MovieFavorite.TABLE_NAME,null,cv);
    }
    /**
     * Adds a new guest to the mDb including the party count and the current timestamp
     *
     * @param movieName  Movie's name
     * @param movieId Movie ID in party
     * @return id of new record added
     */
    private void addFavoriteMovieUsingContentProvider(String movieName, int movieId)
    {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieFavorite.COLUMN_MOVIE_NAME,movieName);
        cv.put(MovieContract.MovieFavorite.COLUMN_MOVIE_ID,movieId);

        Uri uri = getContentResolver().insert(MovieContract.MovieFavorite.CONTENT_URI,cv);

        if(uri != null){
            Toast.makeText(getBaseContext(),uri.toString(),Toast.LENGTH_LONG).show();
        }
      //  finish();
    }




    private boolean removeFavoriteMovie(int id)
    {
       return mDb.delete(MovieContract.MovieFavorite.TABLE_NAME,
                MovieContract.MovieFavorite.COLUMN_MOVIE_ID+"="+id,null) > 0;
    }
    //TODO LAST PROBLEAM IN DATABASE AND FAVORITE
    private boolean isFavoriteMovie(int id)
    {

        Cursor cursor= mDb.query(
                MovieContract.MovieFavorite.TABLE_NAME,
                null,
                MovieContract.MovieFavorite.COLUMN_MOVIE_ID +" = "+id,
                null,
                null,
                null,
                null
        );
        return cursor.moveToFirst();
    }
//    public boolean isFavoriteMovieNew(int id) {
//        return DatabaseUtils.queryNumEntries(mDb, MovieContract.MovieFavorite.TABLE_NAME,
//                MovieContract.MovieFavorite.COLUMN_MOVIE_ID +" = "+id) > 0;
//    }


//    public boolean isFavoriteMovieNew( int id)
//    {
//        Cursor mContactsCursor = mDb.query(MovieContract.MovieFavorite.TABLE_NAME,
//                "MovieContract.MovieFavorite.COLUMN_MOVIE_ID "+ id+
// null, null, null, null);
//
//        if(mContactsCursor.moveToFirst())
//        {
//            mContactsCursor.close();
//            mDb.close();
//            return true;
//        } else {
//            mContactsCursor.close();
//            mDb.close();
//            return false;
//        }
//    }

    public void setMovieDetails(MovieData movieData) {
        originalTitle.setText(movieData.getOriginalTitle());
        voteAverage.setText(movieData.getUserRating() + "/10");
        releaseDate.setText(movieData.getReleaseDate());
        overview.setText(movieData.getOverview());

        //TODO LAST PROBLEM 2 LAST LAST LAST
//        if(isFavoriteMovie(movieData.getId()))
//        {
//            favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
//
//        }
//        else
//        {
//            favoriteIcon.setImageResource(android.R.drawable.btn_star_big_off);
//
//        }

        String baseImageUrl = "http://image.tmdb.org/t/p/w185/";
        Picasso.get().load(baseImageUrl + movieData.getPosterPath()).into(thumbnail);

    }
    public void setMovieTrailers( List<MovieTrailer>  trailer){
        Log.e(TAG, "\n\n@@@@@@@@@ = $$$ "+trailer.size());
        //TODO [1] try to use MovieDate insteade of MovieTrailer
        //TODO [2] try Use MovieData List isteade of class object  in MovieDetai
    //trailerId.setText(trailer.getId()); Note this error and try to use MovieDate insteade of MovieTrailer [1]
        // and
        LinearLayout my_root = (LinearLayout) findViewById(R.id.ll_detail_base);
        for( int e=0;e<trailer.size();e++)
        {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(17,17,17,17);


            TextView trailerTextView = new TextView(this);
            ImageView trailerImageView = new ImageView(this);

            trailerImageView.setImageResource(R.drawable.clapperboard);
            trailerImageView.setMaxWidth(150);
            trailerImageView.setMaxHeight(150);
            trailerImageView.setAdjustViewBounds(true);
            trailerImageView.setPadding(17,17,17,17);


            trailerTextView.setText("Trailer # "+(e+1)+"\n"+trailer.get(e).getTrailerName());
            trailerTextView.setPadding(17,17,17,17);
            layout.addView(trailerImageView);
            layout.addView(trailerTextView);
            //when you click on image you get a trailer video
            final String tempKeyID = trailer.get(e).getTrailerKey();
            trailerImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo(mContext,tempKeyID);
                }
            });
            //or if you clicked on text you get trailer also
            trailerTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo(mContext,tempKeyID);
                }
            });
            my_root.addView(layout);
        }
    }
    public void setMovieReview(List<MovieReviews> movieReviewW){
        LinearLayout my_root = (LinearLayout) findViewById(R.id.ll_detail_base);
        LinearLayout layout1 = new LinearLayout(this);//(LinearLayout)findViewById(R.id.ll_trailer);
        layout1.setOrientation(LinearLayout.VERTICAL);

        View lineSeparator = new View(this);
        lineSeparator.setBackgroundColor(Color.BLACK);// check it againg if ant error

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
        params.setMargins(0,17,0,17);
        lineSeparator.setLayoutParams(params);

        TextView reviewLabel = new TextView(this);
        reviewLabel.setText(R.string.reviews);
        reviewLabel.setTypeface(reviewLabel.getTypeface(), Typeface.BOLD);
        layout1.setPadding(34,34,34,34);

        layout1.addView(lineSeparator);
        layout1.addView(reviewLabel);
        my_root.addView(layout1);

        for( int e=0;e<movieReviewW.size();e++) {
            LinearLayout layout = new LinearLayout(this);//(LinearLayout)findViewById(R.id.ll_trailer);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(34,34,34,34);



            TextView reviewAuthor =  new TextView(this);
            TextView reviewContent = new TextView(this);

            reviewAuthor.setText(movieReviewW.get(e).getmReviewAuthor());
            reviewAuthor.setTypeface(reviewAuthor.getTypeface(), Typeface.BOLD_ITALIC);
            reviewContent.setText(movieReviewW.get(e).getmContent());

            layout.addView(reviewAuthor);
            layout.addView(reviewContent);
            my_root.addView(layout);
        }
    }
    public void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);

        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public class FetchMovieDetails extends AsyncTask<String, Void, MovieData> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected MovieData doInBackground(String... str) {
            // just send movie id
            URL movieDetailsRequestUrl = NetworkUtils.buildUrl(str[0]);
            try {
                String jsonMovieDetailsResponse = NetworkUtils.getResponseFromHttpUrl(movieDetailsRequestUrl);
                moviesDetails = MovieJsonUtils.getMovieDetails(MovieDetail.this, jsonMovieDetailsResponse);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return moviesDetails;
        }
        @Override
        protected void onPostExecute(MovieData result) {
            setMovieDetails(result);
        }
    }

    public class FetchMovieTrailer extends AsyncTask<String, Void, List<MovieTrailer>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieTrailer> doInBackground(String... str) {
            // just send movie id IS THAT RIGHT
            URL movieDetailsRequestUrl = NetworkUtils.buildUrlForTrailerOrReview(str[0],str[1]);
            try {
                String jsonMovieTrailersResponse = NetworkUtils.getResponseFromHttpUrl(movieDetailsRequestUrl);

                movieTrailers = MovieJsonUtils.getMovieTrailer(MovieDetail.this, jsonMovieTrailersResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return movieTrailers;
        }
        @Override
        protected void onPostExecute(List<MovieTrailer> result) {
            setMovieTrailers(result);
        }
    }
    public class FetchMovieReview extends AsyncTask<String, Void, List<MovieReviews>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<MovieReviews> doInBackground(String... str) {
            // just send movie id IS THAT RIGHT
            URL movieDetailsRequestUrl = NetworkUtils.buildUrlForTrailerOrReview(str[0],str[1]);
            try {
                String jsonMovierReviewResponse = NetworkUtils.getResponseFromHttpUrl(movieDetailsRequestUrl);

                mMovieReview = MovieJsonUtils.getMovieReviews(MovieDetail.this, jsonMovierReviewResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return mMovieReview;
        }
        @Override
        protected void onPostExecute(List<MovieReviews> result) {
            setMovieReview(result);
        }
    }
}

