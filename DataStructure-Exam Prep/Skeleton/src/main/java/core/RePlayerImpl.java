package core;

import models.Track;

import java.util.*;

public class RePlayerImpl implements RePlayer {

    private final Set<Map<String,Track>> albums;
    private final ArrayDeque<Track> player;


    public RePlayerImpl() {
        this.albums = new LinkedHashSet<>();
        this.player = new ArrayDeque<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        Map<String,Track> currentAlbum = albums.
        if (currentAlbum == null) {
            currentAlbum = new LinkedHashMap<>();
        }
        currentAlbum.put(track.getTitle(),track);
        this.albums.put(album, currentAlbum);
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        Map<String,Track> currentAlbum = albums.get(albumName);
        if (currentAlbum == null || !currentAlbum.containsKey(trackTitle)) {
            throw new IllegalArgumentException();
        }
        this.albums.remove(trackTitle);
        this.player.remove(trackTitle);
    }

    @Override
    public boolean contains(Track track) {
        return albums.values().
    }

    @Override
    public int size() {
        return this.albums.values().stream().mapToInt(Map::size).sum();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        return null;
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        return null;
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        Map<String,Track> currentAlbum = albums.get(albumName);
        if (currentAlbum == null || !currentAlbum.containsKey(trackName)) {
            throw new IllegalArgumentException();
        }
        Track currentTrack = currentAlbum.get(trackName);
        player.offer(currentTrack);

    }

    @Override
    public Track play() {
        return null;
    }

    @Override
    public Iterable<Track> getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(int lowerBound, int upperBound) {
        return null;
    }

    @Override
    public Iterable<Track> getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending() {
        return null;
    }

    @Override
    public Map<String, List<Track>> getDiscography(String artistName) {
        return null;
    }
}
