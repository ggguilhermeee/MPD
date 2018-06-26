package event.model;



import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Created by fabio on 16/05/2017.
 */
public class GenericSpliterator<S,T,R> extends AbstractSpliterator<R>{
    private final BiFunction<S, Integer, T[]> request;
    private final Function<T,R> transform;
    private final S param;
    private int counter;
    private Spliterator<R> page;

    public int getCounter() {
        return counter;
    }

    public GenericSpliterator(BiFunction<S, Integer, T[]> request, Function<T, R> transform, S param) {

        super(Long.MAX_VALUE, ORDERED);
        this.request = request;
        this.transform = transform;
        this.param = param;
        this.counter = 1;
    }

    @Override
    public boolean tryAdvance(Consumer<? super R> action) {
        if(page==null)
            this.page = Arrays.stream(request.apply(param, counter++))
                    .map(transform).spliterator();
        if(page.tryAdvance(action))
            return true;
        try {
            this.page = Arrays.stream(request.apply(param, counter++))
                    .map(transform).spliterator();
        }catch (Exception e){
            return false;
        }
        return page.tryAdvance(action);
    }
}
