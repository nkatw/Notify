package github.daneren2005.dsub.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.adapter.NotifySearchAdapter;
import github.daneren2005.dsub.domain.Artist;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.domain.SearchCritera;
import github.daneren2005.dsub.domain.SearchResult;
import github.daneren2005.dsub.service.MusicService;
import github.daneren2005.dsub.service.MusicServiceFactory;
import github.daneren2005.dsub.util.GetDataListener;
import github.daneren2005.dsub.util.SearchItemOnClickListener;
import github.daneren2005.dsub.util.TabBackgroundTask;

public class NotifySearchFragment extends NotifyFragment implements SearchItemOnClickListener {
    private static final String TAG = NotifySearchFragment.class.getSimpleName();

    private static final int MAX_ARTISTS_QUANTITY = 50;
    private static final int MAX_SONGS_QUANTITY = 50;

    private RecyclerView recyclerView;
    private NotifySearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notify_search_page, container, false);
        createNotifyCustomToolbar(true, true,
                false, false, true);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loadSearchResult(editable.toString());
            }
        });

        recyclerView = rootView.findViewById(R.id.search_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new NotifySearchAdapter(
                getString(R.string.search_artists),
                getString(R.string.search_albums),
                getString(R.string.search_songs),
                this
        );
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void loadSearchResult(String query) {
        new LoadSearchData<>(this, ((musicService, listener) -> {
            try {
                SearchCritera criteria = new SearchCritera(query + "*", MAX_ARTISTS_QUANTITY, 0, MAX_SONGS_QUANTITY);
                MusicService service = MusicServiceFactory.getMusicService(context);
                return service.search(criteria, context, listener);
            } catch (Exception e) {
                Log.e(TAG, "loadSearchResult: exception = ", e);
                return null;
            }
        })).execute();
    }

    private void updateSearchResult(SearchResult searchResult) {
        adapter.updateSearchResult(searchResult);
    }

    @Override
    public void onArtistItemClick(Artist artist) {
        showArtistPageFragment(artist.getId(), artist.getName());
    }

    @Override
    public void onSongItemClick(MusicDirectory.Entry song) {
        // TODO: show player page
        showPlayerPage();
    }

    private class LoadSearchData<T> extends TabBackgroundTask<T> {
        GetDataListener<T> getDataListener;

        LoadSearchData(SubsonicFragment fragment, GetDataListener<T> getDataListener) {
            super(fragment);
            LoadSearchData.this.getDataListener = getDataListener;
        }

        @Override
        protected T doInBackground() throws Throwable {
            MusicService musicService = MusicServiceFactory.getMusicService(context);
            return getDataListener.getObjects(musicService, this);
        }

        @Override
        protected void done(T result) {
            updateSearchResult((SearchResult) result);
        }
    }
}
