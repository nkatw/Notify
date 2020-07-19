package github.daneren2005.dsub.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.dialogFragment.AdminLoginDialogFragment;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.util.Constants;

public class NotifyFragment extends SubsonicFragment {
    private static final String TAG = NotifyFragment.class.getSimpleName();

    public static final int RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_DEFAULT = 73;
    public static final int RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_HAS_SEARCH_BAR = 42;

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
            // TODO: Show notifySearchFragment
            Toast.makeText(context, "Show search fragment", Toast.LENGTH_SHORT).show();
            SubsonicFragment fragment = new NotifySearchFragment();
            replaceFragment(fragment);
        });

        radioBtn = rootView.findViewById(R.id.notify_toolbar_radio_button);
        radioBtn.setOnClickListener(v -> {
            // TODO: Show notifyRadioFragment
            Toast.makeText(context, "Show radio fragment", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "createNotifyCustomToolbar: radioBtn");
        });

        toolbarItemVisibility(hasBack, hasSearchBar, hasAdmin, hasSearch, hasRadio);
    }

    protected void toolbarItemVisibility(boolean hasBack, boolean hasSearchBar,
                                         boolean hasAdmin, boolean hasSearch, boolean hasRadio) {
        backBtn.setVisibility(hasBack ? View.VISIBLE : View.GONE);

        searchBar.setVisibility(hasSearchBar ? View.VISIBLE : View.INVISIBLE);
        ViewGroup.MarginLayoutParams radioBtnMlp = (ViewGroup.MarginLayoutParams)radioBtn.getLayoutParams();
        radioBtnMlp.setMarginStart(hasSearchBar ? RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_HAS_SEARCH_BAR :
                RADIO_BTN_LAYOUT_MARGIN_LEFT_VALUE_DEFAULT);

        adminSettingsBtn.setVisibility(hasAdmin ? View.VISIBLE : View.GONE);
        searchBtn.setVisibility(hasSearch ? View.VISIBLE : View.GONE);
        radioBtn.setVisibility(hasRadio ? View.VISIBLE : View.GONE);
    }

    protected void showPlayerPage() {
        // TODO: show player page
        Toast.makeText(context, "Song item Click!", Toast.LENGTH_SHORT).show();
    }

    protected void showAlbumPage(String albumId, String albumName) {
        Log.d(TAG, "showAlbumPage: id = " + albumId + ", name = " + albumName);
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
}
