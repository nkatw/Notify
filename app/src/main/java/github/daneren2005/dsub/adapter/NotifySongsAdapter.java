package github.daneren2005.dsub.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.DownloadService;

public class NotifySongsAdapter extends RecyclerView.Adapter<NotifySongsAdapter.NotifySongsHolder> {
    private static final String TAG = NotifySongsAdapter.class.getSimpleName();
    private CopyOnWriteArrayList<NotifySongsHolder> songsHolderList = new CopyOnWriteArrayList<>();
    private Thread updateThread;
    private Handler uiHandler;
    private Runnable runnable = () -> {
        // TODO: Replace with Broadcast
        while (!Thread.currentThread().isInterrupted()) {
            for (NotifySongsHolder holder : songsHolderList) {
                boolean isPlaying = DownloadService.getInstance().isPlayingSong(holder.getSong());
                if (isPlaying && !holder.isPlaying()) {
                    holder.setPlaying(true);
                    reloadPlayingStatus(holder, true);
                } else if (!isPlaying && holder.isPlaying()) {
                    holder.setPlaying(false);
                    reloadPlayingStatus(holder, false);
                }
            }
        }
    };

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

    public void startCheckPlaying() {
        uiHandler = new Handler();
        updateThread = new Thread(runnable);
        updateThread.start();
    }

    public void stopCheckPlaying() {
        uiHandler.removeCallbacks(runnable);
        updateThread.interrupt();
    }

    public void addHolderToCheck(NotifySongsHolder holder) {
        songsHolderList.add(holder);
    }

    private synchronized void reloadPlayingStatus(RecyclerView.ViewHolder holder, boolean isPlaying) {
        if (uiHandler != null) {
            uiHandler.post(() -> {
                ((NotifySongsHolder) holder).indexText.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
                ((NotifySongsHolder) holder).nowPlaying.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
            });
        }
    }

    protected class NotifySongsHolder extends RecyclerView.ViewHolder {
        public ViewGroup layout;
        public TextView indexText;
        public ImageView nowPlaying;
        public TextView songName;
        public TextView artist;
        public TextView duration;
        private MusicDirectory.Entry song;
        private AtomicBoolean playing = new AtomicBoolean(false);

        NotifySongsHolder(View itemView) {
            super(itemView);
            NotifySongsHolder.this.layout = itemView.findViewById(R.id.notify_song_item_layout);
            indexText = itemView.findViewById(R.id.notify_song_item_index);
            nowPlaying = itemView.findViewById(R.id.notify_song_item_nowPlaying_img);
            songName = itemView.findViewById(R.id.notify_song_item_song_name);
            artist = itemView.findViewById(R.id.notify_song_item_artist);
            duration = itemView.findViewById(R.id.notify_song_item_duration);
        }

        public MusicDirectory.Entry getSong() {
            return song;
        }

        public void setSong(MusicDirectory.Entry song) {
            this.song = song;
        }

        public boolean isPlaying() {
            return playing.get();
        }

        public void setPlaying(boolean playing) {
            this.playing.set(playing);
        }
    }
}
