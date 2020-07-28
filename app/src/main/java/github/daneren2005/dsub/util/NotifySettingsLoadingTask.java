package github.daneren2005.dsub.util;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.activity.SubsonicActivity;
import github.daneren2005.dsub.dialogFragment.NotifyProgressBarDialogFragment;

public abstract class NotifySettingsLoadingTask<T> extends BackgroundTask<T> {

    private final Activity tabActivity;
    private NotifyProgressBarDialogFragment loading;
    private final boolean cancellable;
    private final FragmentManager fragmentManager;

    public NotifySettingsLoadingTask(Activity activity, FragmentManager fragmentManager) {
        super(activity);
        tabActivity = activity;
        this.fragmentManager = fragmentManager;
        this.cancellable = true;
    }

    public NotifySettingsLoadingTask(Activity activity, FragmentManager fragmentManager, final boolean cancellable) {
        super(activity);
        tabActivity = activity;
        this.fragmentManager = fragmentManager;
        this.cancellable = cancellable;
    }

    @Override
    public void execute() {
        loading = NotifyProgressBarDialogFragment.show(fragmentManager, "",
                tabActivity.getResources().getString(R.string.service_connecting),
                true, cancellable, new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        cancel();
                    }
                });

        queue.offer(task = new Task() {
            @Override
            public void onDone(T result) {
                if (loading.isShowing()) {
                    loading.dismiss();
                }
                done(result);
            }

            @Override
            public void onError(Throwable t) {
                if (loading.isShowing()) {
                    loading.dismiss();
                }
                error(t);
            }
        });
    }

    @Override
    public boolean isCancelled() {
        return (tabActivity instanceof SubsonicActivity && ((SubsonicActivity) tabActivity).isDestroyedCompat()) || cancelled.get();
    }

    @Override
    public void updateProgress(final String message) {
        if (!cancelled.get()) {
            getHandler().post(new Runnable() {
                @Override
                public void run() {
                    loading.setContent(message);
                }
            });
        }
    }
}
