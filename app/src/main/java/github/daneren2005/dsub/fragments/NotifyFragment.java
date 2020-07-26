package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.dialogFragment.AdminLoginDialogFragment;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.service.DownloadService;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class NotifyFragment extends SubsonicFragment {
    private static final String TAG = NotifyFragment.class.getSimpleName();

    public static final int RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_DEFAULT = 73;
    public static final int RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_HAS_SEARCH_BAR = 42;
    public static final int SONG_NOT_FOUND = -1;
    public static final String PLAYLIST_ID = "1";

    protected ImageButton backBtn, adminSettingsBtn, searchBtn, radioBtn;
    protected EditText searchBar;

    protected void createNotifyCustomToolbar(boolean hasBack, boolean hasSearchBar,
                                             boolean hasAdmin, boolean hasSearch, boolean hasRadio) {
        if (rootView == null) {
            Log.e(TAG, "createNotifyCustomToolbar: Error on rootView");
            return;
        }

        backBtn = rootView.findViewById(R.id.notify_toolbar_back);
        backBtn.setOnClickListener(view -> context.onBackPressed());

        searchBar = rootView.findViewById(R.id.notify_toolbar_search_bar);

        adminSettingsBtn = rootView.findViewById(R.id.notify_toolbar_admin_settings);
        adminSettingsBtn.setOnLongClickListener(v -> {
            AdminLoginDialogFragment adminLoginDialogFragment = new AdminLoginDialogFragment();
            adminLoginDialogFragment.show(context.getSupportFragmentManager(), "NotifyAdminLogin");
            Log.d(TAG, "createNotifyCustomToolbar: adminSettingsBtn");
            return true;
        });

        searchBtn = rootView.findViewById(R.id.notify_toolbar_search_button);
        searchBtn.setOnClickListener(v -> {
            SubsonicFragment fragment = new NotifySearchFragment();
            replaceFragment(fragment);
        });

        radioBtn = rootView.findViewById(R.id.notify_toolbar_radio_button);
        radioBtn.setOnClickListener(v -> {
            playNotifyRadioByLoadPlaylist();
        });

        toolbarItemVisibility(hasBack, hasSearchBar, hasAdmin, hasSearch, hasRadio);
    }

    protected void toolbarItemVisibility(boolean hasBack, boolean hasSearchBar,
                                         boolean hasAdmin, boolean hasSearch, boolean hasRadio) {
        backBtn.setVisibility(hasBack ? View.VISIBLE : View.GONE);

        searchBar.setVisibility(hasSearchBar ? View.VISIBLE : View.INVISIBLE);
        searchBar.setEnabled(hasSearchBar);
        ViewGroup.MarginLayoutParams radioBtnMlp = (ViewGroup.MarginLayoutParams) radioBtn.getLayoutParams();
        radioBtnMlp.setMarginStart(hasSearchBar ? RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_HAS_SEARCH_BAR :
                RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_DEFAULT);

        adminSettingsBtn.setVisibility(hasAdmin ? View.VISIBLE : View.GONE);
        searchBtn.setVisibility(hasSearch ? View.VISIBLE : View.GONE);
        radioBtn.setVisibility(hasRadio ? View.VISIBLE : View.GONE);
    }

    protected void showAlbumPage(String albumId, String albumName) {
        SubsonicFragment fragment = new AlbumPageFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_EXTRA_NAME_ID, albumId);
        args.putString(Constants.INTENT_EXTRA_NAME_NAME, albumName);
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    protected void showGenreDetailPage(Genre genre) {
        SubsonicFragment fragment = new GenreDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PREFERENCES_KEY_SHOW_SONG_BY_GENRE, genre.getName());
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    protected void showArtistPageFragment(String artistId, String artistName) {
        SubsonicFragment fragment = new ArtistPageFragment();
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_EXTRA_NAME_ID, artistId);
        args.putString(Constants.INTENT_EXTRA_NAME_NAME, artistName);
        fragment.setArguments(args);
        replaceFragment(fragment);
    }

    protected void playNotifyRadioByLoadPlaylist() {
        LoadDataTask<MusicDirectory> loadPlaylist = new LoadDataTask<>(this, null,
                NotifyDataType.PLAYLIST,
                ((musicService, listener) -> {
                    try {
                        return musicService.getPlaylist(false, PLAYLIST_ID, null, context, listener);
                    } catch (Exception e) {
                        Log.e(TAG, "loadGenres: exception = ", e);
                        return null;
                    }
                }));
        loadPlaylist.execute();
    }

    private void showRadioPage(MusicDirectory loadedPlaylist) {
        DownloadService.getInstance().setNotifyRadio(true, loadedPlaylist.getName());
        playNow(loadedPlaylist.getSongs());
    }

    protected void playAlbumBySongPosition(List<MusicDirectory.Entry> songs, MusicDirectory.Entry song,
                                           int position) {
        DownloadService.getInstance().setNotifyRadio(false);
        playNow(songs, song, position);
    }

    protected void loadAndPlayAlbumBySong(MusicDirectory.Entry song) {
        LoadDataTask<MusicDirectory> loadAlbumTask = new LoadDataTask<>(this, song,
                NotifyDataType.ALBUM,
                ((musicService, listener) -> {
                    try {
                        return musicService.getMusicDirectory(song.getAlbumId(), song.getAlbum(), false, context, listener);
                    } catch (Exception e) {
                        Log.e(TAG, "loadAlbums: exception = " + e);
                        return null;
                    }
                })
        );
        loadAlbumTask.execute();
    }

    private void playAlbumBySong(MusicDirectory album, MusicDirectory.Entry songToPlay) {
        DownloadService.getInstance().setNotifyRadio(false);
        List<MusicDirectory.Entry> songs = album.getSongs();
        playNow(songs, songToPlay, getSongPosition(songs, songToPlay));
    }

    private int getSongPosition(List<MusicDirectory.Entry> songs, MusicDirectory.Entry song) {
        int position = 0;
        for (MusicDirectory.Entry targetSong : songs) {
            String target = targetSong.getId();
            String id = song.getId();
            if (target.equals(id)) {
                return position;
            } else {
                position++;
            }
        }
        return SONG_NOT_FOUND;
    }

    private class LoadDataTask<T> extends TabBackgroundTask<T> {
        private MusicDirectory.Entry entry;
        private NotifyDataType dataType;
        private GetDataListener<T> getDataListener;

        public LoadDataTask(SubsonicFragment fragment, MusicDirectory.Entry entry,
                            NotifyDataType dataType, GetDataListener<T> getDataListener) {
            super(fragment);
            this.entry = entry;
            this.dataType = dataType;
            this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            switch (dataType) {
                case ALBUM:
                    playAlbumBySong((MusicDirectory) result, entry);
                    break;
                case PLAYLIST:
                    showRadioPage((MusicDirectory) result);
                    break;
            }
        }
    }

    public enum NotifyDataType {
        ALBUM, PLAYLIST
    }
}
