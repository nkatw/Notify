package github.daneren2005.dsub.dialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import github.daneren2005.dsub.R;

public class NotifyProgressBarDialogFragment extends NotifyDialogFragment {
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        progressBar = view.findViewById(R.id.notify_dialog_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    public static NotifyProgressBarDialogFragment show(FragmentManager fragmentManager, String title,
                                                       String message, boolean indeterminate,
                                                       boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        NotifyProgressBarDialogFragment dialog = new NotifyProgressBarDialogFragment();
        dialog.setTitle(title);
        dialog.setContent(message);
        dialog.show(fragmentManager, "NotifyProgressBar");
        return dialog;
    }


}
