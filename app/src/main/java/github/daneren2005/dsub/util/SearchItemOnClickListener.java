package github.daneren2005.dsub.util;

import github.daneren2005.dsub.domain.Artist;
import github.daneren2005.dsub.domain.MusicDirectory;

public interface SearchItemOnClickListener {
    void onArtistItemClick(Artist artist);
    void onSongItemClick(MusicDirectory.Entry song);
}
