package event;

import event.model.Artist;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by fabio on 12/04/2017.
 */
public class ArtistSupplier implements Supplier{
    private final String artistId;
    private final Function<String , CompletableFuture<Artist>> request;
    private CompletableFuture<Artist> artistPromise;

    @Override
    public Artist get() {
        return artistPromise.join();
    }

    public ArtistSupplier(String artistId, Function<String , CompletableFuture<Artist>> request) {
        this.artistId = artistId;
        this.request = request;
        artistPromise = request.apply(artistId);
    }
}
