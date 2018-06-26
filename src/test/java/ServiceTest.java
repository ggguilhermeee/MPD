import event.VibeService;
import event.VibeServiceCache;
import event.data.LastfmApi;
import event.data.SetlistApi;
import event.model.Artist;
import event.model.Event;
import event.model.Track;
import event.model.Venue;
import org.junit.Test;
import util.Countify;
import util.FileRequest;
import util.HttpRequest;
import util.ICounter;
import util.queries.LazyQueries;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static util.queries.LazyQueries.skip;

/**
 * Created by Diogo Costa on 19/04/2017.
 */
public class ServiceTest {
    @Test
    public void testVibeServiceCache(){
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        VibeService service = new VibeServiceCache(req::apply);
        Spliterator<Event> events = service.getEvents("33d4a8c9").get().spliterator();
        assertEquals(0,req.getCount());
        events.tryAdvance(out::println);
        assertEquals(1,req.getCount());

        events.forEachRemaining(out::println);
        assertEquals(4,req.getCount());
        events  = service.getEvents("33d4a8c9").get().spliterator();

        System.out.println("-------------------------------------------------");
        for (int i = 0 ; i<5;++i) {
            events.tryAdvance(out::println);
        }
        assertEquals(4,req.getCount());
    }

    @Test
    public void testVibeService(){
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        VibeService service = new VibeService(req::apply);

        Spliterator<Event> events = service.getEvents("33d4a8c9").get().spliterator();
        assertEquals(0,req.getCount());
        events.tryAdvance(out::println);
        assertEquals(1,req.getCount());

        events.forEachRemaining(out::print);
        assertEquals(3,req.getCount());
        events  = service.getEvents("33d4a8c9").get().spliterator();
        events.forEachRemaining(out::print);
        assertEquals(6,req.getCount());
    }

    @Test
    public void testingRequest(){
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        VibeService service = new VibeService(req::apply);

        Spliterator<Event> events = service.getEvents("33d4a8c9").get().spliterator();
        assertEquals(0,req.getCount());

        Event[] event = new Event[1];
        events.tryAdvance((ev) -> event[0] = ev);
        assertEquals( 1,req.getCount());

        events.tryAdvance(out::println);
        assertEquals(1,req.getCount());

        Spliterator<Track> track = event[0].getTracks().spliterator();
        track.tryAdvance(out::println);
        assertEquals(2,req.getCount());
    }


    // Verificar se a informação estraida do pedido é igual a informação e que se pretende
    @Test
    public void extractCorrectData(){
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        VibeService service = new VibeServiceCache(req::apply);

        Spliterator<Event> events = service.getEvents("33d4a8c9").get().spliterator();

        Event[] testEvent = new Event[1];
        events.tryAdvance((e) -> testEvent[0] = e);

        assertEquals("be6319e", testEvent[0].getSetlistId());

        assertEquals("Roberto Carlos", testEvent[0].getArtistName());

        Track track = testEvent[0].getTracks().iterator().next();
        assertEquals("INTRO", track.getName());
    }

    @Test
    public void paginationTesting(){
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        VibeService service = new VibeServiceCache(req::apply);

        Spliterator<Event> events = service.getEvents("33d4a8c9").get().spliterator();

        for(int i = 1; i<=20; i++){
            events.tryAdvance(out::println);
            assertEquals(1, req.getCount());
        }

        events.tryAdvance(out::println);
        assertEquals(2, req.getCount());
    }

}