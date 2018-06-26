package event.data.dto.VenuesDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dmigu on 30/03/2017.
 */
public class VenueDto {
    @SerializedName("@id")
    private String id;
    @SerializedName("@name")
    private String name;
    private CityDto city;
    private String url;

    public VenueDto(String id, String name, CityDto city, String url) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.url = url;
    }

    @Override
    public String toString() {
        return "VenueDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city=" + city +
                ", url='" + url + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CityDto getCity() {
        return city;
    }

    public String getUrl() {
        return url;
    }
}
