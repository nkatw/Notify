/*
  This file is part of Subsonic.
	Subsonic is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	Subsonic is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with Subsonic. If not, see <http://www.gnu.org/licenses/>.
	Copyright 2014 (C) Scott Jackson
*/
package github.daneren2005.dsub.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.MediaRouteButton;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.activity.SubsonicFragmentActivity;
import github.daneren2005.dsub.adapter.DownloadFileAdapter;
import github.daneren2005.dsub.adapter.SectionAdapter;
import github.daneren2005.dsub.audiofx.EqualizerController;
import github.daneren2005.dsub.domain.PlayerState;
import github.daneren2005.dsub.domain.RepeatMode;
import github.daneren2005.dsub.service.DownloadFile;
import github.daneren2005.dsub.service.DownloadService;
import github.daneren2005.dsub.service.DownloadService.OnSongChangedListener;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.FileUtil;
import github.daneren2005.dsub.util.MenuUtil;
import github.daneren2005.dsub.util.SilentBackgroundTask;
import github.daneren2005.dsub.util.UpdateHelper;
import github.daneren2005.dsub.util.Util;
import github.daneren2005.dsub.view.AutoRepeatButton;
import github.daneren2005.dsub.view.UpdateView;
import github.daneren2005.dsub.view.compat.CustomMediaRouteDialogFactory;

import static github.daneren2005.dsub.domain.MusicDirectory.Entry;
import static github.daneren2005.dsub.domain.PlayerState.COMPLETED;
import static github.daneren2005.dsub.domain.PlayerState.IDLE;
import static github.daneren2005.dsub.domain.PlayerState.PAUSED;
import static github.daneren2005.dsub.domain.PlayerState.STOPPED;

public class NowPlayingFragment extends SubsonicFragment implements OnGestureListener, SectionAdapter.OnItemClickedListener<DownloadFile>, OnSongChangedListener {
    private static final String TAG = NowPlayingFragment.class.getSimpleName();
    public static final int PERCENTAGE_OF_SCREEN_FOR_SWIPE = 10;
    public static final String TIME_MIN_FORMAT = "0:00";
    public static final String TIME_UNKNOWN_FORMAT = "--:--";

    private static final int ACTION_PREVIOUS = 1;
    private static final int ACTION_NEXT = 2;
    private static final int ACTION_REWIND = 3;
    private static final int ACTION_FORWARD = 4;

    private TextView artistTitleTextView;
    private ImageView albumArtImageView;
    private TextView positionTextView;
    private TextView durationTextView;
    protected TextView statusTextView;
    private SeekBar progressBar;
    private AutoRepeatButton previousButton;
    private AutoRepeatButton nextButton;
    private AutoRepeatButton rewindButton;
    private AutoRepeatButton fastforwardButton;
    private View pauseButton;
    private View stopButton;
    private View startButton;
    private ImageButton repeatButton;
    private ImageButton shuffleButton;

