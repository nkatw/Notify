package github.daneren2005.dsub.util;

import github.daneren2005.dsub.service.MusicService;

public interface GetDataListener<T> {
    T getObjects(MusicService musicService, ProgressListener listener) throws Exception;
}
