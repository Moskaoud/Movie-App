package com.moskaoud.movieapp.popularmovies.utilities;
import android.content.Context;
import android.util.Log;

import com.moskaoud.movieapp.popularmovies.MovieData;
import com.moskaoud.movieapp.popularmovies.MovieReviews;
import com.moskaoud.movieapp.popularmovies.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
public class MovieJsonUtils {
    static MovieData movieDetailsS;
//    static List<MovieTrailer> movieTrailers = new ArrayList<>();//TODO should i add this new ArrayList<>(); ?

    /**
     * @param movieJsonStr JSON response from server
     * @return MovieData of Strings  poster
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<MovieData> getPosterPath(Context context, String movieJsonStr)
            throws JSONException {
          List<MovieData> somePoster = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(movieJsonStr);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int e = 0; e < results.length(); e++) {
                JSONObject dataInResults = results.getJSONObject(e);
                MovieData item = new MovieData();
                item.setPosterPath(dataInResults.getString("poster_path"));
                item.setId(dataInResults.getInt("id"));
                somePoster.add(item);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return somePoster;
    }
    public static MovieData getMovieDetails(Context context, String movieJsonStr)
            throws JSONException {
        try {

            MovieData movieDataDetails = new MovieData();

            JSONObject movieDetails = new JSONObject(movieJsonStr);
            String originalTitle = movieDetails.getString("original_title");
            String posterImageThumbnail = movieDetails.getString("poster_path");
            String overview = movieDetails.getString("overview");
            Number vote_average = movieDetails.getDouble("vote_average");
            String releaseDate = movieDetails.getString("release_date");

            movieDataDetails.setPosterPath(posterImageThumbnail);
            movieDataDetails.setOriginalTitle(originalTitle);
            movieDataDetails.setOverview(overview);
            movieDataDetails.setUserRating(vote_average);
            movieDataDetails.setReleaseDate(releaseDate);

            movieDetailsS = movieDataDetails;


        } catch (Exception exception) {
            exception.printStackTrace();
        }


        return movieDetailsS;
    }
    public static List<MovieTrailer> getMovieTrailer(Context context, String movieJsonStr)
            throws JSONException {
        List<MovieTrailer> movieTrailers = new ArrayList<>();

        try {
            JSONObject results = new JSONObject(movieJsonStr);
            JSONArray result = results.getJSONArray("results");

            Log.w(TAG, "\n\n############## length  = "+result.length() );

            for (int e = 0; e < result.length(); e++) {

                JSONObject dataInResults = result.getJSONObject(e);
                MovieTrailer movieTrailer = new MovieTrailer();
//
//                //TODO should i use it without string get it direct ?
                String trailerId = dataInResults.getString("id");
                String trailerKey = dataInResults.getString("key");
                String trailerName = dataInResults.getString("name");

                movieTrailer.setTrailerId(trailerId);
                movieTrailer.setTrailerKey(trailerKey);
                movieTrailer.setTrailerName(trailerName);

                Log.e(TAG, "\n\n##############"+e+e+e+e+e+e+e+e+
                        "\ntrailerID "+trailerId+"\ntrailerKEY "+trailerKey+
                        "\ntrailerNAME "+trailerName );

                movieTrailers.add(movieTrailer);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return movieTrailers;
    }
    public static List<MovieReviews> getMovieReviews(Context context, String movieJsonStr)
            throws JSONException {
        List<MovieReviews> movieReviews = new ArrayList<>();

        try {
            JSONObject results = new JSONObject(movieJsonStr);
            JSONArray result = results.getJSONArray("results");

            for (int e = 0; e < result.length(); e++) {

                JSONObject dataInResults = result.getJSONObject(e);
                MovieReviews movieReview = new MovieReviews();

                String author = dataInResults.getString("author");
                String content = dataInResults.getString("content");

                movieReview.setmReviewAuthor(author);
                movieReview.setmContent(content);


                Log.e(TAG, "\n\n*******************"+e+e+e+e+e+e+e+e+
                        "\nauthor "+author+"\ncontent "+content+
                        "\n***************************** " );

                movieReviews.add(movieReview);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return movieReviews;
    }

}
