package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class MainPageFragment extends SubsonicFragment {
    private static final String TAG = MainPageFragment.class.getSimpleName();

    public static int SONGS_SIZE = 4;
    public static int ALBUMS_SIZE = 4;
    public static int GENRES_SIZE = 8;
    public static String ALBUM_TYPE = "alphabeticalByName";

    private ImageButton adminSettingsBtn;
    private ImageButton searchBtn;
    private ImageButton radioBtn;

    private List<ImageView> songsImageViews = new ArrayList<>();
    private List<TextView> songsTextViews = new ArrayList<>();

    private List<ImageView> albumsImageViews = new ArrayList<>();
    private List<TextView> albumsTextViews = new ArrayList<>();

    private Button moreGenresButton;
    private List<Button> genreButtons = new ArrayList<>();

    private MusicDirectory songs;
    private MusicDirectory albums;
    private List<Genre> genres = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_main_page, container, false);
        createNotifyCustomToolbar();
        createSongView();
        createAlbumView();
        createGenreView();

        loadData();

        return rootView;
    }

    private void createNotifyCustomToolbar() {
        adminSettingsBtn = rootView.findViewById(R.id.notify_main_page_admin_settings);
        adminSettingsBtn.setOnLongClickListener(v -> {
            // TODO: Show notifySettingsDialog
            Log.d(TAG, "createNotifyCustomToolbar: adminSettingsBtn");
            Toast.makeText(context, "Show admin login", Toast.LENGTH_SHORT).show();
            return true;
        });

        searchBtn = rootView.findViewById(R.id.notify_main_page_search_button);
        searchBtn.setOnClickListener(v -> {
            // TODO: Show notifySearchFragment
            Toast.makeText(context, "Show search fragment", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "createNotifyCustomToolbar: searchBtn");
        });

        radioBtn = rootView.findViewById(R.id.notify_main_page_radio_button);
        radioBtn.setOnClickListener(v -> {
            // TODO: Show notifyRadioFragment
            Toast.makeText(context, "Show radio fragment", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "createNotifyCustomToolbar: radioBtn");
        });
    }

    private void createSongView() {
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView00));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView01));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView02));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView03));

        songsTextViews.add(rootView.findViewById(R.id.main_page_songs_textView00));
        songsTextViews.add(rootView.findViewById(R.id.main_page_songs_textView01));
        songsTextViews.add(rootView.findViewById(R.id.main_page_songs_textView02));
        songsTextViews.add(rootView.findViewById(R.id.main_page_songs_textView03));

        for (ImageView songImageView : songsImageViews) {
            songImageView.setOnClickListener(v -> {
                // TODO: Show song on music player
                Log.d(TAG, "createSongView: Song image clicked!");
            });
        }
    }

    private void createAlbumView() {
        albumsImageViews.add(rootView.findViewById(R.id.main_page_albums_imageView00));
        albumsImageViews.add(rootView.findViewById(R.id.main_page_albums_imageView01));
        albumsImageViews.add(rootView.findViewById(R.id.main_page_albums_imageView02));
        albumsImageViews.add(rootView.findViewById(R.id.main_page_albums_imageView03));

        albumsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView00));
        albumsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView01));
        albumsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView02));
        albumsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView03));

        for (ImageView albumImageView : albumsImageViews) {
            albumImageView.setOnClickListener(v -> {
                // TODO: Show album page
                Log.d(TAG, "createAlbumView: Album image clicked!");
            });
        }
    }

    private void createGenreView() {
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button00));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button01));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button02));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button03));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button04));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button05));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button06));
        genreButtons.add(rootView.findViewById(R.id.main_page_genres_button07));

        moreGenresButton = rootView.findViewById(R.id.main_page_more_genres_button);

        for (Button genreButton : genreButtons) {
            genreButton.setOnClickListener(v -> {
                // TODO: Show genres page
                String genreName = genreButton.getText().toString();
                Log.d(TAG, "createGenreView: Genre Button clicked! " + genreName);
            });
        }
    }

    private void loadData() {
        loadSongs();
        loadAlbums();
        loadGenres();
    }

    private void loadSongs() {
        LoadMainPageDataTask<MusicDirectory> loadSongsTask = new LoadMainPageDataTask<>(
                this,
                MainPageDataType.SONGS,
                (musicService, listener) -> {
                    try {
                        return musicService.getRandomSongs(SONGS_SIZE, null, null,
                                null, null, context, listener);
                    } catch (Exception e) {
                        Log.e(TAG, "loadSongs: exception = " + e);
                        return null;
                    }
                });
        loadSongsTask.execute();
    }

    private void updateSongs(MusicDirectory loadedSongs) {
        if (loadedSongs == null) {
            Log.e(TAG, "updateSongs: Loading failed");
            return;
        }

        songs = loadedSongs;

        int songIdx = 0;
        for (TextView songsTextView : songsTextViews) {
            songsTextView.setText(songs.getSongs().get(songIdx++).getTitle());
        }
    }

    private void loadAlbums() {
        LoadMainPageDataTask<MusicDirectory> loadAlbumsTask = new LoadMainPageDataTask<>(
                this,
                MainPageDataType.ALBUMS,
                ((musicService, listener) -> {
                    try {
                        return musicService.getAlbumList(ALBUM_TYPE, ALBUMS_SIZE,
                                0, false, context, listener);
                    } catch (Exception e) {
                        Log.e(TAG, "loadAlbums: exception = " + e);
                        return null;
                    }
                }));
        loadAlbumsTask.execute();
    }

    private void updateAlbums(MusicDirectory loadedAlbums) {
        if (loadedAlbums == null) {
            Log.e(TAG, "updateAlbums: Loading failed");
            return;
        }

        albums = loadedAlbums;

        int albumIdx = 0;
        for (TextView albumsTextView : albumsTextViews) {
            albumsTextView.setText(albums.getChildren().get(albumIdx++).getTitle());
        }
    }

    private void loadGenres() {
        LoadMainPageDataTask<List<Genre>> loadGenreTask = new LoadMainPageDataTask<>(
                this, MainPageDataType.GENRES,
                (musicService, listener) -> {
                    try {
                        return musicService.getGenres(false, context, listener);
                    } catch (Exception e) {
                        Log.e(TAG, "loadGenres: exception = ", e);
                        return null;
                    }
                });
        loadGenreTask.execute();
    }

    private void updateGenres(List<Genre> loadedGenres) {
        genres.addAll(loadedGenres);

        if (genres.isEmpty()) {
            Log.e(TAG, "updateGenres: Loading failed");
            return;
        }

        List<Genre> shuffledGenres = new ArrayList<>(genres);
        Collections.shuffle(shuffledGenres);

        int genreIdx = 0;
        for (Button genreButton : genreButtons) {
            if (genreIdx < shuffledGenres.size()) {
                genreButton.setText(shuffledGenres.get(genreIdx++).getName());
            } else {
                genreButton.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ((DrawerHider) this.getActivity()).setDrawerEnable(false);
        ((DrawerHider) this.getActivity()).setDrawerTitle("");
    }

    private class LoadMainPageDataTask<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;
        MainPageDataType dataType;

        LoadMainPageDataTask(SubsonicFragment fragment, MainPageDataType mainPageDataType,
                             GetDataListener<T> getDataListener) {
            super(fragment);
            LoadMainPageDataTask.this.getDataListener = getDataListener;
            LoadMainPageDataTask.this.dataType = mainPageDataType;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            switch (dataType) {
                case SONGS:
                    updateSongs((MusicDirectory) result);
                    break;
                case ALBUMS:
                    updateAlbums((MusicDirectory) result);
                    break;
                case GENRES:
                    updateGenres((List<Genre>) result);
                    break;
                default:
                    Log.e(TAG, "done: Error on get data result.");
                    break;
            }
        }
    }

    public enum MainPageDataType {
        SONGS, ALBUMS, GENRES
    }

    public interface DrawerHider {
        // TODO: Move to util
        // Due to SubsonicActivity had called Drawer methods on onPostCreate()
        // To following lifecycle on activity and fragment, invoke those
        // methods on onResume() if necessary.
        void setDrawerEnable(Boolean enable);

        void setDrawerTitle(String title);
    }
}
