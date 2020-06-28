package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.daneren2005.dsub.R;

public class GenrePageFragment extends NotifyFragment {
    private static final String TAG = GenrePageFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_genre_page, container, false);

        super.createNotifyCustomToolbar();
        return rootView;
    }
}
