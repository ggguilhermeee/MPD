package event.data.dto.EventsDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fabio on 10/04/2017.
 */
public class EventArtistDto {
    // Not sure if necessary
    @SerializedName("@mbid")
    private String mbid;
    @SerializedName("@name")
    private String name;

    private String url;

    public String getMbid() {
        return mbid;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
