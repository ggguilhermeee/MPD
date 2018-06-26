package event.model;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by dmigu on 24/05/2017.
 */
public class GenericPagerSpliterator<R> extends Spliterators.AbstractSpliterator<R> {
    private final Function<Integer, CompletableFuture<Stream<R>>> sequence;
    private CompletableFuture<Spliterator<R>> pagePromise;
    private int curPage;

    public GenericPagerSpliterator(Function<Integer, CompletableFuture<Stream<R>>> sequence, Integer curPage) {
        super(Long.MAX_VALUE, ORDERED);
        this.sequence = sequence;
        this.curPage = curPage;
        this.pagePromise = sequence
                .apply(this.curPage++)
                .thenApply(st -> st.spliterator());
    }

    @Override
    public boolean tryAdvance(Consumer<? super R> action) {
        if (pagePromise == null || pagePromise.join() == null) {
            return false;
        }

        if (pagePromise.join().tryAdvance(action))
            return true;
        try {
            this.pagePromise = null;
            this.pagePromise = sequence
                    .apply(curPage++)
                    .thenApply(st -> st.spliterator())
                    .exceptionally(e -> null);
        } catch (RuntimeException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
        if (pagePromise.join() == null) {
            return false;
        }
        return pagePromise.join().tryAdvance(action);
    }
}
