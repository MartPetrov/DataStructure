package core;

import models.Track;

import java.util.*;
import java.util.stream.Collectors;

public class RePlayerImpl implements RePlayer {

    private Map<String, Track> allTracks;
    private Map<String, Map<String, Track>> AllAlbumsByTitleTracks;
    private ArrayDeque<Track> Queue;


    public RePlayerImpl() {

        this.allTracks = new HashMap<>();
        this.AllAlbumsByTitleTracks = new TreeMap<>();
        this.Queue = new ArrayDeque<>();
    }

    @Override
    public void addTrack(Track track, String album) {
        String id = track.getId();
        if (!allTracks.containsKey(id)) {
            AllAlbumsByTitleTracks.putIfAbsent(album, new HashMap<>());
            allTracks.put(id, track);
            String title = track.getTitle();
            AllAlbumsByTitleTracks.get(album).putIfAbsent(title, track);
        }
    }

    @Override
    public void removeTrack(String trackTitle, String albumName) {
        if (!AllAlbumsByTitleTracks.containsKey(albumName)) {
            throw new IllegalArgumentException();
        }
        Track track = AllAlbumsByTitleTracks.get(albumName).remove(trackTitle);
        if (track == null) {
            throw new IllegalArgumentException();
        }
        allTracks.remove(track.getId());
       if (Queue.contains(track)) {
           Queue.remove(track);
       }
    }

    private void checkAlbumAndTrack(String trackTitle, String albumName) {
        boolean albumCont = AllAlbumsByTitleTracks.containsKey(albumName);
        boolean trackCont = AllAlbumsByTitleTracks.get(albumName).containsKey(trackTitle);
        if (!albumCont || !trackCont) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean contains(Track track) {
        return this.allTracks.containsKey(track.getId());
    }

    @Override
    public int size() {
        return this.allTracks.size();
    }

    @Override
    public Track getTrack(String title, String albumName) {
        checkAlbumAndTrack(title, albumName);
        return AllAlbumsByTitleTracks.get(albumName).get(title);
    }

    @Override
    public Iterable<Track> getAlbum(String albumName) {
        if (!AllAlbumsByTitleTracks.containsKey(albumName)) {
            throw new IllegalArgumentException();
        }
        return AllAlbumsByTitleTracks.get(albumName).values().stream()
                .sorted((l, r) -> Integer.compare(r.getPlays(), l.getPlays()))
                .collect(Collectors.toList());
    }

    @Override
    public void addToQueue(String trackName, String albumName) {
        checkAlbumAndTrack(trackName, albumName);
        Queue.offer(AllAlbumsByTitleTracks.get(albumName).get(trackName));
    }


    @Override
    public Track play() {
        Track currentTrack = Queue.poll();
        if (currentTrack == null) {
            throw new IllegalArgumentException();
        }
        currentTrack.setPlays(currentTrack.getPlays() + 1);
        return currentTrack;
    }

    @Override
    public Iterable<Track> getTracksInDurationRangeOrderedByDurationThenByPlaysDescending(int lowerBound, int upperBound) {
        return this.allTracks.values().stream()
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
        for (Map<String, Track> map : AllAlbumsByTitleTracks.values()) {
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
        for (Map.Entry<String, Map<String, Track>> entry : AllAlbumsByTitleTracks.entrySet()) {
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
