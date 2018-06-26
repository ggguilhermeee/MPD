package event.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import event.data.dto.EventsDtosRelated.EventDto;
import event.data.dto.EventsDtosRelated.EventInitDto;
import event.data.dto.EventsDtosRelated.EventsInitDto;
import event.data.dto.VenuesDtosRelated.VenueDto;
import event.data.dto.VenuesDtosRelated.VenuesInitDto;
import util.IRequest;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by dmigu on 30/03/2017.
 */
public class SetlistApi {

    private static final String SET_HOST = "https://api.setlist.fm";
    private static final String SET_VENUES = "/rest/0.1/search/venues.json";
    private static final String SET_VENUES_ARGS = "?cityName=%s&p=%s";
    private static final String SET_VENUE_SETLIST = "/rest/0.1/venue/%s/setlists.json";
    private static final String SET_VENUES_SETLIST_ARGS = "?p=%s";
    private static final String SET_VENUE_SETLIST_SOLO = "/rest/0.1/setlist/%s.json";


    private final IRequest req;
    private Gson gson = new Gson();

    public SetlistApi(IRequest req) {
        this.req = req;
    }

    public CompletableFuture<VenueDto[]> getVenues(String cityName){
        return getVenues(cityName,1);
    }

    public CompletableFuture<VenueDto[]> getVenues(String cityName, int page){
        String path = SET_HOST + SET_VENUES + SET_VENUES_ARGS;
        String url = String.format(path, cityName, page);
        return req.getContent(url)
                //.thenApply(seq -> seq.peek(System.out::println))
                .thenApply(seq -> gson.fromJson(seq.collect(Collectors.joining(" ")), VenuesInitDto.class)
                .getVenues()
                .getVenue()
                );
    }

    public CompletableFuture<EventDto[]>  getEvents(String venueId){
        return getEvents(venueId,1);
    }

    public CompletableFuture<EventDto[]> getEvents(String venueId, int page){
        String path = SET_HOST + SET_VENUE_SETLIST + SET_VENUES_SETLIST_ARGS;
        String url = String.format(path, venueId, page);
       return req.getContent(url)
                .thenApply(seq -> gson.fromJson(seq.collect(Collectors.joining(" ")), EventsInitDto.class)
                        .getSetlists()
                        .getEvents()
                )
               .thenApply(jsonElement -> {
                   if(jsonElement.isJsonArray())
                       return resolveArray(jsonElement);
                   else
                       return resolveObject(jsonElement);
               });
    }

    public CompletableFuture<EventDto> getEvent(String eventId){
        String path = SET_HOST + SET_VENUE_SETLIST_SOLO;
        String url = String.format(path,eventId);
        return req.getContent(url)
                .thenApply(stringStream -> gson.fromJson(stringStream.collect(Collectors.joining(" ")), EventInitDto.class)
                            .getSetlist());
    }

    private EventDto[] resolveObject(JsonElement elem) {
        JsonObject obj = elem.getAsJsonObject();
        EventDto event = gson.fromJson(obj,EventDto.class);
        return new EventDto[]{event};
    }

    private EventDto[] resolveArray(JsonElement elem) {
        JsonArray array = elem.getAsJsonArray();
        EventDto[] events = new EventDto[array.size()];
        for (int i = 0; i < events.length; i++) {
            events[i] = gson.fromJson(array.get(i),EventDto.class);
        }
        return events;
    }
}
