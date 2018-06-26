package event.data.dto.VenuesDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 05/04/2017.
 */
public class CityDto {
    @SerializedName("@id")
    private String id;
    @SerializedName("@name")
    private String name;
    @SerializedName("@state")
    private String state;
    @SerializedName("@stateCode")
    private String stateCode;
    private CoordenateDto coords;
    private CountryDto country;

}
