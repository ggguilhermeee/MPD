package event.data.dto.VenuesDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 05/04/2017.
 */
public class CountryDto {
    @SerializedName("@code")
    private String code;
    @SerializedName("@name")
    private String name;

    public CountryDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
