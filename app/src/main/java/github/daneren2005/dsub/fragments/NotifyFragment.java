package github.daneren2005.dsub.fragments;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.dialogFragment.AdminLoginDialogFragment;
import github.daneren2005.dsub.domain.Genre;

public class NotifyFragment extends SubsonicFragment {
    private static final String TAG = NotifyFragment.class.getSimpleName();

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
            Log.d(TAG, "createNotifyCustomToolbar: searchBtn");
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
        adminSettingsBtn.setVisibility(hasAdmin ? View.VISIBLE : View.GONE);
        searchBtn.setVisibility(hasSearch ? View.VISIBLE : View.GONE);
        radioBtn.setVisibility(hasRadio ? View.VISIBLE : View.GONE);
    }

    protected void showGenreDetailPage(Genre genre) {
        // TODO: replace fragment with GenreDetailPage
        Toast.makeText(context, "Clicked genre = " + genre.getName(), Toast.LENGTH_SHORT).show();
    }
}
