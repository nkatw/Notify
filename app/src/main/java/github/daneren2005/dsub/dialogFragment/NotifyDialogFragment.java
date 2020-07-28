package github.daneren2005.dsub.dialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.util.Util;

public class NotifyDialogFragment extends AppCompatDialogFragment {
    protected Context context;

    private String title = "";
    private String content = "";
    private Boolean canShow = false;

    private ImageButton dismissBtn;
    private View backgroundLayout;
    private TextView titleTxv;
    private TextView contentTxv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.notify_dialog, container, false);

        dismissBtn = view.findViewById(R.id.notify_dialog_dismiss_imgbtn);
        dismissBtn.setVisibility(canShow ? View.VISIBLE : View.GONE);
        dismissBtn.setOnClickListener(v -> this.dismiss());

        backgroundLayout = view.findViewById(R.id.notify_dialog_background);
        backgroundLayout.setOnClickListener(v -> this.dismiss());

        titleTxv = view.findViewById(R.id.notify_dialog_title);
        setTitle(title);

        contentTxv = view.findViewById(R.id.notify_dialog_content);
        setContent(content);

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

    public static NotifyDialogFragment show(FragmentManager fragmentManager, String title,
                                            String message, boolean indeterminate,
                                            boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        NotifyDialogFragment dialog = new NotifyDialogFragment();
        dialog.setTitle(title);
        dialog.setContent(message);
        dialog.show(fragmentManager, "NotifyDialog");
        return dialog;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setContent(String content) {
        if (contentTxv == null) {
            this.content = content;
        } else {
            contentTxv.setText(content);
            contentTxv.setVisibility(TextUtils.equals(content, "") ? View.GONE : View.VISIBLE);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (titleTxv == null) {
            this.title = title;
        } else {
            titleTxv.setText(title);
            titleTxv.setVisibility(TextUtils.equals(title, "") ? View.GONE : View.VISIBLE);
        }
    }

    public void showDismiss(boolean canShow) {
        if (dismissBtn == null) {
            this.canShow = canShow;
        } else {
            dismissBtn.setVisibility(canShow ? View.VISIBLE : View.GONE);
        }
    }

    public boolean isShowing() {
        return this.getDialog().isShowing();
    }
}
