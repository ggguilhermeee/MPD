package event;

import event.data.LastfmApi;
import event.data.SetlistApi;
import event.data.dto.EventsDtosRelated.EventDto;
import event.data.dto.VenuesDtosRelated.VenueDto;
import event.model.*;
import util.IRequest;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by fabio on 18/04/2017.
 */

public class VibeServiceCache extends VibeService{


    public VibeServiceCache(IRequest req) {
        super(req);
    }

    public VibeServiceCache(SetlistApi setlistApi, LastfmApi lastfmApi) {
        super(setlistApi, lastfmApi);
    }


    private final Map<String, Artist> artistCache = new HashMap<>();
    private final Map<Song, Track> trackCache = new HashMap<>();
    private final Map<String, Map<Integer,List<Event>>> eventCache = new HashMap<>();
    private final Map<String, Map<Integer,List<Venue>>> venueCache = new HashMap<>();


    @Override
    public Supplier<Stream<Venue>> searchVenues(String venuesName) {
        Function<VenueDto , Venue> transform =  e -> new Venue(e.getName(), getEvents(e.getId()),e.getId());
        Function<Integer , CompletableFuture<Stream<Venue>>> request = page -> {
            List<Venue> list = getOrCreate(venuesName , venueCache).get(page);
            if(list != null){
                CompletableFuture<Stream<Venue>> promise = new CompletableFuture<>();
                promise.complete(list.stream());
                return promise;
            }
            final List<Venue> list1 = new ArrayList<>();
            CompletableFuture<VenueDto[]> venuesDto = setlistApi.getVenues(venuesName, page);
            return venuesDto.thenApply(dtoArray ->{
                return Arrays.stream(dtoArray)
                        .map(transform)
                        .peek(
                                item -> {
                                    list1.add(item);
                                    if(list1.size() == dtoArray.length) {
                                        Map<Integer, List<Venue>> aux = getOrCreate(venuesName , venueCache);
                                        aux.put(page , list1);
                                        venueCache.put(venuesName,aux);
                                    }
                                }
                        );
            });
        };
        Spliterator<Venue> split = new GenericPagerSpliterator<>(request, 1);
        return () -> StreamSupport.stream(split,false);
    }

    private Stream<Event> getEventsStream(String venueId){
        Function<EventDto, Event> transform = (e) -> {
            String[] raw = getTracksNames(e.getSets());
            String [] trackNames = format(raw);
            CompletableFuture<Track>[] promises = new CompletableFuture[trackNames.length];
            for(int i  = 0; i<trackNames.length;++i){
                promises[i]= getTrack(e.getArtist().getName(),trackNames[i]);
            }
            return new Event(getArtist(e.getArtist().getMbid()),
                    e.getArtist().getName(),
                    e.getEventDate(),
                    e.getTour(),
                    trackNames,
                    promises,
                    e.getId());

        };

        Function<Integer,CompletableFuture<Stream<Event>>> requestAndTransform = page -> {
            List<Event> list = getOrCreate(venueId, eventCache).get(page);
            if (list != null) {
                CompletableFuture<Stream<Event>> promise = new CompletableFuture<>();
                promise.complete(list.stream());
                return promise;
            }
            final List<Event> list1 = new ArrayList<>();
            CompletableFuture<EventDto[]> eventsDto = setlistApi.getEvents(venueId, page);
            return eventsDto.thenApply(eventDtos -> {
                return Arrays.stream(eventDtos)
                        .map(transform)
                        .peek(
                                item -> {
                                    list1.add(item);
                                    if (list1.size() == eventDtos.length) {
                                        Map<Integer, List<Event>> aux = getOrCreate(venueId, eventCache);
                                        aux.put(page, list1);
                                        eventCache.put(venueId, aux);
                                    }
                                }
                        );
            });
        };
        Spliterator split = new GenericPagerSpliterator(requestAndTransform , 1);
        return StreamSupport.stream(split, false);
    }

    @Override
    public Supplier<Stream<Event>> getEvents(String venueId) {
        return ()->getEventsStream(venueId);
    }

    private static <R> Map<Integer, List<R>> getOrCreate(String id,Map<String, Map<Integer, List<R>>> cache) {
        Map<Integer, List<R>> page = cache.get(id);
        if(page == null) {
            page = new HashMap<>();
            cache.put(id, page);
        }
        return page;
    }


    @Override
    public CompletableFuture<Artist> getArtist(String artistId) {
        CompletableFuture<Artist> promise = new CompletableFuture<>();
        Artist res = artistCache.get(artistId);
        if(res == null) {
            promise = super.getArtist(artistId);
            promise.thenApply(artist -> artistCache.put(artistId,artist));
        }else
            promise.complete(res);
        return promise;
    }

    @Override
    public CompletableFuture<Track> getTrack(String artistName, String trackName) {
        CompletableFuture<Track> promise = new CompletableFuture<>();
        Song song = new Song(artistName, trackName);
        Track res = trackCache.get(song);
        if(res == null) {
            promise = super.getTrack(artistName, trackName);
            promise.thenApply(track -> trackCache.put(song,track));
        }else
            promise.complete(res);
        return promise;
    }

    private class Song {
        private String artistName;
        private String trackName;

        public Song(String artistName, String trackName) {
            this.artistName = artistName;
            this.trackName = trackName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Song song = (Song) o;

            if (!artistName.equals(song.artistName)) return false;
            return trackName.equals(song.trackName);
        }

        @Override
        public int hashCode() {
            int result = artistName.hashCode();
            result = 31 * result + trackName.hashCode();
            return result;
        }
    }
}

