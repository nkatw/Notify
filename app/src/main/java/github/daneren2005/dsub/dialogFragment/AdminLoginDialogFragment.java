package github.daneren2005.dsub.dialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.activity.SettingsActivity;

public class AdminLoginDialogFragment extends NotifyDialogFragment implements View.OnClickListener {
    private static final String TAG = AdminLoginDialogFragment.class.getSimpleName();
    private static final String ADMIN_PASSWORD = "p@ssword";

    private EditText passwordEdt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout inputLayout = view.findViewById(R.id.notify_dialog_input_layout);
        inputLayout.setVisibility(View.VISIBLE);

        ImageView inputIcon = view.findViewById(R.id.notify_dialog_input_password_icon);
        inputIcon.setVisibility(View.VISIBLE);

        passwordEdt = view.findViewById(R.id.notify_dialog_input_edt);
        passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEdt.setHint(R.string.notify_dialog_password_hint);

        Button enterBtn = view.findViewById(R.id.notify_dialog_bottom_btn);
        enterBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (passwordEdt.getText().toString().equals(ADMIN_PASSWORD)) {
            // TODO: Enter Setting Page
            Toast.makeText(this.getContext(), "Correct password!", Toast.LENGTH_SHORT).show();
            this.dismiss();
            startActivity(new Intent(getContext(), SettingsActivity.class));
        } else {
            passwordEdt.setText(null);
            Toast.makeText(this.getContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
        }
    }
}
