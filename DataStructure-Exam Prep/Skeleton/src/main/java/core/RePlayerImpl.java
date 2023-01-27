package core;

import models.Track;

import java.util.*;

public class RePlayerImpl implements RePlayer {
    public class Album {

        private final String nameOfAlbum;
        private final List<Track> trackInAlbum;
        private int size;

        public Album(String nameOfAlbums) {
            this.nameOfAlbum = nameOfAlbums;
            this.trackInAlbum = new LinkedList<>();
        }


        public String getNameOfAlbum() {
            return nameOfAlbum;
        }

        public void addTrack(Track track) {
            this.trackInAlbum.add(track);
            this.size++;
        }

        public List<Track> getTrackInAlbum() {
            return trackInAlbum;
        }

        public int getSize() {
            return size;
        }

        public Track getTrack(String trackName) {
            return this.trackInAlbum.stream().filter(track -> track.getTitle().equals(trackName)).findFirst().get();
        }
    }

    private final ArrayDeque<Track> playersTracks;
    private final Map<String, Album> playersAlbums;


    public RePlayerImpl() {
        playersTracks = new ArrayDeque<>();
        playersAlbums = new LinkedHashMap<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        Album currentAlbum = this.playersAlbums.get(album);
        if (currentAlbum == null) {
            Album newAlbum = new Album(album);
            newAlbum.addTrack(track);
            this.playersAlbums.put(album, newAlbum);
        } else {
            currentAlbum.addTrack(track);
            this.playersAlbums.put(album, currentAlbum);
        }
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        Album currentAlbum = this.playersAlbums.get(albumName);
        if (currentAlbum == null) {
            throw new IllegalArgumentException();
        }
        boolean flag = false;
        while (currentAlbum.getTrackInAlbum().iterator().hasNext()) {
            Track next = currentAlbum.getTrackInAlbum().iterator().next();
            if (next.getTitle().equals(trackTitle)) {
                flag = true;
            }
        }
        if (!flag) {
            throw new IllegalArgumentException();
        }
        currentAlbum.getTrackInAlbum().remove(trackTitle);

        if (this.playersTracks.contains(trackTitle)) {
            this.playersTracks.remove(trackTitle);
        }

    }

    @Override
    public boolean contains(Track track) {
        return this.playersTracks.contains(track);
    }

    @Override
    public int size() {
        return this.playersAlbums.values().stream().mapToInt(Album::getSize).sum();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        Album currentAlbum = this.playersAlbums.get(albumName);
        if (currentAlbum == null) {
            throw new IllegalArgumentException();
        }
        return currentAlbum.getTrack(title);
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        Optional<Album> currentAlbum = this.playersAlbums.values().stream().filter(album -> album.nameOfAlbum.equals(albumName)).findFirst();
        if (currentAlbum.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return currentAlbum.get().getTrackInAlbum();
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        Album album = this.playersAlbums.get(albumName);
        if (album == null) {
            throw new IllegalArgumentException();
        }
        Optional<Track> currenTrack = album.trackInAlbum.stream().filter(track -> track.getTitle().equals(trackName)).findFirst();

        if (currenTrack.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Track toAdd = currenTrack.get();
        album.addTrack(toAdd);
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
