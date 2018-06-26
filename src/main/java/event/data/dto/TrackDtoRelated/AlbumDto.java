package event.data.dto.TrackDtoRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 05/04/2017.
 */
public class AlbumDto {
    private String artist;
    private String title;
    private String mbid;
    private String url;
    private ImageDto[] image;
    @SerializedName("@attr")
    private PositionDto attr;

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
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

    public PositionDto getAttr() {
        return attr;
    }
}
