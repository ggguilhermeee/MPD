package util;


import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

/**
 * @author Miguel Gamboa
 *         created on 08-03-2017
 */
public class HttpRequest implements IRequest{

    private final AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();

    @Override
    public CompletableFuture<Stream<String>> getContent(String path) {
        System.out.println(path);
        return asyncHttpClient
                .prepareGet(path)
                .execute()
                .toCompletableFuture()
                .thenApply(resp ->{
                    if(resp.getStatusCode()!=200){
                        throw new RuntimeException();
                    }
                    return resp.getResponseBodyAsStream();
                })
                .thenApply(in -> stream(new SpliteratorFromReader(in), false));

    }

    @Override
    public void close() {
        if(!asyncHttpClient.isClosed())
            try {
                asyncHttpClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }
}
