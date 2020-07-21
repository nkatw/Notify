package github.daneren2005.dsub.dialogFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.View;
import android.widget.ImageButton;

import github.daneren2005.dsub.R;

public class NotifyDialogPreference extends DialogPreference {
    protected ImageButton dismissBtn;

    public NotifyDialogPreference(Context context) {
        super(context);
        setPersistent(false);
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
    protected void onBindDialogView(View view) {
        dismissBtn = view.findViewById(R.id.notify_dialog_dismiss_imgbtn);
        dismissBtn.setOnClickListener(v -> getDialog().dismiss());
        View layout = view.findViewById(R.id.notify_dialog_background);
        layout.setOnClickListener(v -> getDialog().dismiss());

        super.onBindDialogView(view);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
