package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import github.daneren2005.dsub.R;

public class NotifySongsAdapter extends RecyclerView.Adapter<NotifySongsAdapter.NotifySongsHolder> {
    private static final String TAG = NotifySongsAdapter.class.getSimpleName();

    @NonNull
    @Override
    public NotifySongsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.notify_song_item_view, parent, false);
        return new NotifySongsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifySongsHolder holder, int position) {
        // Override it on instantiation.
    }

    @Override
    public int getItemCount() {
        // Override it on instantiation.
        return 0;
    }

    protected class NotifySongsHolder extends RecyclerView.ViewHolder {
        public ViewGroup layout;
        public TextView indexText;
        public ImageView nowPlaying;
        public TextView songName;
        public TextView artist;
        public TextView duration;

        NotifySongsHolder(View itemView) {
            super(itemView);
            NotifySongsHolder.this.layout = itemView.findViewById(R.id.notify_song_item_layout);
            indexText = itemView.findViewById(R.id.notify_song_item_index);
            nowPlaying = itemView.findViewById(R.id.notify_song_item_nowPlaying_img);
            songName = itemView.findViewById(R.id.notify_song_item_song_name);
            artist = itemView.findViewById(R.id.notify_song_item_artist);
            duration = itemView.findViewById(R.id.notify_song_item_duration);
        }
    }
}