    private ScheduledExecutorService executorService;
    private DownloadFile currentPlaying;
    private int swipeDistance;
    private int swipeVelocity;
    private ScheduledFuture<?> hideControlsFuture;
    private List<DownloadFile> songList;
    private DownloadFileAdapter songListAdapter;
    private boolean seekInProgress = false;
    private boolean startFlipped = false;
    private int lastY = 0;
    private int currentPlayingSize = 0;
    private MenuItem timerMenu;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.getInt(Constants.FRAGMENT_DOWNLOAD_FLIPPER) == 1) {
                startFlipped = true;
            }
        }
        primaryFragment = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        rootView = inflater.inflate(R.layout.download, container, false);
        setTitle(R.string.button_bar_now_playing);

        WindowManager w = context.getWindowManager();
        Display d = w.getDefaultDisplay();
        swipeDistance = (d.getWidth() + d.getHeight()) * PERCENTAGE_OF_SCREEN_FOR_SWIPE / 100;
        swipeVelocity = (d.getWidth() + d.getHeight()) * PERCENTAGE_OF_SCREEN_FOR_SWIPE / 100;
        gestureScanner = new GestureDetector(this);

        artistTitleTextView = (TextView) rootView.findViewById(R.id.download_artist_title);
        albumArtImageView = (ImageView) rootView.findViewById(R.id.download_album_art_image);
        positionTextView = (TextView) rootView.findViewById(R.id.download_position);
        durationTextView = (TextView) rootView.findViewById(R.id.download_duration);
        statusTextView = (TextView) rootView.findViewById(R.id.download_status);
        progressBar = (SeekBar) rootView.findViewById(R.id.download_progress_bar);
        previousButton = (AutoRepeatButton) rootView.findViewById(R.id.download_previous);
        nextButton = (AutoRepeatButton) rootView.findViewById(R.id.download_next);
        rewindButton = (AutoRepeatButton) rootView.findViewById(R.id.download_rewind);
        fastforwardButton = (AutoRepeatButton) rootView.findViewById(R.id.download_fastforward);
        pauseButton = rootView.findViewById(R.id.download_pause);
        stopButton = rootView.findViewById(R.id.download_stop);
        startButton = rootView.findViewById(R.id.download_start);
        repeatButton = (ImageButton) rootView.findViewById(R.id.download_repeat);
        shuffleButton = rootView.findViewById(R.id.download_shuffle);

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                return gestureScanner.onTouchEvent(me);
            }
        };
        pauseButton.setOnTouchListener(touchListener);
        stopButton.setOnTouchListener(touchListener);
        startButton.setOnTouchListener(touchListener);
        albumArtImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                if (me.getAction() == MotionEvent.ACTION_DOWN) {
                    lastY = (int) me.getRawY();
                }
                return gestureScanner.onTouchEvent(me);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfStorageUnavailable();
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        getDownloadService().previous();
                        return null;
                    }
                }.execute();
                setControlsVisible(true);
            }
        });
        previousButton.setOnRepeatListener(new Runnable() {
            public void run() {
                changeProgress(true);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfStorageUnavailable();
                new SilentBackgroundTask<Boolean>(context) {
                    @Override
                    protected Boolean doInBackground() throws Throwable {
                        getDownloadService().next();
                        return true;
                    }
                }.execute();
                setControlsVisible(true);
            }
        });
        nextButton.setOnRepeatListener(new Runnable() {
            public void run() {
                changeProgress(false);
            }
        });

        rewindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProgress(true);
            }
        });
        rewindButton.setOnRepeatListener(new Runnable() {
            public void run() {
                changeProgress(true);
            }
        });

        fastforwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProgress(false);
            }
        });
        fastforwardButton.setOnRepeatListener(new Runnable() {
            public void run() {
                changeProgress(false);
            }
        });


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        getDownloadService().pause();
                        return null;
                    }
                }.execute();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        getDownloadService().reset();
                        return null;
                    }
                }.execute();
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warnIfStorageUnavailable();
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        start();
                        return null;
                    }
                }.execute();
            }
        });

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepeatMode repeatMode = getDownloadService().getRepeatMode().next();
                getDownloadService().setRepeatMode(repeatMode);
                switch (repeatMode) {
                    case OFF:
                        Util.toast(context, R.string.download_repeat_off);
                        break;
                    case ALL:
                        Util.toast(context, R.string.download_repeat_all);
                        break;
                    case SINGLE:
                        Util.toast(context, R.string.download_repeat_single);
                        break;
                    default:
                        break;
                }
                updateRepeatButton();
                setControlsVisible(true);
            }
        });

        shuffleButton.setOnClickListener(v -> {
            new SilentBackgroundTask<Void>(context) {
                @Override
                protected Void doInBackground() throws Throwable {
                    getDownloadService().shuffle();
                    return null;
                }

                @Override
                protected void done(Void result) {
                    Util.toast(context, R.string.download_menu_shuffle_notification);
                }
            }.execute();
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        getDownloadService().seekTo(progressBar.getProgress());
                        return null;
                    }

                    @Override
                    protected void done(Void result) {
                        seekInProgress = false;
                    }
                }.execute();
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar) {
                seekInProgress = true;
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int position, final boolean fromUser) {
                if (fromUser) {
                    positionTextView.setText(Util.formatDuration(position / 1000));
                    setControlsVisible(true);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        DownloadService downloadService = getDownloadService();
        if (Util.isOffline(context)) {
            menuInflater.inflate(R.menu.nowplaying_offline, menu);
        } else {
            menuInflater.inflate(R.menu.nowplaying, menu);
        }
        if (downloadService != null && downloadService.getSleepTimer()) {
            int timeRemaining = downloadService.getSleepTimeRemaining();
            timerMenu = menu.findItem(R.id.menu_toggle_timer);
            if (timeRemaining > 1) {
                timerMenu.setTitle(context.getResources().getString(R.string.download_stop_time_remaining, Util.formatDuration(timeRemaining)));
            } else {
                timerMenu.setTitle(R.string.menu_set_timer);
            }
        }
        if (downloadService != null && downloadService.getKeepScreenOn()) {
            menu.findItem(R.id.menu_screen_on_off).setChecked(true);
        }
        if (downloadService != null && downloadService.isRemovePlayed()) {
            menu.findItem(R.id.menu_remove_played).setChecked(true);
        }

        boolean equalizerAvailable = downloadService != null && downloadService.getEqualizerAvailable();
        boolean isRemoteEnabled = downloadService != null && downloadService.isRemoteEnabled();
        if (equalizerAvailable && !isRemoteEnabled) {
            SharedPreferences prefs = Util.getPreferences(context);
            boolean equalizerOn = prefs.getBoolean(Constants.PREFERENCES_EQUALIZER_ON, false);
            if (equalizerOn && downloadService != null) {
                if (downloadService.getEqualizerController() != null && downloadService.getEqualizerController().isEnabled()) {
                    menu.findItem(R.id.menu_equalizer).setChecked(true);
                }
            }
        } else {
            menu.removeItem(R.id.menu_equalizer);
        }

        if (downloadService != null) {
            MenuItem mediaRouteItem = menu.findItem(R.id.menu_mediaroute);
            if (mediaRouteItem != null) {
                MediaRouteButton mediaRouteButton = (MediaRouteButton) MenuItemCompat.getActionView(mediaRouteItem);
                mediaRouteButton.setDialogFactory(new CustomMediaRouteDialogFactory());
                mediaRouteButton.setRouteSelector(downloadService.getRemoteSelector());
            }

            if (downloadService.isCurrentPlayingSingle()) {
                if (!Util.isOffline(context)) {
                    menu.removeItem(R.id.menu_save_playlist);
                }

                menu.removeItem(R.id.menu_batch_mode);
                menu.removeItem(R.id.menu_remove_played);
            }
        }

        if (Util.getPreferences(context).getBoolean(Constants.PREFERENCES_KEY_BATCH_MODE, false)) {
            menu.findItem(R.id.menu_batch_mode).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItemSelected(menuItem.getItemId(), null)) {
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onCreateContextMenu(Menu menu, MenuInflater menuInflater, UpdateView<DownloadFile> updateView, DownloadFile downloadFile) {
        if (Util.isOffline(context)) {
            menuInflater.inflate(R.menu.nowplaying_context_offline, menu);
        } else {
            menuInflater.inflate(R.menu.nowplaying_context, menu);
            menu.findItem(R.id.song_menu_star).setTitle(downloadFile.getSong().isStarred() ? R.string.common_unstar : R.string.common_star);
        }

        if (downloadFile.getSong().getParent() == null) {
            menu.findItem(R.id.menu_show_album).setVisible(false);
            menu.findItem(R.id.menu_show_artist).setVisible(false);
        }

        MenuUtil.hideMenuItems(context, menu, updateView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem, UpdateView<DownloadFile> updateView, DownloadFile downloadFile) {
        if (onContextItemSelected(menuItem, downloadFile.getSong())) {
            return true;
        }

        return menuItemSelected(menuItem.getItemId(), downloadFile);
    }

    private boolean menuItemSelected(int menuItemId, final DownloadFile song) {
        List<Entry> songs;
        switch (menuItemId) {
            case R.id.menu_show_album:
            case R.id.menu_show_artist:
                Entry entry = song.getSong();

                Intent intent = new Intent(context, SubsonicFragmentActivity.class);
                intent.putExtra(Constants.INTENT_EXTRA_VIEW_ALBUM, true);
                String albumId;
                String albumName;
                if (menuItemId == R.id.menu_show_album) {
                    if (Util.isTagBrowsing(context)) {
                        albumId = entry.getAlbumId();
                    } else {
                        albumId = entry.getParent();
                    }
                    albumName = entry.getAlbum();
                } else {
                    if (Util.isTagBrowsing(context)) {
                        albumId = entry.getArtistId();
                    } else {
                        albumId = entry.getGrandParent();
                        if (albumId == null) {
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_CHILD_ID, entry.getParent());
                        }
                    }
                    albumName = entry.getArtist();
                    intent.putExtra(Constants.INTENT_EXTRA_NAME_ARTIST, true);
                }
                intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, albumId);
                intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, albumName);
                intent.putExtra(Constants.INTENT_EXTRA_FRAGMENT_TYPE, "Artist");

                if (Util.isOffline(context)) {
                    try {
                        // This should only be successful if this is a online song in offline mode
                        Integer.parseInt(entry.getParent());
                        String root = FileUtil.getMusicDirectory(context).getPath();
                        String id = root + "/" + entry.getPath();
                        id = id.substring(0, id.lastIndexOf("/"));
                        if (menuItemId == R.id.menu_show_album) {
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, id);
                        }
                        id = id.substring(0, id.lastIndexOf("/"));
                        if (menuItemId != R.id.menu_show_album) {
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_ID, id);
                            intent.putExtra(Constants.INTENT_EXTRA_NAME_NAME, entry.getArtist());
                            intent.removeExtra(Constants.INTENT_EXTRA_NAME_CHILD_ID);
                        }
                    } catch (Exception e) {
                        // Do nothing, entry.getParent() is fine
                    }
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Util.startActivityWithoutTransition(context, intent);
                return true;
            case R.id.menu_lyrics: {
                SubsonicFragment fragment = new LyricsFragment();
                Bundle args = new Bundle();
                args.putString(Constants.INTENT_EXTRA_NAME_ARTIST, song.getSong().getArtist());
                args.putString(Constants.INTENT_EXTRA_NAME_TITLE, song.getSong().getTitle());
                fragment.setArguments(args);

                replaceFragment(fragment);
                return true;
            }
            case R.id.menu_remove_all:
                Util.confirmDialog(context, R.string.download_menu_remove_all, "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SilentBackgroundTask<Void>(context) {
                            @Override
                            protected Void doInBackground() throws Throwable {
                                getDownloadService().setShufflePlayEnabled(false);
                                getDownloadService().clear();
                                return null;
                            }

                            @Override
                            protected void done(Void result) {
                                context.closeNowPlaying();
                            }
                        }.execute();
                    }
                });
                return true;
            case R.id.menu_screen_on_off:
                if (getDownloadService().getKeepScreenOn()) {
                    context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    getDownloadService().setKeepScreenOn(false);
                } else {
                    context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    getDownloadService().setKeepScreenOn(true);
                }
                context.supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_remove_played:
                if (getDownloadService().isRemovePlayed()) {
                    getDownloadService().setRemovePlayed(false);
                } else {
                    getDownloadService().setRemovePlayed(true);
                }
                context.supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_shuffle:
                new SilentBackgroundTask<Void>(context) {
                    @Override
                    protected Void doInBackground() throws Throwable {
                        getDownloadService().shuffle();
                        return null;
                    }

                    @Override
                    protected void done(Void result) {
                        Util.toast(context, R.string.download_menu_shuffle_notification);
                    }
                }.execute();
                return true;
            case R.id.menu_save_playlist:
                List<Entry> entries = new LinkedList<Entry>();
                for (DownloadFile downloadFile : getDownloadService().getSongs()) {
                    entries.add(downloadFile.getSong());
                }
                createNewPlaylist(entries, true);
                return true;
            case R.id.menu_rate:
                UpdateHelper.setRating(context, song.getSong());
                return true;
            case R.id.menu_toggle_timer:
                if (getDownloadService().getSleepTimer()) {
                    getDownloadService().stopSleepTimer();
                    context.supportInvalidateOptionsMenu();
                } else {
                    startTimer();
                }
                return true;
            case R.id.menu_info:
                displaySongInfo(song.getSong());
                return true;
            case R.id.menu_share:
                songs = new ArrayList<Entry>(1);
                songs.add(song.getSong());
                createShare(songs);
                return true;
            case R.id.menu_equalizer: {
                DownloadService downloadService = getDownloadService();
                if (downloadService != null) {
                    EqualizerController controller = downloadService.getEqualizerController();
                    if (controller != null) {
                        SubsonicFragment fragment = new EqualizerFragment();
                        replaceFragment(fragment);
                        setControlsVisible(true);

                        return true;
                    }
                }

                // Any failed condition will get here
                Util.toast(context, "Failed to start equalizer.  Try restarting.");
                return true;
            }
            case R.id.menu_batch_mode:
                if (Util.isBatchMode(context)) {
                    Util.setBatchMode(context, false);
                    songListAdapter.notifyDataSetChanged();
                } else {
                    Util.setBatchMode(context, true);
                    songListAdapter.notifyDataSetChanged();
                }
                context.supportInvalidateOptionsMenu();

                return true;
            default:
                return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.primaryFragment) {
            onResumeHandlers();
        }
    }

    private void onResumeHandlers() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        setControlsVisible(true);

        final DownloadService downloadService = getDownloadService();
        if (downloadService == null || downloadService.getCurrentPlaying() == null || startFlipped) {
            startFlipped = false;
        }
        if (downloadService != null && downloadService.getKeepScreenOn()) {
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        updateButtons();

        if (currentPlaying == null && downloadService != null && currentPlaying == downloadService.getCurrentPlaying()) {
            getImageLoader().loadImage(albumArtImageView, (Entry) null, true, false);
        }

        context.runWhenServiceAvailable(new Runnable() {
            @Override
            public void run() {
                if (primaryFragment) {
                    DownloadService downloadService = getDownloadService();
                    downloadService.startRemoteScan();
                    downloadService.addOnSongChangedListener(NowPlayingFragment.this, true);
                }
                updateRepeatButton();
                updateTitle();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        onPauseHandlers();
    }

    private void onPauseHandlers() {
        if (executorService != null) {
            DownloadService downloadService = getDownloadService();
            if (downloadService != null) {
                downloadService.stopRemoteScan();
                downloadService.removeOnSongChangeListener(this);
            }
        }
    }

    @Override
    public void setPrimaryFragment(boolean primary) {
        super.setPrimaryFragment(primary);
        if (rootView != null) {
            if (primary) {
                onResumeHandlers();
            } else {
                onPauseHandlers();
            }
        }
    }

    @Override
    public void setTitle(int title) {
        this.title = context.getResources().getString(title);
        if (this.primaryFragment) {
            context.setTitle(this.title);
        }
    }

    @Override
    public void setSubtitle(CharSequence title) {
        this.subtitle = title;
        if (this.primaryFragment) {
            context.setSubtitle(title);
        }
    }

    @Override
    public SectionAdapter getCurrentAdapter() {
        return songListAdapter;
    }

    private void scheduleHideControls() {
        if (hideControlsFuture != null) {
            hideControlsFuture.cancel(false);
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setControlsVisible(false);
                    }
                });
            }
        };
        hideControlsFuture = executorService.schedule(runnable, 3000L, TimeUnit.MILLISECONDS);
    }

    private void setControlsVisible(boolean visible) {
        DownloadService downloadService = getDownloadService();
        if (downloadService != null && downloadService.isCurrentPlayingSingle()) {
            return;
        }

        try {
            long duration = 1700L;

            if (visible) {
                scheduleHideControls();
            }
        } catch (Exception e) {

        }
    }

    private void updateButtons() {
        if (context == null) {
            return;
        }
    }


    protected void startTimer() {
        View dialogView = context.getLayoutInflater().inflate(R.layout.start_timer, null);

        // Setup length label
        final TextView lengthBox = (TextView) dialogView.findViewById(R.id.timer_length_label);
        final SharedPreferences prefs = Util.getPreferences(context);
        String lengthString = prefs.getString(Constants.PREFERENCES_KEY_SLEEP_TIMER_DURATION, "5");
        int length = Integer.parseInt(lengthString);
        lengthBox.setText(Util.formatDuration(length));

        // Setup length slider
        final SeekBar lengthBar = (SeekBar) dialogView.findViewById(R.id.timer_length_bar);
        lengthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int length = getMinutes(progress);
                    lengthBox.setText(Util.formatDuration(length));
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        lengthBar.setProgress(length - 1);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.menu_set_timer)
                .setView(dialogView)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int length = getMinutes(lengthBar.getProgress());

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PREFERENCES_KEY_SLEEP_TIMER_DURATION, Integer.toString(length));
                        editor.commit();

                        getDownloadService().setSleepTimerDuration(length);
                        getDownloadService().startSleepTimer();
                        context.supportInvalidateOptionsMenu();
                    }
                })
                .setNegativeButton(R.string.common_cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getMinutes(int progress) {
        if (progress < 30) {
            return progress + 1;
        } else if (progress < 49) {
            return (progress - 30) * 5 + getMinutes(29);
        } else if (progress < 57) {
            return (progress - 48) * 30 + getMinutes(48);
        } else if (progress < 81) {
            return (progress - 56) * 60 + getMinutes(56);
        } else {
            return (progress - 80) * 150 + getMinutes(80);
        }
    }


    private void start() {
        DownloadService service = getDownloadService();
        PlayerState state = service.getPlayerState();
        if (state == PAUSED || state == COMPLETED || state == STOPPED) {
            service.start();
        } else if (state == STOPPED || state == IDLE) {
            warnIfStorageUnavailable();
            int current = service.getCurrentPlayingIndex();
            // TODO: Use play() method.
            if (current == -1) {
                service.play(0);
            } else {
                service.play(current);
            }
        }
    }

    private void changeProgress(final boolean rewind) {
        final DownloadService downloadService = getDownloadService();
        if (downloadService == null) {
            return;
        }

        new SilentBackgroundTask<Void>(context) {
            int seekTo;

            @Override
            protected Void doInBackground() throws Throwable {
                if (rewind) {
                    seekTo = downloadService.rewind();
                } else {
                    seekTo = downloadService.fastForward();
                }
                return null;
            }

            @Override
            protected void done(Void result) {
                progressBar.setProgress(seekTo);
            }
        }.execute();
    }

    @Override
    public boolean onDown(MotionEvent me) {
        setControlsVisible(true);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final DownloadService downloadService = getDownloadService();
        if (downloadService == null || e1 == null || e2 == null) {
            return false;
        }

        // Right to Left swipe
        int action = 0;
        if (e1.getX() - e2.getX() > swipeDistance && Math.abs(velocityX) > swipeVelocity) {
            action = ACTION_NEXT;
        }
        // Left to Right swipe
        else if (e2.getX() - e1.getX() > swipeDistance && Math.abs(velocityX) > swipeVelocity) {
            action = ACTION_PREVIOUS;
        }
        // Top to Bottom swipe
        else if (e2.getY() - e1.getY() > swipeDistance && Math.abs(velocityY) > swipeVelocity) {
            action = ACTION_FORWARD;
        }
        // Bottom to Top swipe
        else if (e1.getY() - e2.getY() > swipeDistance && Math.abs(velocityY) > swipeVelocity) {
            action = ACTION_REWIND;
        }

        if (action > 0) {
            final int performAction = action;
            warnIfStorageUnavailable();
            new SilentBackgroundTask<Void>(context) {
                @Override
                protected Void doInBackground() throws Throwable {
                    switch (performAction) {
                        case ACTION_NEXT:
                            downloadService.next();
                            break;
                        case ACTION_PREVIOUS:
                            downloadService.previous();
                            break;
                        case ACTION_FORWARD:
                            downloadService.fastForward();
                            break;
                        case ACTION_REWIND:
                            downloadService.rewind();
                            break;
                    }
                    return null;
                }
            }.execute();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public void onItemClicked(UpdateView<DownloadFile> updateView, final DownloadFile item) {
        warnIfStorageUnavailable();
        new SilentBackgroundTask<Void>(context) {
            @Override
            protected Void doInBackground() throws Throwable {
                getDownloadService().play(item);
                return null;
            }
        }.execute();
    }

    @Override
    public void onSongChanged(DownloadFile currentPlaying, int currentPlayingIndex, boolean shouldFastForward) {
        this.currentPlaying = currentPlaying;
        setupSubtitle(currentPlayingIndex);

        updateMediaButton(shouldFastForward);
        updateTitle();
    }

    private void updateMediaButton(boolean shouldFastForward) {
        DownloadService downloadService = getDownloadService();
        if (downloadService.isCurrentPlayingSingle()) {
            previousButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            rewindButton.setVisibility(View.GONE);
            fastforwardButton.setVisibility(View.GONE);
        } else {
            if (downloadService.shouldFastForward()) {
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);

                rewindButton.setVisibility(View.VISIBLE);
                fastforwardButton.setVisibility(View.VISIBLE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);

                rewindButton.setVisibility(View.GONE);
                fastforwardButton.setVisibility(View.GONE);
            }
        }
    }

    private void setupSubtitle(int currentPlayingIndex) {
        if (currentPlaying != null) {
            Entry song = currentPlaying.getSong();
            statusTextView.setText(song.getTitle());
            getImageLoader().loadImage(albumArtImageView, song, true, true);

            DownloadService downloadService = getDownloadService();
            if (downloadService.isCurrentPlayingSingle()) {
                setSubtitle(null);
            } else if (downloadService.isShufflePlayEnabled()) {
                setSubtitle(context.getResources().getString(R.string.download_playerstate_playing_shuffle));
            } else if (downloadService.isArtistRadio()) {
                setSubtitle(context.getResources().getString(R.string.download_playerstate_playing_artist_radio));
            } else {
                setSubtitle(context.getResources().getString(R.string.download_playing_out_of, currentPlayingIndex + 1, currentPlayingSize));
            }
        } else {
            statusTextView.setText(null);
            getImageLoader().loadImage(albumArtImageView, (Entry) null, true, false);
            setSubtitle(null);
        }
    }

    @Override
    public void onSongsChanged(List<DownloadFile> songs, DownloadFile currentPlaying, int currentPlayingIndex, boolean shouldFastForward) {
        currentPlayingSize = songs.size();

        DownloadService downloadService = getDownloadService();

        if (songListAdapter == null) {
            songList = new ArrayList<>();
            songList.addAll(songs);
        } else {
            songList.clear();
            songList.addAll(songs);
            songListAdapter.notifyDataSetChanged();
        }

        if (this.currentPlaying != currentPlaying) {
            onSongChanged(currentPlaying, currentPlayingIndex, shouldFastForward);
            onMetadataUpdate(currentPlaying != null ? currentPlaying.getSong() : null, DownloadService.METADATA_UPDATED_ALL);
        } else {
            updateMediaButton(shouldFastForward);
            setupSubtitle(currentPlayingIndex);
        }
    }

    @Override
    public void onSongProgress(DownloadFile currentPlaying, int millisPlayed, Integer duration, boolean isSeekable) {
        if (currentPlaying != null) {
            int millisTotal = duration == null ? 0 : duration;

            positionTextView.setText(Util.formatDuration(millisPlayed / 1000));
            if (millisTotal > 0) {
                durationTextView.setText(Util.formatDuration(millisTotal / 1000));
            } else {
                durationTextView.setText(TIME_UNKNOWN_FORMAT);
            }
            progressBar.setMax(millisTotal == 0 ? 100 : millisTotal); // Work-around for apparent bug.
            if (!seekInProgress) {
                progressBar.setProgress(millisPlayed);
            }
            progressBar.setEnabled(isSeekable);
        } else {
            positionTextView.setText(TIME_MIN_FORMAT);
            durationTextView.setText(TIME_UNKNOWN_FORMAT);
            progressBar.setProgress(0);
            progressBar.setEnabled(false);
        }

        DownloadService downloadService = getDownloadService();
        if (downloadService != null && downloadService.getSleepTimer() && timerMenu != null) {
            int timeRemaining = downloadService.getSleepTimeRemaining();
            if (timeRemaining > 1) {
                timerMenu.setTitle(context.getResources().getString(R.string.download_stop_time_remaining, Util.formatDuration(timeRemaining)));
            } else {
                timerMenu.setTitle(R.string.menu_set_timer);
            }
        }
    }

    @Override
    public void onStateUpdate(DownloadFile downloadFile, PlayerState playerState) {
        switch (playerState) {
            case DOWNLOADING:
                if (currentPlaying != null) {
                    if (Util.isWifiRequiredForDownload(context) || Util.isLocalNetworkRequiredForDownload(context)) {
                        statusTextView.setText(context.getResources().getString(R.string.download_playerstate_mobile_disabled));
                    } else {
                        long bytes = currentPlaying.getPartialFile().length();
                        statusTextView.setText(context.getResources().getString(R.string.download_playerstate_downloading, Util.formatLocalizedBytes(bytes, context)));
                    }
                }
                break;
            case PREPARING:
                statusTextView.setText(R.string.download_playerstate_buffering);
                break;
            default:
                if (currentPlaying != null) {
                    Entry entry = currentPlaying.getSong();
                    if (entry.getAlbum() != null) {
                        String artist = "";
                        if (entry.getArtist() != null) {
                            artist = currentPlaying.getSong().getArtist();
                            artistTitleTextView.setText(artist);
                            statusTextView.setText(entry.getTitle());
                        }
                    } else {
                        statusTextView.setText(null);
                    }
                } else {
                    statusTextView.setText(null);
                }
                break;
        }

        switch (playerState) {
            case STARTED:
                pauseButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                break;
            case DOWNLOADING:
            case PREPARING:
                pauseButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                break;
            default:
                pauseButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onMetadataUpdate(Entry song, int fieldChange) {
        if (song != null && albumArtImageView != null && fieldChange == DownloadService.METADATA_UPDATED_COVER_ART) {
            getImageLoader().loadImage(albumArtImageView, song, true, true);
        }
    }

    public void updateRepeatButton() {
        DownloadService downloadService = getDownloadService();
        switch (downloadService.getRepeatMode()) {
            case OFF:
                repeatButton.setImageResource(R.mipmap.icon_repeat);
                break;
            case ALL:
                repeatButton.setImageResource(R.mipmap.icon_repeat_p);
                break;
            case SINGLE:
                repeatButton.setImageResource(R.mipmap.icon_repeat_song);
                break;
            default:
                break;
        }
    }

    private void updateTitle() {
        DownloadService downloadService = getDownloadService();
        float playbackSpeed = downloadService.getPlaybackSpeed();

        String title = context.getResources().getString(R.string.button_bar_now_playing);
        int stringRes = -1;
        if (playbackSpeed == 0.5f) {
            stringRes = R.string.download_playback_speed_half;
        } else if (playbackSpeed == 1.5f) {
            stringRes = R.string.download_playback_speed_one_half;
        } else if (playbackSpeed == 2.0f) {
            stringRes = R.string.download_playback_speed_double;
        } else if (playbackSpeed == 3.0f) {
            stringRes = R.string.download_playback_speed_tripple;
        }

        String playbackSpeedText = null;
        if (stringRes != -1) {
            playbackSpeedText = context.getResources().getString(stringRes);
        } else if (Math.abs(playbackSpeed - 1.0) > 0.01) {
            playbackSpeedText = Float.toString(playbackSpeed) + "x";
        }

        if (playbackSpeedText != null) {
            title += " (" + playbackSpeedText + ")";
        }
        setTitle(title);
    }

    @Override
    protected List<Entry> getSelectedEntries() {
        List<DownloadFile> selected = getCurrentAdapter().getSelected();
        List<Entry> entries = new ArrayList<>();

        for (DownloadFile downloadFile : selected) {
            if (downloadFile.getSong() != null) {
                entries.add(downloadFile.getSong());
            }
        }

        return entries;
    }

}
