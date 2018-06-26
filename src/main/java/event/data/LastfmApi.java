package event.data;

import com.google.gson.Gson;
import event.data.dto.ArtistDtoRelated.ArtistDto;
import event.data.dto.ArtistDtoRelated.ArtistInitDto;
import event.data.dto.TrackDtoRelated.TrackDto;
import event.data.dto.TrackDtoRelated.TrackInitDto;
import util.HttpRequest;
import util.IRequest;

import java.io.*;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by dmigu on 30/03/2017.
 */
public class LastfmApi {

    private static final String LAST_TOKEN;
    private static final String LAST_HOST = "http://ws.audioscrobbler.com/2.0/";
    private static final String LAST_ARTIST_ARGS = "?method=artist.getinfo&mbid=%s&api_key=%s&format=json";
    private static final String LAST_TRACK_ARGS = "?method=track.getInfo&artist=%s&track=%s&api_key=%s&format=json";

    static {
        try {
            URL keyFile = ClassLoader.getSystemResource("lastfmapi-app-key.txt");
            if(keyFile == null) {
                throw new IllegalStateException(
                        "YOU MUST GET a KEY in lastfmapi.com and place it in src/main/resources/lastfmapi-app-key.txt");
            } else {
                InputStream keyStream = keyFile.openStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyStream))) {
                    LAST_TOKEN = reader.readLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final IRequest req;
    private Gson gson = new Gson();

    public LastfmApi(IRequest req) {
        this.req = req;
    }

    public LastfmApi() {
        this.req = new HttpRequest();
    }

    public CompletableFuture<ArtistDto> getArtistInfo(String artistId){
        String path = LAST_HOST + LAST_ARTIST_ARGS;
        String url = String.format(path , artistId , LAST_TOKEN);
        return req.getContent(url)
                .thenApply(seq -> gson.fromJson(seq.collect(Collectors.joining(" ")), ArtistInitDto.class )
                .getArtist());
    }

    public CompletableFuture<TrackDto> getTrackInfo(String artistName, String trackName){
        String path = LAST_HOST + LAST_TRACK_ARGS;
        String url = String.format(path , artistName , trackName , LAST_TOKEN).replace(" " , "+");
        return req.getContent(url)
                .thenApply(seq -> gson.fromJson(seq.collect(Collectors.joining(" ")), TrackInitDto.class )
                        .getTrack());
    }

}
