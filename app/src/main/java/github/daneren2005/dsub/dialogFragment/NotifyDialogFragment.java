package github.daneren2005.dsub.dialogFragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.util.Util;

public class NotifyDialogFragment extends AppCompatDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.notify_dialog, container, false);
        ImageButton dismissBtn = view.findViewById(R.id.notify_dialog_dismiss_imgbtn);
        dismissBtn.setOnClickListener(v -> this.dismiss());
        View layout = view.findViewById(R.id.notify_dialog_background);
        layout.setOnClickListener(v -> this.dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Util.toSetNotifyDialog(getContext(), getDialog());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // To hide status bar and navigation bar
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                getDialog().setOnShowListener((dialog) -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    Util.toImmersiveMode(window);
                });
            }
        }

    }

}
