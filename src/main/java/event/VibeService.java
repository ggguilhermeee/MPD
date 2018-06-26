package event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import event.data.LastfmApi;
import event.data.SetlistApi;
import event.data.dto.ArtistDtoRelated.ArtistDto;
import event.data.dto.EventsDtosRelated.EventDto;
import event.data.dto.TrackDtoRelated.ImageDto;
import event.data.dto.TrackDtoRelated.TrackDto;
import event.data.dto.VenuesDtosRelated.VenueDto;
import event.model.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.IRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by dmigu on 30/03/2017.
 */
public class VibeService {
    protected final SetlistApi setlistApi;
    protected final LastfmApi lastfmApi;
    protected final Map<String,Event> eventCache = new HashMap<>();

    private final Gson gson = new Gson();


    public VibeService(SetlistApi setlistApi, LastfmApi lastfmApi) {
        this.lastfmApi = lastfmApi;
        this.setlistApi = setlistApi;
    }


    public VibeService(IRequest req) {
        this.lastfmApi = new LastfmApi(req);
        this.setlistApi = new SetlistApi(req);
    }

    public Supplier<Stream<Venue>> searchVenues(String venuesName){
        Function<Integer,CompletableFuture<Stream<Venue>>> requestAndTransform = (page) ->{
            CompletableFuture<VenueDto[]> venusDtos = setlistApi.getVenues(venuesName, page);
            return  venusDtos.thenApply(
                    venueDtoArray -> Arrays
                                    .stream(venueDtoArray)
                                    .map(venueDto -> new Venue(venueDto.getName(),getEvents(venueDto.getId()),venueDto.getId())));

        };

        Spliterator<Venue> split = new GenericPagerSpliterator<>(requestAndTransform,1);
        return () -> StreamSupport.stream(split,false);
    }


    public Supplier<Stream<Event>> getEvents(String venueId) {
        return ()-> getEventsStream(venueId);
    }

    private Stream<Event> getEventsStream(String venueId){
        Function<EventDto, Event> transform = (e) -> {
            String[] raw = getTracksNames(e.getSets());
            String [] trackNames = format(raw);
            CompletableFuture<Track>[] promises = new CompletableFuture[trackNames.length];
            for(int i  = 0; i<trackNames.length;++i){
                promises[i]=getTrack(e.getArtist().getName(),trackNames[i]);
            }
            return new Event(getArtist(e.getArtist().getMbid()),
                    e.getArtist().getName(),
                    e.getEventDate(),
                    e.getTour(),
                    trackNames,
                    promises,
                    e.getId());

        };

        Function<Integer,CompletableFuture<Stream<Event>>> requestAndTransform = (page) ->{
            CompletableFuture<EventDto[]> eventsDto = setlistApi.getEvents(venueId, page);
            return eventsDto.thenApply(eventDtoArray -> Arrays.stream(eventDtoArray)
                    .map(transform));

        };
        Spliterator<Event> split = new GenericPagerSpliterator<>(requestAndTransform,1);
        return StreamSupport.stream(split, false);
    }

    protected String[] format(String[] raw) {
        String[] toRet = new String[raw.length];
        for (int i = 0; i < toRet.length; i++) {
            String songName = raw[i];
            if(songName.contains("#")||songName.contains("&")||songName.contains(" ")){
                songName=songName
                        .replace("&","and")
                        .replace(" ","+")
                        .replace("#","");
            }
            toRet[i] = songName;
        }
        return toRet;
    }

    protected String[] getTracksNames(JsonElement sets) {
        if(sets.isJsonPrimitive())
            return new String[0];
        else {
            List<String> res = new ArrayList<>();
            JsonElement set = sets.getAsJsonObject().get("set");
            if(set.isJsonObject()) insertSongsInto(set, res);
            else {
                set
                        .getAsJsonArray()
                        .forEach(elem -> insertSongsInto(elem, res));
            }
            return res.toArray(new String[res.size()]);
        }
    }

    private void insertSongsInto(JsonElement elem, List<String> res) {
        JsonElement song = elem.getAsJsonObject().get("song");
        if(song.isJsonObject()) {
            res.add(song.getAsJsonObject().get("@name").getAsString());
        } else {
            JsonArray songs = song.getAsJsonArray();
            songs.forEach(item -> res.add(item.getAsJsonObject().get("@name").getAsString()));
        }
    }

    public CompletableFuture<Artist> getArtist(String artistId){
        CompletableFuture<ArtistDto> res = lastfmApi.getArtistInfo(artistId);

        return res.thenApply(artistDto -> {
            if(artistDto==null)
                return null;

            return new Artist(artistDto.getName() ,
                    artistDto.getBio().getSummary() ,
                    artistDto.getUrl(),
                    imageToString(artistDto.getImage()),
                    artistDto.getMbid());
        });
    }

    public CompletableFuture<Event> getEvent(String id){
        CompletableFuture<Event> promise = new CompletableFuture<>();
        if(eventCache.containsKey(id)){
             promise.complete(eventCache.get(id));
             return promise;
        }

        promise = setlistApi.getEvent(id).thenApply(eventDto -> {
                    String[] raw = getTracksNames(eventDto.getSets());
                    String[] trackNames = format(raw);
                    CompletableFuture<Track>[] promises = new CompletableFuture[trackNames.length];
                    for (int i = 0; i < trackNames.length; ++i) {
                        promises[i] = getTrack(eventDto.getArtist().getName(), trackNames[i]);
                    }
                    return new Event(getArtist(eventDto.getArtist().getMbid()),
                            eventDto.getArtist().getName(),
                            eventDto.getEventDate(),
                            eventDto.getTour(),
                            trackNames,
                            promises,
                            eventDto.getId());
                });

        eventCache.put(id, promise.join());
        return promise;
    }


    public CompletableFuture<Track> getTrack(String artistName, String trackName) {
        CompletableFuture<TrackDto> res = lastfmApi.getTrackInfo(artistName, trackName);
        return res.thenApply(this::checkTrackDto);
    }

    private Track checkTrackDto(TrackDto trackDto) {
        if(trackDto ==null)
            return null;
        String title;
        String [] image = null;
        String url;
        if(trackDto.getAlbum() == null) {
            title = "No available album";
            url = "No available album";
        }
        else{
            title = trackDto.getAlbum().getTitle();
            image = imageToString(trackDto.getAlbum().getImage());
            url = trackDto.getAlbum().getUrl();
        }
        return new Track(trackDto.getName() ,
                trackDto.getArtist().getName(),
                title,
                trackDto.getUrl(),
                image,
                url,
                trackDto.getDuration());
    }

    private String[] imageToString(ImageDto[] image) {
        String res[] = new String[image.length];
        for (int i = 0; i < image.length; i++)
            res[i] = image[i].getHref();

        return res;
    }


}
