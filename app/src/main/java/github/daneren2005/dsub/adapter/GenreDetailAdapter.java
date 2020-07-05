package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import github.daneren2005.dsub.R;

public class GenreDetailAdapter extends RecyclerView.Adapter<GenreDetailAdapter.GenreDetailHolder> {
    private static final String TAG = GenreDetailAdapter.class.getSimpleName();

    @NonNull
    @Override
    public GenreDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.genre_detail_item_view, parent, false);
        return new GenreDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreDetailHolder holder, int position) {
        // Override it on instantiation.
    }

    @Override
    public int getItemCount() {
        // Override it on instantiation.
        return 0;
    }

    protected class GenreDetailHolder extends RecyclerView.ViewHolder {
        public ViewGroup layout;
        public TextView indexText;
        public ImageView nowPlaying;
        public TextView songName;
        public TextView artist;
        public TextView duration;

        GenreDetailHolder(View itemView) {
            super(itemView);
            GenreDetailHolder.this.layout = itemView.findViewById(R.id.genre_detail_page_item_layout);
            indexText = itemView.findViewById(R.id.genre_detail_page_item_index_text);
            nowPlaying = itemView.findViewById(R.id.genre_detail_page_item_nowPlaying);
            songName = itemView.findViewById(R.id.genre_detail_page_item_song_name);
            artist = itemView.findViewById(R.id.genre_detail_page_item_artist);
            duration = itemView.findViewById(R.id.genre_detail_page_item_song_duration);
        }
    }
}
