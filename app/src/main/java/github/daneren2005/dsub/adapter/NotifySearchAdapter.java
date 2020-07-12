package github.daneren2005.dsub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import github.daneren2005.dsub.R;
import github.daneren2005.dsub.domain.Artist;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.domain.SearchResult;
import github.daneren2005.dsub.util.SearchItemOnClickListener;

public class NotifySearchAdapter extends RecyclerView.Adapter<NotifySearchAdapter.NotifySearchViewHolder> {
    private static final String TAG = NotifySearchAdapter.class.getSimpleName();
    private static final String SONG_ITEM_SUBTITLE_FORMAT = "%s | %s";

    private String artistStrFromRes, albumStrFromRes, songStrFromRes;
    private List<NotifySearchItem> searchItems = new ArrayList<>();
    private SearchItemOnClickListener listener;

    public NotifySearchAdapter(String artistStrFromRes, String albumStrFromRes, String songStrFromRes,
                               SearchItemOnClickListener searchItemOnClickListener) {
        this.artistStrFromRes = artistStrFromRes;
        this.albumStrFromRes = albumStrFromRes;
        this.songStrFromRes = songStrFromRes;
        this.listener = searchItemOnClickListener;
    }

    @NonNull
    @Override
    public NotifySearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.search_item_view, parent, false);
        return new NotifySearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifySearchViewHolder holder, int position) {
        NotifySearchItem item = searchItems.get(position);

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        holder.layout.setOnClickListener(v -> {
            switch (item.itemType) {
                case artist:
                    Artist artist = (Artist) item.object;
                    listener.onArtistItemClick(artist);
                    break;
                case song:
                    MusicDirectory.Entry song = (MusicDirectory.Entry) item.object;
                    listener.onSongItemClick(song);
                case album:
                default:
                    break;
            }
        });
    }

    private void filterData(SearchResult result) {
        if (result.hasArtists()) {
            List<Artist> artists = result.getArtists();
            for (Artist artist : artists) {
                NotifySearchItem item =
                        new NotifySearchItem(artist.getName(),
                                artistStrFromRes,
                                artist, ItemType.artist);
                searchItems.add(item);
            }
        }

        if (result.hasSongs()) {
            List<MusicDirectory.Entry> songs = result.getSongs();
            for (MusicDirectory.Entry song : songs) {
                NotifySearchItem item =
                        new NotifySearchItem(song.getTitle(),
                                String.format(SONG_ITEM_SUBTITLE_FORMAT, albumStrFromRes, song.getAlbum()),
                                song, ItemType.song);
                searchItems.add(item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public void updateSearchResult(SearchResult result) {
        searchItems.clear();
        filterData(result);
        notifyDataSetChanged();
    }


    protected class NotifySearchViewHolder extends RecyclerView.ViewHolder {
        // TODO: Find better way to encapsulate members
        public ViewGroup layout;
        public TextView title, subtitle;

        NotifySearchViewHolder(View itemView) {
            super(itemView);
            NotifySearchViewHolder.this.layout = itemView.findViewById(R.id.search_item_layout);
            NotifySearchViewHolder.this.title = itemView.findViewById(R.id.search_item_title);
            NotifySearchViewHolder.this.subtitle = itemView.findViewById(R.id.search_item_subtitle);
        }
    }

    public class NotifySearchItem {
        private String title, subtitle;
        private Object object;
        private ItemType itemType;

        public NotifySearchItem(String title, String subtitle, Object object, ItemType itemType) {
            this.title = title;
            this.subtitle = subtitle;
            this.object = object;
            this.itemType = itemType;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public Object getObject() {
            return object;
        }
    }

    public enum ItemType {
        artist, album, song
    }
}
