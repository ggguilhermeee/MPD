package event.controllers;

import event.VibeService;
import event.model.Artist;
import event.model.Event;
import event.model.Track;
import event.model.Venue;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.ClassLoader.getSystemResource;
import static java.nio.file.Files.lines;

/**
 * Created by user on 15/06/2017.
 */
public class EventController {
    private final String root;

    private final VibeService api;
    private final String searchVenuesView = load("views/searchVenues.html");
    private final String searchVenuesRowView = load("views/searchVenuesRow.html");
    private final String eventsView = load("views/events.html");
    private final String eventsRowView = load("views/eventsRow.html");
    private final String eventDetailsView = load("views/eventDetails.html");
    private final String artistDetailView = load("views/artistDetail.html");
    private final String songsView = load("views/songs.html");
    private final String songsRowView = load("views/songsRow.html");

    private final ConcurrentHashMap<String, String>
            eventsCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, CompletableFuture<String>>
            artistCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, String>
            songsCache = new ConcurrentHashMap<>();



    public EventController(VibeService api) {
        this.api = api;
        try {
            this.root = getSystemResource(".").toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public String searchVenues(HttpServletRequest req){
        String location = req.getParameter("name");
        String rows = api
                .searchVenues(location)
                .get()
                .map(venue -> String.format(searchVenuesRowView,
                        venue.getName(),
                        linkForEvents(venue)))
                .collect(Collectors.joining());
        return String.format(searchVenuesView, rows);
    }

    public String events(HttpServletRequest req){
        String venueId = req.getParameter("venueId");
        String rows = api.getEvents(venueId)
                .get()
                .map(event -> String.format(eventsRowView,
                        event.getArtistName(),
                        event.getEventDate(),
                        event.getSetlistId(),
                        event.getTour(),
                        linkForEventDetail(event)))
                .collect(Collectors.joining());
        return String.format(eventsView, rows);
    }


    public String eventDetails(HttpServletRequest req){
        String eventId = req.getParameter("eventId");
        Event event = api.getEvent(eventId)
                .join();


        //Artist page done here
        CompletableFuture<Artist> artist = event.getArtist();
        artistCache.putIfAbsent(eventId,requestArtist(artist));

        //Songs done here
        CompletableFuture<Track>[] tracks = event.getTracks();
        songsCache.putIfAbsent(eventId, requestSong(tracks));

        return eventDetails(event);
    }


    //need's a way to find the artist id
    public String artistDetail(HttpServletRequest req){
        String eventId = req.getParameter("eventId");
        return artistDetails(eventId).join();

    }
    //need's a way to send trackNames or events so this can use them to do html
    public String songs(HttpServletRequest req){
        String eventId = req.getParameter("eventId");
        return songsDetails(eventId);
    }


    public String requestSong(CompletableFuture<Track>[] song) {
        String rows = "";
        for (int i = 0 ; i < song.length ; ++i) {
            rows += song[i].thenApply(song1 -> String.format(songsRowView,
                    song1.getName(),
                    song1.getArtistName(),
                    song1.getAlbumName(),
                    song1.getTrackUrl(),
                    song1.getAlbumUrl(),
                    song1.getDuration())).join();
        }
        return String.format(songsView , rows);
    }
    //
//    /**
//     * First tries load from last30daysViewsCache.
//     * If it does not exist look in file system: loadLast30daysWeather(lat, log);
//     * Otherwise request to service: requestLast30daysWeather(lat, log);
//     */
    private String songsDetails(String eventId) {
        String track = songsCache.get(eventId);
        if (track == null) {
            String view = loadSongs(eventId); // Search on file system
            if(view == null) { // Not in file system
                track= requestSong(api.getEvent(eventId).join().getTracks()); // Request to service
                writeSongs(eventId, track);
                     //   .thenApply(v -> writeSongs(eventId, v)); // Save on disk
            }
            else  // Already in File System
                track =view;

            songsCache.putIfAbsent(eventId,track); // Save on cache
        }
        return track;
    }

    private String loadSongs(String eventId) {
        String path = root + eventId + "track"+ ".html";
        File file = new File(path);
        URI uri = !file.exists() ? null : file.toURI();
        return uri == null ? null : load(uri);
    }
    //
    private String writeSongs(String eventId,String view) {
        try {
            String path = root + eventId + "track"+ ".html";
            try (FileWriter fw = new FileWriter(path)) {
                fw.write(view);
                fw.flush();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }


    public CompletableFuture<String> requestArtist(CompletableFuture<Artist> artist) {
        return artist
                .thenApply(artist1 -> String.format(artistDetailView,
                        artist1.getName(),
                        artist1.getBio(),
                        artist1.getUrl(),
                        artist1.getmBid()
                ));
    }
//
//    /**
//     * First tries load from last30daysViewsCache.
//     * If it does not exist look in file system: loadLast30daysWeather(lat, log);
//     * Otherwise request to service: requestLast30daysWeather(lat, log);
//     */
    private CompletableFuture<String> artistDetails(String eventId) {
        CompletableFuture<String> artist = artistCache.get(eventId);
        if (artist == null) {
            String view = loadArtist(eventId); // Search on file system
            if(view == null) { // Not in file system
                artist = requestArtist(api.getEvent(eventId).join().getArtist()) // Request to service
                        .thenApply(v -> writeArtist(eventId, v)); // Save on disk
            }
            else { // Already in File System
                artist = new CompletableFuture<>();
                artist.complete(view);
            }
            artistCache.putIfAbsent(eventId,artist); // Save on cache
        }
        return artist;
    }

    private String loadArtist(String eventId) {
        String path = root + eventId + "artist"+ ".html";
        File file = new File(path);
        URI uri = !file.exists() ? null : file.toURI();
        return uri == null ? null : load(uri);
    }
//
    private String writeArtist(String eventId,String view) {
        try {
            String path = root + eventId + "artist"+ ".html";
            try (FileWriter fw = new FileWriter(path)) {
                fw.write(view);
                fw.flush();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    public String requestEvent(Event event) {
       String basicInfo = String.format(eventsRowView,
                        event.getEventDate(),
                        event.getArtistName(),
                        event.getTour(),
                        event.getSetlistId(),
                        "");

        return String.format(eventDetailsView, basicInfo,linkForArtist(event),linkForSongs(event));
    }

    private String eventDetails(Event event) {
        String evt = eventsCache.get(event.getSetlistId());
        if (evt == null) {
            String view = loadEvent(event); // Search on file system
            if(view == null) { // Not in file system
                evt = requestEvent(event); // Request to service
                writeEvent(event,evt);// Save on disk
            }
            else { // Already in File System
                evt=view;
            }
            eventsCache.putIfAbsent(event.getSetlistId(),evt); // Save on cache
        }
        return evt;
    }

    private String loadEvent(Event event) {
        String path = root + event.getSetlistId() +"event"+ ".html";
        File file = new File(path);
        URI uri = !file.exists() ? null : file.toURI();
        return uri == null ? null : load(uri);
    }
    //
    private String writeEvent(Event event,String view) {
        try {
            String path = root + event.getSetlistId() +"event"+ ".html";
            try (FileWriter fw = new FileWriter(path)) {
                fw.write(view);
                fw.flush();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private static String linkForEvents(Venue v) {
        return String.format("<a href=\"/events?venueId=%s\">%s</a>",
                v.getVenueId(),
                v.getName() + " events");
    }


    private static String linkForEventDetail(Event event) {
        return String.format("<a href=\"/eventDetails?eventId=%s\">%s</a>",
                event.getSetlistId(),
                "Songs and artist info");
    }

    private String linkForSongs(Event event) {
        return String.format("<a href=\"/songs?eventId=%s\">%s</a>",
                event.getSetlistId(),
                "Songs");
    }

    private String linkForArtist(Event event) {
        return String.format("<a href=\"/artistDetail?eventId=%s\">%s</a>",
                event.getSetlistId(),
                "Artist");
    }

    private static String load(String path) {
        return load(getSystemResource(path));
    }

    private static String load(URL url) {
        try {
            return load(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static String load(URI uri) {
        try {
            Path path = Paths.get(uri);
            return lines(path).collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
