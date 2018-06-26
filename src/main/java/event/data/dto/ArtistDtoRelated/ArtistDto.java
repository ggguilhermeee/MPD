package event.data.dto.ArtistDtoRelated;

import event.data.dto.TrackDtoRelated.ImageDto;
import event.data.dto.TrackDtoRelated.TagDto;
import event.data.dto.TrackDtoRelated.ToptagsDto;

import java.util.Arrays;

/**
 * Created by dmigu on 30/03/2017.
 */
public class ArtistDto {
    private String name;
    private String mbid;
    private String url;
    private ImageDto[] image;
    //private int ontour;
    //private StatsDto stats;
    //private SimilarDto similar;
    //This is actually TagDto
    //private ToptagsDto tags;
    private BioDto bio;


    @Override
    public String toString() {
        return "ArtistDto{" +
                "name='" + name + '\'' +
                ", mbid='" + mbid + '\'' +
                ", url='" + url + '\'' +
                ", image=" + Arrays.toString(image) +
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

    public ImageDto[] getImage() {
        return image;
    }

    public BioDto getBio() {
        return bio;
    }
}
