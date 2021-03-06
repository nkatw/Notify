package github.daneren2005.dsub.view;

import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.view.View;

import github.daneren2005.dsub.dialogFragment.NotifyEditTextPreference;
import github.daneren2005.dsub.util.Constants;
import github.daneren2005.dsub.util.KeyStoreUtil;
import github.daneren2005.dsub.util.Util;

public class NotifyEditPasswordPreference extends NotifyEditTextPreference {
    final private String TAG = NotifyEditPasswordPreference.class.getSimpleName();

    private int instance;
    private boolean passwordDecrypted;

    public NotifyEditPasswordPreference(Context context, int instance) {
        super(context);

        final NotifyEditPasswordPreference editPassPref = this;
        this.instance = instance;

        if (Build.VERSION.SDK_INT >= 23) {
            this.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return editPassPref.onPreferenceClick();
                }
            });
        }
    }

    private boolean onPreferenceClick() {
        Context context = this.getContext();

        // If password is encrypted, attempt to decrypt it to list actual password in masked box
        // It could be that we should fill in nonsense in here instead, but if we do and the user clicks OK,
        // the nonsense will be encrypted and the server connection will fail
        // Checks first to see if the password has already been decrypted - if the user clicks on the preference a second time
        // before the box has loaded, but after the password has already been encrypted
        if (!(passwordDecrypted) && (Util.getPreferences(context).getBoolean(Constants.PREFERENCES_KEY_ENCRYPTED_PASSWORD + this.instance, false))) {
            String decryptedPassword = KeyStoreUtil.decrypt(this.getEditText().getText().toString());
            if (decryptedPassword != null) {
                this.getEditText().setText(decryptedPassword);
                this.passwordDecrypted = true;
            } else {
                Util.toast(context, "Password Decryption Failed");
            }
        }

        // Let the click action continue as normal
        return false;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        savePassword();
    }

    private void savePassword() {
        if (Build.VERSION.SDK_INT >= 23) {
            Context context = this.getContext();

            String encryptedString = KeyStoreUtil.encrypt(getText());
            if (encryptedString != null) {
                setText(encryptedString);
                Util.getPreferences(context).edit().putBoolean(Constants.PREFERENCES_KEY_ENCRYPTED_PASSWORD + instance, true).commit();
            } else {
                Util.toast(context, "Password encryption failed ODC");
                Util.getPreferences(context).edit().putBoolean(Constants.PREFERENCES_KEY_ENCRYPTED_PASSWORD + instance, false).commit();
            }
        }

        // Reset this flag so it decrypts if applicable next time the dialog is opened
        this.passwordDecrypted = false;
    }

}
