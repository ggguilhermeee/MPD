package event.model;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by dmigu on 30/03/2017.
 */
public class Event {
    private CompletableFuture<Artist> artist;
    private String artistName;
    private String eventDate;
    private String tour;
    private String[] trackNames;
    private CompletableFuture<Track>[] tracks;
    private String setlistId;

    public Event(CompletableFuture<Artist> artist, String artistName, String eventDate, String tour, String[] trackNames, CompletableFuture<Track>[] tracks, String setlistId) {
        this.artist = artist;
        this.artistName = artistName;
        this.eventDate = eventDate;
        this.tour = tour;
        this.trackNames = trackNames;
        this.tracks = tracks;
        this.setlistId = setlistId;
    }

    public CompletableFuture<Artist> getArtist() {
        return artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getTour() {
        return tour;
    }

    public String[] getTrackNames() {
        return trackNames;
    }

    public CompletableFuture<Track>[] getTracks() {
        return tracks;
    }

    public String getSetlistId() {
        return setlistId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "artist=" + getArtist().join() +
                ", artistName='" + artistName + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", tour='" + tour + '\'' +
                ", trackNames=" + Arrays.toString(trackNames) +
                ", tracks=" + getAllTracks() +
                ", setlistId='" + setlistId + '\'' +
                '}';
    }

    private String getAllTracks() {
        String s = "";
        for (CompletableFuture<Track> t: tracks ) {
            Track tr =t.join();
            if(tr!=null)
                s+=tr.getName();
        }
        return s;
    }
}
