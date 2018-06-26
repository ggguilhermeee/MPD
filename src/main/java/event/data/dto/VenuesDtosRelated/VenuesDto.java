package event.data.dto.VenuesDtosRelated;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by dmigu on 05/04/2017.
 */
public class VenuesDto {
    @SerializedName("@itemsPerPage")
    private String itemsPerPage;
    @SerializedName("@page")
    private int page;
    @SerializedName("@total")
    private int total;
    private VenueDto[] venue;

    public VenuesDto(String itemsPerPage, int page, int total, VenueDto[] venue) {
        this.itemsPerPage = itemsPerPage;
        this.page = page;
        this.total = total;
        this.venue = venue;
    }

    public String getItemsPerPage() {
        return itemsPerPage;
    }

    public int getPage() {
        return page;
    }

    public int getTotal() {
        return total;
    }

    public VenueDto[] getVenue() {
        return venue;
    }

    @Override
    public String toString() {
        return "VenuesDto{" +
                "itemsPerPage='" + itemsPerPage + '\'' +
                ", page=" + page +
                ", total=" + total +
                ", venue=" + Arrays.toString(venue) +
                '}';
    }
}
