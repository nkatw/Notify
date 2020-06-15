package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.daneren2005.dsub.R;

public class MainPageFragment extends SubsonicFragment {
    private static final String TAG = MainPageFragment.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_main_page, container, false);
        Log.d(TAG, "onCreateView: called");
        return rootView;
    }
}
