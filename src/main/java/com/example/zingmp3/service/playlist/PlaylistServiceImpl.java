package com.example.zingmp3.service.playlist;

import com.example.zingmp3.model.Playlist;
import com.example.zingmp3.model.Song;
import com.example.zingmp3.model.User;
import com.example.zingmp3.repository.IPlaylistRepository;
import com.example.zingmp3.repository.ISongRepository;
import com.example.zingmp3.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements IPlaylistService {
    @Autowired
    private UserService userService;

    @Autowired
    private IPlaylistRepository playlistRepository;

    @Autowired
    private ISongRepository songRepository;

    @Override
    public List<Playlist> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return playlistRepository.findAll(pageRequest).getContent();
    }

    @Override
    public Optional<Playlist> findById(Long id) {
        return playlistRepository.findById(id);
    }

    @Override
    public Playlist save(Playlist playlist) {
        User currentUser = userService.getCurrentUser();
        playlist.setUser(currentUser);
        Date date = new java.util.Date();
        playlist.setCreateAt(date);
        playlist.setStatus(true);
        return playlistRepository.save(playlist);
    }

    @Override
    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    @Override
    public Page<Playlist> findAllByStatus(Boolean status, Pageable pageable) {
        return playlistRepository.findAllSongByStatus(status, pageable);
    }

    @Override
    public List<Playlist> findAllByCreatedTimeOrderByCreatedTime() {
        return playlistRepository.findAllByCreatedTimeOrderByCreatedTime();
    }

    @Override
    public Playlist addSongToPlaylist(Long idSong, Long idPlaylist) {
        Song song = songRepository.findById(idSong).get();
        Playlist playlist = playlistRepository.findById(idPlaylist).get();
        List<Song> songs = playlist.getSongs();
        if (songs.contains(song)) {
            return null;
        }
        songs.add(song);
        playlist.setSongs(songs);
        playlistRepository.save(playlist);
        return playlist;
    }

    @Override
    public List<Playlist> findAllByViewsOrderByViews() {
        return playlistRepository.findAllByViewsOrderByViews();
    }

    @Override
    public List<Playlist> findAllByUserUsername(String username) {
        return playlistRepository.findAllByUserUsername(username);
    }
}
