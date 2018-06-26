package event.data.dto.EventsDtosRelated;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fabio on 10/04/2017.
 */
public class SetListsDto {
    @SerializedName("@itemsPerPage")
    private String itemsPerPage;
    @SerializedName("@page")
    private int page;
    @SerializedName("@total")
    private int total;
    private JsonElement setlist ;


    public String getItemsPerPage() {
        return itemsPerPage;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

    public JsonElement getEvents() {
        return setlist;
    }
}
