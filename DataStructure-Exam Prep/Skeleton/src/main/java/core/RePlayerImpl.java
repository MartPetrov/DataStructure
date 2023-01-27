package core;

import models.Track;

import java.util.*;
import java.util.stream.Collectors;

public class RePlayerImpl implements RePlayer {

    private Map<String, Track> allTracksByID;
    private Map<String, Map<String, Track>> albumsWithTracksByTitle;
    private ArrayDeque<Track> listeningQueue;


    public RePlayerImpl() {

        this.allTracksByID = new HashMap<>();
        this.albumsWithTracksByTitle = new TreeMap<>();
        this.listeningQueue = new ArrayDeque<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        String id = track.getId();
        if (!allTracksByID.containsKey(id)) {
            allTracksByID.put(id, track);
            albumsWithTracksByTitle.putIfAbsent(album, new HashMap<>());
            String title = track.getTitle();
            albumsWithTracksByTitle.get(album).putIfAbsent(title, track);
        }
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        if (!albumsWithTracksByTitle.containsKey(albumName)) {
            throw new IllegalArgumentException();
        }
        Track track = albumsWithTracksByTitle.get(albumName).remove(trackTitle);
        if (track == null) {
            throw new IllegalArgumentException();
        }
        allTracksByID.remove(track.getId());
       if (listeningQueue.contains(track)) {
           listeningQueue.remove(track);
       }
    }

    private void checkAlbumAndTrack(String trackTitle, String albumName) {
        boolean albumCont = albumsWithTracksByTitle.containsKey(albumName);
        boolean trackCont = albumsWithTracksByTitle.get(albumName).containsKey(trackTitle);
        if (!albumCont || !trackCont) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean contains(Track track) {
        return this.allTracksByID.containsKey(track.getId());
    }

    @Override
    public int size() {
        return this.allTracksByID.size();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        checkAlbumAndTrack(title, albumName);
        return albumsWithTracksByTitle.get(albumName).get(title);
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        if (!albumsWithTracksByTitle.containsKey(albumName)) {
            throw new IllegalArgumentException();
        }
        return albumsWithTracksByTitle.get(albumName).values().stream()
                .sorted((l, r) -> Integer.compare(r.getPlays(), l.getPlays()))
                .collect(Collectors.toList());
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        checkAlbumAndTrack(trackName, albumName);
        listeningQueue.offer(albumsWithTracksByTitle.get(albumName).get(trackName));
    }


    @Override
    public Track play() {
        Track currentTrack = listeningQueue.poll();
        if (currentTrack == null) {
            throw new IllegalArgumentException();
        }
        currentTrack.setPlays(currentTrack.getPlays() + 1);
        return currentTrack;
    }

    @Override
    public Iterable<Track> getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(int lowerBound, int upperBound) {
        return this.allTracksByID.values().stream()
                .filter(track -> lowerBound <= track.getDurationInSeconds()
                        && track.getDurationInSeconds() <= upperBound)
                .sorted((l,r) -> {
                    if (l.getDurationInSeconds() != r.getDurationInSeconds()) {
                        return l.getDurationInSeconds() - r.getDurationInSeconds();
                    }
                    return r.getPlays() - l.getPlays();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Track> getTracksOrderedByAlbumNameThenByPlaysDescendingThenByDurationDescending() {
        ArrayList<Track> result = new ArrayList<>();
        for (Map<String, Track> map : albumsWithTracksByTitle.values()) {
            List<Track> collect = map.values().stream().sorted((l, r) -> {
                if (l.getPlays() == r.getPlays()) {
                    return Integer.compare(r.getDurationInSeconds(), l.getDurationInSeconds());
                }
                return Integer.compare(r.getPlays(), l.getPlays());
            }).collect(Collectors.toList());
            result.addAll(collect);
        }
        return result;
    }

    @Override
    public Map<String, List<Track>> getDiscography(String artistName) {
        Map<String, List<Track>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Track>> entry : albumsWithTracksByTitle.entrySet()) {
            String albumName = entry.getKey();
            List<Track> currentTracks = entry.getValue().values().stream()
                    .filter(track -> track.getArtist().equals(artistName))
                    .collect(Collectors.toList());
            if (!currentTracks.isEmpty()) {
                result.put(albumName, currentTracks);
            }
        }
        if (result.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return result;
    }
}
