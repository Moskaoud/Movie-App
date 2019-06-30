package com.moskaoud.movieapp.popularmovies;

public class MovieTrailer {

    private String trailerId;
    private String trailerKey;
    private String trailerName;
    public MovieTrailer(){}
    public String getTrailerId() {
        return trailerId;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }
}
