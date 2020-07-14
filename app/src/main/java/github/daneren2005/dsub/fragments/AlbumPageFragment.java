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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class AlbumPageFragment extends NotifyFragment {
    private static final String TAG = AlbumPageFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private String albumId, albumName;
    private TextView albumTitle, albumSubtitle;
    private ImageView albumImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_album_page, container, false);

        albumId = getArguments().getString(Constants.INTENT_EXTRA_NAME_ID);
        albumName = getArguments().getString(Constants.INTENT_EXTRA_NAME_NAME);

        createNotifyCustomToolbar(true, false,
                false, true, true);

        albumTitle = rootView.findViewById(R.id.album_page_title);
        albumSubtitle = rootView.findViewById(R.id.album_page_subtitle);
        albumImage = rootView.findViewById(R.id.album_page_album_image);
        recyclerView = rootView.findViewById(R.id.album_page_recycler_view);

        loadAlbum();

        return rootView;
    }

    private void loadAlbum() {
        new LoadAlbumDataTask<>(this, ((musicService, listener) -> {
            try {
                return musicService.getMusicDirectory(albumId, albumName, false, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadAlbum: exception = " + e);
                return null;
            }
        })).execute();
    }

    private void updateSongsOnAlbum(MusicDirectory loadedAlbum) {
        if (loadedAlbum == null) {
            Log.e(TAG, "updateSongsOnAlbum: Loading failed");
            return;
        }

        albumSubtitle.setText(loadedAlbum.getName());

        // Get album year and image from song
        List<MusicDirectory.Entry> songs = loadedAlbum.getSongs();
        Integer year = songs.get(0).getYear();
        albumTitle.setText(year != null ? String.valueOf(year) : "");
        getImageLoader().loadImage(albumImage, songs.get(0), false, false);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // TODO: add adapter
    }

    private class LoadAlbumDataTask<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;

        LoadAlbumDataTask(SubsonicFragment fragment, GetDataListener<T> getDataListener) {
            super(fragment);
            LoadAlbumDataTask.this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            updateSongsOnAlbum((MusicDirectory) result);
        }
    }
}
