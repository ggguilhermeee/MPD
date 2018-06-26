package util;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Created by dmigu on 30/03/2017.
 */
public interface IRequest extends AutoCloseable{
    CompletableFuture<Stream<String>> getContent(String path);

    @Override
    public default void close() {
    }
}