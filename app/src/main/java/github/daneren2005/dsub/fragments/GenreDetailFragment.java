package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.adapter.NotifySongsAdapter;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;
import github.daneren2005.dsub.util.Util;

public class GenreDetailFragment extends NotifyFragment {
    private static final String TAG = GenreDetailFragment.class.getSimpleName();

    public static final int SONGS_DEFAULT_COUNT = 100;
    public static final int SONGS_INDEX_OFFSET = 1;
    private static final String SONG_INDEX_FORMAT = "%03d";

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_genre_page, container, false);
        createNotifyCustomToolbar(true, false,
                false, true, true);

        recyclerView = rootView.findViewById(R.id.genre_page_recycler_view);
        TextView title = rootView.findViewById(R.id.genre_page_title);

        String genreName = getArguments().getString(Constants.PREFERENCES_KEY_SHOW_SONG_BY_GENRE);
        title.setText(genreName);
        loadGenreDetail(genreName);

        return rootView;
    }

    private void loadGenreDetail(String genreName) {
        LoadSongsDataTask<MusicDirectory> loadSongsByGenreTask = new LoadSongsDataTask<>(
                this, ((musicService, listener) -> {
            try {
                return musicService.getSongsByGenre(genreName, SONGS_DEFAULT_COUNT, 0, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadGenreDetail: exception = ", e);
                return null;
            }
        }));

        loadSongsByGenreTask.execute();
    }

    private void updateSongs(List<MusicDirectory.Entry> loadedSongs) {
        if (loadedSongs.isEmpty()) {
            Log.e(TAG, "updateSongs: Loading failed");
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new NotifySongsAdapter() {
            @Override
            public void onBindViewHolder(@NonNull NotifySongsHolder holder, int position) {
                MusicDirectory.Entry song = loadedSongs.get(position);
                holder.indexText.setText(String.format(SONG_INDEX_FORMAT, (position + SONGS_INDEX_OFFSET)));

                holder.songName.setText(song.getTitle());
                holder.artist.setText(song.getArtist());
                holder.duration.setText(Util.formatDuration(song.getDuration()));
                holder.layout.setOnClickListener( view -> {
                    // TODO: Play music from song id
                });

                // TODO: Show now playing icon if song was playing
//                holder.indexText.setVisibility(XXX ? View.GONE : View.VISIBLE);
//                holder.nowPlaying.setVisibility(XXX ? View.VISIBLE : View.GONE);
            }

            @Override
            public int getItemCount() {
                return loadedSongs.size();
            }
        });
    }

    private class LoadSongsDataTask<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;

        LoadSongsDataTask(SubsonicFragment fragment, GetDataListener<T> getDataListener) {
            super(fragment);
            LoadSongsDataTask.this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            MusicDirectory musicDirectory = (MusicDirectory) result;
            updateSongs(musicDirectory.getSongs());
        }
    }

}
