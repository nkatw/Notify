package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import github.daneren2005.dsub.R;

public class MainPageFragment extends SubsonicFragment {
    private static final String TAG = MainPageFragment.class.getSimpleName();

    private List<ImageView> songsImageViews = new ArrayList<>();
    private List<TextView> songsTextViews = new ArrayList<>();

    private List<ImageView> albumsImageViews = new ArrayList<>();
    private List<TextView> albumsTextViews = new ArrayList<>();

    private Button moreGenresButton;
    private List<Button> genreButtons = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_main_page, container, false);
        createSongView();
        createAlbumView();
        createGenreView();

        // TODO: Remove shadow on actionbar

        // TODO: Add loading process when loading music data.
        Log.d(TAG, "onCreateView: called");
        return rootView;
    }

    private void createSongView() {
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView00));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView01));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView02));
        songsImageViews.add(rootView.findViewById(R.id.main_page_songs_imageView03));

        songsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView00));
        songsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView01));
        songsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView02));
        songsTextViews.add(rootView.findViewById(R.id.main_page_albums_textView03));

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
            genreButton.setOnClickListener( v -> {
                // TODO: Show genres page
                String genreName = genreButton.getText().toString();
                Log.d(TAG, "createGenreView: Genre Button clicked! " + genreName);
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        ((DrawerHider)this.getActivity()).setDrawerEnable(false);
        ((DrawerHider)this.getActivity()).setDrawerTitle("");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public interface DrawerHider {
        // Due to SubsonicActivity had called Drawer methods on onPostCreate()
        // To following lifecycle on activity and fragment,
        //                      invoke those methods on onResume() if necessary.
        void setDrawerEnable(Boolean enable);
        void setDrawerTitle(String title);
    }
}
