package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import github.daneren2005.dsub.R;

public class GenreListAdapter extends RecyclerView.Adapter<GenreListAdapter.GenreListViewHolder> {
    private static final String TAG = GenreListAdapter.class.getSimpleName();

    @NonNull
    @Override
    public GenreListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notify_genre_list_item_view, parent, false);
        return new GenreListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreListViewHolder holder, int position) {
        // Override it on instantiation.
    }

    @Override
    public int getItemCount() {
        // Override it on instantiation.
        return 0;
    }

    protected class GenreListViewHolder extends RecyclerView.ViewHolder {
        // TODO: Find better way to encapsulate members
        public ViewGroup layout;
        public TextView genreNameTextView;

        GenreListViewHolder(View itemView) {
            super(itemView);
            GenreListViewHolder.this.genreNameTextView = itemView.findViewById(R.id.genre_name);
            GenreListViewHolder.this.layout = itemView.findViewById(R.id.genre_list_item_layout);
        }
    }
}
