package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.adapter.GenreListAdapter;
import github.daneren2005.dsub.domain.Genre;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class GenreListFragment extends NotifyFragment {
    private static final String TAG = GenreListFragment.class.getSimpleName();
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_genre_page, container, false);
        createNotifyCustomToolbar(true, false,
                false, true, true);
        recyclerView = rootView.findViewById(R.id.genre_page_recycler_view);

        loadGenres();

        return rootView;
    }

    private void loadGenres() {
        LoadGenreDataTask<List<Genre>> loadGenreTask = new LoadGenreDataTask<>(
                this, (musicService, listener) -> {
            try {
                return musicService.getGenres(false, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadGenres: exception = ", e);
                return null;
            }
        });
        loadGenreTask.execute();
    }

    private void updateGenres(List<Genre> loadedGenres) {
        if (loadedGenres.isEmpty()) {
            Log.e(TAG, "updateGenres: Loading failed");
            return;
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new GenreListAdapter() {
            @Override
            public void onBindViewHolder(@NonNull GenreListViewHolder holder, int position) {
                Genre genre = loadedGenres.get(position);
                holder.genreNameTextView.setText(genre.getName());
                holder.layout.setOnClickListener(v -> showGenreDetailPage(genre));
            }

            @Override
            public int getItemCount() {
                return loadedGenres.size();
            }
        });
    }

    private class LoadGenreDataTask<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;

        LoadGenreDataTask(SubsonicFragment fragment, GetDataListener<T> getDataListener) {
            super(fragment);
            LoadGenreDataTask.this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            updateGenres((List<Genre>) result);
        }
    }
}
