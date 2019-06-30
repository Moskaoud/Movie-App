package com.moskaoud.movieapp.popularmovies.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.moskaoud.movieapp.popularmovies.data.MovieContract.*;
public class FavoriteMovieDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "favoriteMovies.db";
    public static final int DATABASE_VERSION = 2;
    public FavoriteMovieDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORTE_MOVIE_TABLE = "CREATE TABLE "+
                MovieFavorite.TABLE_NAME + " ("+
                MovieFavorite.COLUMN_MOVIE_ID +" INTEGER PRIMARY KEY ,"+// should i make it with movie id ?!
                MovieFavorite.COLUMN_MOVIE_NAME + " TEXT NOT NULL"+
                ");";
        db.execSQL(SQL_CREATE_FAVORTE_MOVIE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MovieFavorite.TABLE_NAME);
        onCreate(db);
    }
}
