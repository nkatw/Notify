package github.daneren2005.dsub.dialogFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import github.daneren2005.dsub.R;

public class NotifyDialogPreference extends DialogPreference {
    public final static int NO_ID_ON_VIEW = 0;
    protected ImageButton dismissBtn;
    protected TextView titleTxv;
    protected TextView contentTxv;
    private int strResIdOnTitle = NO_ID_ON_VIEW;
    private int strResIdOnContent = NO_ID_ON_VIEW;

    public NotifyDialogPreference(Context context) {
        super(context);
        setDialogLayoutResource(R.layout.notify_dialog);
    }

    public NotifyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.notify_dialog);
    }

    public NotifyDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDialogLayoutResource(R.layout.notify_dialog);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle("");
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        dismissBtn = view.findViewById(R.id.notify_dialog_dismiss_imgbtn);
        dismissBtn.setOnClickListener(v -> getDialog().dismiss());

        View layout = view.findViewById(R.id.notify_dialog_background);
        layout.setOnClickListener(v -> getDialog().dismiss());

        titleTxv = view.findViewById(R.id.notify_dialog_title);
        if (strResIdOnTitle != NO_ID_ON_VIEW) {
            titleTxv.setText(strResIdOnTitle);
        }

        contentTxv = view.findViewById(R.id.notify_dialog_content);
        if (strResIdOnContent != NO_ID_ON_VIEW) {
            contentTxv.setText(strResIdOnContent);
        }
        contentTxv.setVisibility(View.GONE);

        return view;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void setDialogTitle(int resId) {
        if (titleTxv == null) {
            strResIdOnTitle = resId;
        } else {
            titleTxv.setText(resId);
        }
    }

    public void setDialogContent(int resId) {
        if (contentTxv == null) {
            strResIdOnContent = resId;
        } else {
            contentTxv.setText(resId);
        }

        contentTxv.setText(resId);
        setDialogContentVisibility(View.VISIBLE);
    }

    public void setDialogContentVisibility(int type) {
        contentTxv.setVisibility(type);
    }
}
