package event.data.dto.VenuesDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 05/04/2017.
 */
public class CoordenateDto {
    @SerializedName("@lat")
    private float lat;
    @SerializedName("@long")
    private float longitude;

    public CoordenateDto(float lat, float longitude) {
        this.lat = lat;
        this.longitude = longitude;
    }
}
