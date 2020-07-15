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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.DrawerHider;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class MainPageFragment extends NotifyFragment {
    private static final String TAG = MainPageFragment.class.getSimpleName();

    public static int SONGS_QUANTITY = 4;
    public static int ALBUMS_QUANTITY = 4;
    public static int GENRES_QUANTITY = 8;
    public static String ALBUM_TYPE = "alphabeticalByName";


    private List<MainPageItem> songItems = new ArrayList<>();
    private List<MainPageItem> albumItems = new ArrayList<>();
    private List<MainPageItem> genresItems = new ArrayList<>();

    private ImageButton moreGenresButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_main_page, container, false);

        createNotifyCustomToolbar(false, false,
                true, true, true);
        createSongView();
        createAlbumView();
        createGenreView();

        loadData();

        return rootView;
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
                showPlayerPage();
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
                MusicDirectory.Entry albumListData = (MusicDirectory.Entry) item.detail;
                showAlbumPage(albumListData.getId(), albumListData.getAlbum());
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
                showGenreDetailPage((Genre) item.detail);
            });
        }

        moreGenresButton = rootView.findViewById(R.id.main_page_more_genres_button);
        moreGenresButton.setOnClickListener(v -> {
            replaceFragment(new GenreListFragment());
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
                        return musicService.getRandomSongs(SONGS_QUANTITY, null, null,
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
            item.detail = loadedSongs.getChildren().get(songIdx);
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
                        return musicService.getAlbumList(ALBUM_TYPE, ALBUMS_QUANTITY,
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
        List<MusicDirectory.Entry> albums = loadedAlbums.getChildren(true, false);
        for (MainPageItem item : albumItems) {
            item.title.setText(albums.get(albumIdx).getTitle());
            item.detail = albums.get(albumIdx);
            context.getImageLoader().loadImage(item.coverArt,
                    albums.get(albumIdx++), false, false);
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
        if (loadedGenres.isEmpty()) {
            Log.e(TAG, "updateGenres: Loading failed");
            return;
        }

        List<Genre> shuffledGenres = new ArrayList<>(loadedGenres);
        Collections.shuffle(shuffledGenres);

        int genreIdx = 0;
        for (MainPageItem item : genresItems) {
            if (genreIdx < shuffledGenres.size()) {
                item.detail = shuffledGenres.get(genreIdx);
                item.title.setText(shuffledGenres.get(genreIdx++).getName());
            } else {
                item.setItemVisibility(View.INVISIBLE);
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
        Object detail;

        MainPageItem(ImageView coverArt, TextView title) {
            this.coverArt = coverArt;
            this.title = title;
        }

        void setItemVisibility(int visibility) {
            coverArt.setVisibility(visibility);
            title.setVisibility(visibility);
        }
    }

    public enum MainPageDataType {
        SONGS, ALBUMS, GENRES
    }
}
