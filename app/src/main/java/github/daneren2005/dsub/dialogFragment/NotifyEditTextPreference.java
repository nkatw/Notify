package github.daneren2005.dsub.dialogFragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import github.daneren2005.dsub.R;

public class NotifyEditTextPreference extends NotifyConfirmDialogPreference implements View.OnClickListener {

    public final static int NO_TYPE_ON_VIEW = 0;
    private String text;
    private boolean hasSetText = false;

    private ViewGroup layout;
    private EditText editText;

    private int inputType = NO_TYPE_ON_VIEW;

    public NotifyEditTextPreference(Context context) {
        super(context);
    }

    public NotifyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotifyEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        setOnButtonClickListener(NotifyEditTextPreference.this);

        layout = view.findViewById(R.id.notify_dialog_input_layout);
        layout.setVisibility(View.VISIBLE);

        editText = view.findViewById(R.id.notify_dialog_input_edt);
        editText.setText(getPersistedString(text));
        if (inputType != NO_TYPE_ON_VIEW) {
            editText.setInputType(inputType);
        }

        return view;
    }

    @Override
    public boolean shouldDisableDependents() {
        return TextUtils.isEmpty(text) || super.shouldDisableDependents();
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setText(restoreValue ? getPersistedString(text) : (String) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    public void setText(String text) {
        // Always persist/notify the first time.
        final boolean changed = !TextUtils.equals(this.text, text);
        if (changed || !hasSetText) {
            this.text = text;
            hasSetText = true;
            persistString(text);
            if (changed) {
                notifyDependencyChange(shouldDisableDependents());
                notifyChanged();
            }
        }
    }

    public String getText() {
        return text;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setInputType(int type) {
        inputType = type;
    }

    @Override
    public void onClick(View view) {
        setText(editText.getText().toString());
        getDialog().dismiss();
    }
}
