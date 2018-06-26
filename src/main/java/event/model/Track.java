package event.model;

import java.util.Arrays;

/**
 * Created by dmigu on 30/03/2017.
 */
public class Track {
    private String name;
    private String artistName;
    private String albumName;
    private String trackUrl;
    private String[] imagesUrl;
    private String albumUrl;
    private int duration;

    public Track(String name, String artistName, String albumName, String trackUrl, String[] imagesUrl, String albumUrl, int duration) {
        this.name = name;
        this.artistName = artistName;
        this.albumName = albumName;
        this.trackUrl = trackUrl;
        this.imagesUrl = imagesUrl;
        this.albumUrl = albumUrl;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public String[] getImagesUrl() {
        return imagesUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Track{" +
                "name='" + name + '\'' +
                ", artistName='" + artistName + '\'' +
                ", albumName='" + albumName + '\'' +
                ", trackUrl='" + trackUrl + '\'' +
                ", imagesUrl=" + Arrays.toString(imagesUrl) +
                ", albumUrl='" + albumUrl + '\'' +
                ", duration=" + duration +
                '}';
    }
}
