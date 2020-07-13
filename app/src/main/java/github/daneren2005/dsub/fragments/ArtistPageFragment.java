package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.ArtistInfo;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class ArtistPageFragment extends NotifyFragment {
    private static final String TAG = ArtistPageFragment.class.getSimpleName();

    private String artistId, artistName;

    private TextView artistNameTxtView, artistBiographyTxtView;
    private ImageView artistImage;
    private List<ArtistPageItem> albums = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_artist_page, container, false);

        // TODO: Get artist id and artist name from bundle
        artistId = getArguments().getString(Constants.INTENT_EXTRA_NAME_ID);
        artistName = getArguments().getString(Constants.INTENT_EXTRA_NAME_NAME);

        createNotifyCustomToolbar(true, false,
                false, true, true);

        createArtistInfo();
        createAlbums();

        loadArtistInfo();
        loadArtist();

        return rootView;
    }

    private void createArtistInfo() {
        artistNameTxtView = rootView.findViewById(R.id.artist_page_artist_name);
        artistBiographyTxtView = rootView.findViewById(R.id.artist_page_artist_biography);
        artistImage = rootView.findViewById(R.id.artist_page_artist_image);
    }

    private void createAlbums() {
        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout00),
                rootView.findViewById(R.id.artist_page_album_item_coverArt00),
                rootView.findViewById(R.id.artist_page_album_item_name00),
                rootView.findViewById(R.id.artist_page_album_item_year00)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout01),
                rootView.findViewById(R.id.artist_page_album_item_coverArt01),
                rootView.findViewById(R.id.artist_page_album_item_name01),
                rootView.findViewById(R.id.artist_page_album_item_year01)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout02),
                rootView.findViewById(R.id.artist_page_album_item_coverArt02),
                rootView.findViewById(R.id.artist_page_album_item_name02),
                rootView.findViewById(R.id.artist_page_album_item_year02)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout03),
                rootView.findViewById(R.id.artist_page_album_item_coverArt03),
                rootView.findViewById(R.id.artist_page_album_item_name03),
                rootView.findViewById(R.id.artist_page_album_item_year03)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout04),
                rootView.findViewById(R.id.artist_page_album_item_coverArt04),
                rootView.findViewById(R.id.artist_page_album_item_name04),
                rootView.findViewById(R.id.artist_page_album_item_year04)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout05),
                rootView.findViewById(R.id.artist_page_album_item_coverArt05),
                rootView.findViewById(R.id.artist_page_album_item_name05),
                rootView.findViewById(R.id.artist_page_album_item_year05)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout06),
                rootView.findViewById(R.id.artist_page_album_item_coverArt06),
                rootView.findViewById(R.id.artist_page_album_item_name06),
                rootView.findViewById(R.id.artist_page_album_item_year06)
        ));

        albums.add(new ArtistPageItem(
                rootView.findViewById(R.id.artist_page_album_item_layout07),
                rootView.findViewById(R.id.artist_page_album_item_coverArt07),
                rootView.findViewById(R.id.artist_page_album_item_name07),
                rootView.findViewById(R.id.artist_page_album_item_year07)
        ));

        for (ArtistPageItem item : albums) {
            item.layout.setOnClickListener(view -> {
                // TODO: Show album page
                Toast.makeText(context, "Artist item click!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void loadArtistInfo() {
        LoadArtistPageDataTask<ArtistInfo> loadArtistInfoTask = new LoadArtistPageDataTask<>(
                this, (musicService, listener) -> {
            try {
                return musicService.getArtistInfo(
                        artistId, false, true, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadArtistInfo: exception = " + e);
                return null;
            }
        });
        loadArtistInfoTask.execute();
    }

    private void updateArtistInfo(ArtistInfo loadedArtistInfo) {
        if (loadedArtistInfo == null) {
            Log.e(TAG, "updateArtistInfo: Loading failed");
            return;
        }
        artistNameTxtView.setText(artistName);
        artistBiographyTxtView.setText(loadedArtistInfo.getBiography());
        getImageLoader().loadImage(artistImage, loadedArtistInfo.getImageUrl(), false);

        // TODO: Update data on album item
    }

    private void loadArtist() {
        LoadArtistPageDataTask<MusicDirectory> loadAlbumsTask = new LoadArtistPageDataTask<>(
                this, (musicService, listener) -> {
            try {
                return musicService.getArtist(artistId, artistName, false, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadArtist: exception = " + e);
                return null;
            }
        });
        loadAlbumsTask.execute();
    }

    private void updateArtist(MusicDirectory loadedMusicDirectory) {
        List<MusicDirectory.Entry> shuffledAlbums = new ArrayList<>(loadedMusicDirectory.getChildren());
        Collections.shuffle(shuffledAlbums);

        int albumIdx = 0;
        for (ArtistPageItem item : this.albums) {
            if (albumIdx < shuffledAlbums.size()) {
                item.name.setText(shuffledAlbums.get(albumIdx).getTitle());
                Integer year = shuffledAlbums.get(albumIdx).getYear();
                item.year.setText(year != null ? year.toString() : "");
                getImageLoader().loadImage(item.coverArt, shuffledAlbums.get(albumIdx++),
                        false, false);
            } else {
                item.layout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class LoadArtistPageDataTask<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;

        LoadArtistPageDataTask(SubsonicFragment fragment,
                               GetDataListener<T> getDataListener) {
            super(fragment);
            LoadArtistPageDataTask.this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            if (result instanceof ArtistInfo) {
                updateArtistInfo((ArtistInfo) result);
            } else if (result instanceof MusicDirectory) {
                updateArtist((MusicDirectory) result);
            }
        }
    }

    private class ArtistPageItem {
        View layout;
        ImageView coverArt;
        TextView name, year;

        ArtistPageItem(View layout, ImageView coverArt, TextView name, TextView year) {
            this.layout = layout;
            this.coverArt = coverArt;
            this.name = name;
            this.year = year;
        }
    }
}
