package event.data.dto.TrackDtoRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 05/04/2017.
 */
public class ImageDto {
    @SerializedName("#text")
    private String href;
    private String size;

    public String getHref() {
        return href;
    }

    public String getSize() {
        return size;
    }
}
