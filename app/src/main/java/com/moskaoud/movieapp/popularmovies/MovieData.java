package com.moskaoud.movieapp.popularmovies;

public class MovieData {

    private String originalTitle;
    private String posterPath;
    private String overview;
    private String releaseDate;
    private Number userRating;
    private int id;
    private boolean isFavorite;

    public MovieData(String posterPath) {
        this.posterPath = posterPath;

    }

    public MovieData() {

    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Number getUserRating() {
        return userRating;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setUserRating(Number userRating) {
        this.userRating = userRating;
    }
}
