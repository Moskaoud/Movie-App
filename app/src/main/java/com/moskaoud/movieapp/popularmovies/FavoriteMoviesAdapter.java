package com.moskaoud.movieapp.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moskaoud.movieapp.popularmovies.data.MovieContract;

public class FavoriteMoviesAdapter extends RecyclerView
.Adapter<FavoriteMoviesAdapter.FavoriteViewHolder>{
    private Context mContext;
    private Cursor mCursor;

    public FavoriteMoviesAdapter(Context mContext)
    {
        this.mContext = mContext;
    }
    public FavoriteMoviesAdapter(Context context,Cursor cursor)
    {
        this.mContext = context;
        this.mCursor = cursor;
    }
    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_of_favorite_movies,parent,false);
        return new FavoriteViewHolder(view);
    }
    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position))
            return;
        String movieName = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieFavorite.COLUMN_MOVIE_NAME));
        int movieId = mCursor.getInt(mCursor.getColumnIndex(MovieContract.MovieFavorite.COLUMN_MOVIE_ID));
        //long id = mCursor.getLong(mCursor.getColumnIndex(MovieContract.MovieFavorite._ID));

        holder.movieNameTextView.setText(movieName);
        holder.movieIdTextView.setText(String.valueOf(movieId));
        holder.itemView.setTag(movieId);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
//      Inner class to hold the views needed to display a single item in the recycler-view
    class FavoriteViewHolder extends RecyclerView.ViewHolder{
        TextView movieNameTextView;
        TextView movieIdTextView;
        public FavoriteViewHolder(View itemView){
            super(itemView);

            movieNameTextView = (TextView) itemView.findViewById(R.id.tv_favorite_movie_name);
            movieIdTextView = (TextView) itemView.findViewById(R.id.tv_favorite_movie_id);
        }
    }
}
