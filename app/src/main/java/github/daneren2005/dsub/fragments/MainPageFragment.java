package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.dialogFragment.AdminLoginDialogFragment;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.DrawerHider;
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


    private List<MainPageItem> songItems = new ArrayList<>();
    private List<MainPageItem> albumItems = new ArrayList<>();
    private List<MainPageItem> genresItems = new ArrayList<>();

    private ImageButton moreGenresButton;
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
            AdminLoginDialogFragment adminLoginDialogFragment = new AdminLoginDialogFragment();
            adminLoginDialogFragment.show(context.getSupportFragmentManager(), "NotifyAdminLogin");
            Log.d(TAG, "createNotifyCustomToolbar: adminSettingsBtn");
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
        songItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_songs_imageView00),
                rootView.findViewById(R.id.main_page_songs_textView00)
        ));
        songItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_songs_imageView01),
                rootView.findViewById(R.id.main_page_songs_textView01)
        ));
        songItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_songs_imageView02),
                rootView.findViewById(R.id.main_page_songs_textView02)
        ));
        songItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_songs_imageView03),
                rootView.findViewById(R.id.main_page_songs_textView03)
        ));

        for (MainPageItem item : songItems) {
            item.coverArt.setOnClickListener(v -> {
                // TODO: Show song on music player
                Toast.makeText(context, "Song image clicked!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void createAlbumView() {
        albumItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_albums_imageView00),
                rootView.findViewById(R.id.main_page_albums_textView00)
        ));
        albumItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_albums_imageView01),
                rootView.findViewById(R.id.main_page_albums_textView01)
        ));
        albumItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_albums_imageView02),
                rootView.findViewById(R.id.main_page_albums_textView02)
        ));
        albumItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_albums_imageView03),
                rootView.findViewById(R.id.main_page_albums_textView03)
        ));

        for (MainPageItem item : albumItems) {
            item.coverArt.setOnClickListener(v -> {
                // TODO: Show album page
                Toast.makeText(context, "Album image clicked!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void createGenreView() {
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView00),
                rootView.findViewById(R.id.main_page_genres_textView00)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView01),
                rootView.findViewById(R.id.main_page_genres_textView01)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView02),
                rootView.findViewById(R.id.main_page_genres_textView02)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView03),
                rootView.findViewById(R.id.main_page_genres_textView03)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView04),
                rootView.findViewById(R.id.main_page_genres_textView04)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView05),
                rootView.findViewById(R.id.main_page_genres_textView05)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView06),
                rootView.findViewById(R.id.main_page_genres_textView06)
        ));
        genresItems.add(new MainPageItem(
                rootView.findViewById(R.id.main_page_genres_imageView07),
                rootView.findViewById(R.id.main_page_genres_textView07)
        ));

        for (MainPageItem item : genresItems) {
            item.coverArt.setOnClickListener(v -> {
                // TODO: Show genres page
                String genreName = item.title.getText().toString();
                Log.d(TAG, "createGenreView: Genre Button clicked! " + genreName);
                Toast.makeText(context, genreName + " clicked!", Toast.LENGTH_SHORT).show();
            });
        }

        moreGenresButton = rootView.findViewById(R.id.main_page_more_genres_button);
        moreGenresButton.setOnClickListener(v -> {
            Toast.makeText(context, "More genres button clicked!", Toast.LENGTH_SHORT).show();
        });
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

        int songIdx = 0;
        for (MainPageItem item : songItems) {
            item.title.setText(loadedSongs.getSongs().get(songIdx).getTitle());
            item.musicData = loadedSongs.getChildren().get(songIdx);
            context.getImageLoader().loadImage(item.coverArt,
                    loadedSongs.getSongs().get(songIdx++), false, false);
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

        int albumIdx = 0;
        for (MainPageItem item : albumItems) {
            item.title.setText(loadedAlbums.getChildren().get(albumIdx).getTitle());
            item.musicData = loadedAlbums.getChildren().get(albumIdx);
            context.getImageLoader().loadImage(item.coverArt,
                    loadedAlbums.getChildren().get(albumIdx++), false, false);
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
        for (MainPageItem item : genresItems) {
            if (genreIdx < shuffledGenres.size()) {
                item.title.setText(shuffledGenres.get(genreIdx++).getName());
            } else {
                item.title.setVisibility(View.INVISIBLE);
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

    private class MainPageItem {
        ImageView coverArt;
        TextView title;
        Object musicData;

        MainPageItem(ImageView coverArt, TextView title) {
            this.coverArt = coverArt;
            this.title = title;
        }
    }

    public enum MainPageDataType {
        SONGS, ALBUMS, GENRES
    }
}
