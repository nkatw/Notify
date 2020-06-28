package github.daneren2005.dsub.dialogFragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import github.daneren2005.dsub.R;

public class NotifyDialogFragmentTemplate extends AppCompatDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.notify_dialog, container, false);
        ImageButton dismissBtn = view.findViewById(R.id.notify_dialog_dismiss_imgbtn);
        dismissBtn.setOnClickListener(v -> this.dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setLayout(
                getResources().getDimensionPixelOffset(R.dimen.NotifyDialog_Width),
                getResources().getDimensionPixelOffset(R.dimen.NotifyDialog_Height)
        );
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
