package github.daneren2005.dsub.dialogFragment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import github.daneren2005.dsub.R;

public class NotifyConfirmDialogPreference extends NotifyDialogPreference {
    public final static int NO_ID_ON_VIEW = 0;

    private View.OnClickListener listener;

    private Button positiveBtn;

    private int strResIdOnPositiveBtn = NO_ID_ON_VIEW;


    public NotifyConfirmDialogPreference(Context context) {
        super(context);
    }

    public NotifyConfirmDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyConfirmDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        dismissBtn.setVisibility(View.VISIBLE);

        positiveBtn = view.findViewById(R.id.notify_dialog_bottom_btn);
        positiveBtn.setOnClickListener(listener);
        positiveBtn.setVisibility(View.VISIBLE);
        if (strResIdOnPositiveBtn != NO_ID_ON_VIEW) {
            positiveBtn.setText(strResIdOnPositiveBtn);
        }

        return view;
    }

    public void setButtonText(int resId) {
        if (positiveBtn == null) {
            strResIdOnPositiveBtn = resId;
        } else {
            positiveBtn.setText(resId);
        }
    }

    public void setOnButtonClickListener(View.OnClickListener listener) {
        if (positiveBtn == null) {
            this.listener = listener;
        } else {
            positiveBtn.setOnClickListener(listener);
        }
    }
}
