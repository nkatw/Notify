package github.daneren2005.dsub.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import github.daneren2005.dsub.R;

public class AdminLoginDialogFragment extends NotifyDialogFragmentTemplate {
    private static final String TAG = AdminLoginDialogFragment.class.getSimpleName();
    private static final String ADMIN_PASSWORD = "p@ssword";

    private EditText passwordEdt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        passwordEdt = view.findViewById(R.id.notify_dialog_input_edt);
        Button enterBtn = view.findViewById(R.id.notify_dialog_bottom_btn);
        enterBtn.setOnClickListener(v -> {
            if (passwordEdt.getText().toString().equals(ADMIN_PASSWORD)) {
                // TODO: Enter Setting Page
                Toast.makeText(this.getContext(), "Correct password!", Toast.LENGTH_SHORT).show();
                this.dismiss();
            } else {
                passwordEdt.setText(null);
                Toast.makeText(this.getContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
