package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Miguel Gamboa
 *         created on 08-03-2017
 */
public class FileRequest implements IRequest {

    @Override
    public CompletableFuture<Stream<String>> getContent(String path) {
        return CompletableFuture.supplyAsync(() -> {
            return getStream(path);
        });
    }

    /**
     * Sincrono
     */
    public static Stream<String> getStream(String path) {
        String[] parts = path.split("/");
        path = parts[parts.length-1]
                .replace('?', '-')
                .replace('&', '-')
                .replace('=', '-')
                .replace(',', '-');
        try {
            InputStream in = ClassLoader.getSystemResource(path).openStream();
            return StreamSupport.stream(new SpliteratorFromReader(in), false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public void close(){

    }
}
