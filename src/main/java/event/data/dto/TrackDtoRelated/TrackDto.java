package event.data.dto.TrackDtoRelated;

import event.data.dto.ArtistDtoRelated.ArtistDto;

/**
 * Created by dmigu on 05/04/2017.
 */
public class TrackDto {
    private String name;
    private String mbid;
    private String url;
    private int duration;
    //private int listeners;
    //private int playcount;
    private ArtistDto artist;
    private AlbumDto album;
    //private ToptagsDto toptags;


    @Override
    public String toString() {
        return "TrackDto{" +
                "name='" + name + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", artist=" + artist +
                ", album=" + album +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getMbid() {
        return mbid;
    }

    public String getUrl() {
        return url;
    }

    public int getDuration() {
        return duration;
    }

    public ArtistDto getArtist() {
        return artist;
    }

    public AlbumDto getAlbum() {
        return album;
    }
}
