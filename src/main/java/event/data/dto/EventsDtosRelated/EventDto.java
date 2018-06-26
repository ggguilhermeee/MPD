package event.data.dto.EventsDtosRelated;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import event.data.dto.ArtistDtoRelated.ArtistDto;
import event.data.dto.VenuesDtosRelated.VenueDto;

/**
 * Created by fabio on 10/04/2017.
 */
public class EventDto {
    @SerializedName("@eventDate")
    private String eventDate;
    @SerializedName("@id")
    private String id;
    @SerializedName("@lastUpdated")
    private String lastUpdated;
    @SerializedName("@tour")
    private String tour;
    @SerializedName("@versionId")
    private String versionId;
    private EventArtistDto artist;
    private VenueDto venue;
    private JsonElement sets;



    public String getEventDate() {
        return eventDate;
    }

    public String getId() {
        return id;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public String getTour() {
        return tour;
    }

    public String getVersionId() {
        return versionId;
    }

    public EventArtistDto getArtist() {
        return artist;
    }

    public VenueDto getVenue() {
        return venue;
    }

    public JsonElement getSets() {
        return sets;
    }


    @Override
    public String toString() {
        return "EventDto{" +
                "eventDate='" + eventDate + '\'' +
                ", id='" + id + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                ", tour='" + tour + '\'' +
                ", versionId='" + versionId + '\'' +
                ", artist=" + artist +
                ", venue=" + venue +
                ", sets=" + sets +
                '}';
    }

}
