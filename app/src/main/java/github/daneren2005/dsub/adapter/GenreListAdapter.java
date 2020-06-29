package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.Genre;

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreListViewHolder> {
    private static final String TAG = GenreListAdapter.class.getSimpleName();
    private List<Genre> genres = new ArrayList<>();

    public GenreListAdapter(List<Genre> genres) {
        this.genres.addAll(genres);
    }

    @NonNull
    @Override
    public GenreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.genre_list_item_view, parent, false);
        return new GenreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreListViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genreNameTextView.setText(genre.getName());
        holder.layout.setOnClickListener(v -> {
            // TODO: show genre detail page
            Log.d(TAG, "onBindViewHolder: genre name = " + genre.getName());
        });
    }

    @Override
    public int getItemCount() {
        return this.genres.size();
    }

    public class GenreListViewHolder extends RecyclerView.ViewHolder {
        ViewGroup layout;
        TextView genreNameTextView;

        public GenreListViewHolder(View itemView) {
            super(itemView);
            GenreListViewHolder.this.genreNameTextView = itemView.findViewById(R.id.genre_name);
            GenreListViewHolder.this.layout = itemView.findViewById(R.id.genre_list_item_layout);
        }
    }
}
