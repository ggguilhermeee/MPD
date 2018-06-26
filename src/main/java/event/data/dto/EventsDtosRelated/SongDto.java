package event.data.dto.EventsDtosRelated;

import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 13/04/2017.
 */
public class SongDto {
    @SerializedName("@name")
    private String name;

    public String getName() {
        return name;
    }
}
