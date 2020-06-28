package github.daneren2005.dsub.fragments;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.dialogFragment.AdminLoginDialogFragment;

public class NotifyFragment extends SubsonicFragment {
    private static final String TAG = NotifyFragment.class.getSimpleName();

    protected ImageButton adminSettingsBtn, searchBtn, radioBtn;

    protected void createNotifyCustomToolbar() {
        if (rootView == null) {
            Log.e(TAG, "createNotifyCustomToolbar: Error on rootView");
            return;
        }

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
}
