
import event.VibeService;


import event.VibeServiceCache;
import event.data.LastfmApi;
import event.data.SetlistApi;
import event.data.dto.EventsDtosRelated.EventDto;
import event.data.dto.VenuesDtosRelated.VenueDto;
import event.model.Track;
import event.model.Venue;
import util.Countify;
import util.HttpRequest;
import util.ICounter;
import util.IRequest;
import event.model.Event;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.out;

/**
 * Created by dmigu on 31/03/2017.
 */
public class Test {
    public static void main(String[] args) {
        ICounter<String, CompletableFuture<Stream<String>>> req = Countify.of(new HttpRequest()::getContent);
        SetlistApi set = new SetlistApi(req::apply);

//        IntStream.range(1, 7)
//                .mapToObj(p -> set.getVenues("lisbon", p))
//                .map(CompletableFuture::join)
//                .forEach(st -> Arrays.stream(st).forEach(dto -> System.out.println(dto)));

//        IntStream.range(1, 7)
//                .mapToObj(p -> {
//                    CompletableFuture<VenueDto[]> venusDtos = set.getVenues("lisbon", p);
//                    return  venusDtos.thenApply(
//                            venueDtoArray -> Arrays
//                                    .stream(venueDtoArray)
//                                    .map(venueDto -> new Venue(venueDto.getName() , ()-> null)));
//
//                })
//                .map(CompletableFuture::join)
//                .forEach(st -> st.forEach(out::println));


        LastfmApi last = new LastfmApi(req::apply);
        VibeService service = new VibeServiceCache(set, last);

       // CompletableFuture<EventDto[]> events = set.getEvents("33d4a8c9");

//        service.searchVenues("lisbon").get().forEach(out::println);
        Stream<Venue> venues = service.searchVenues("lisbon").get();

        Stream<Event> s = venues.filter(venue -> venue.getName().equals("TVI24")).findAny().get().getEvents();
        s.forEach(event -> {
            System.out.println(event.getArtist().join());
            CompletableFuture<Track>[] array = event.getTracks();
            for(CompletableFuture<Track> t : array){
                System.out.println(t.join());
            }
        });



        //Iterator iter = service.getEvents("33d4a8c9").iterator();


       /* Iterable<Event> iter = service.getEvents("33d4a8c9");
        Iterator<Event> iterator = iter.iterator();
        Evente1 = iterator.next();
        System.out.println(e1.getArtist().getName());
        System.out.println(e1.getTracks().iterator().next() );
        System.out.println("------------------------------------------------------------------------------");
        Event e2 = iterator.next();
        e2 = iterator.next();111111
        System.out.println(e2.getArtist().getName());
        Iterator<Track> tracks = e2.getTracks().iterator();
        System.out.println(tracks.next());
        System.out.println(tracks.next());

        iter = service.getEvents("33d4a8c9");
        iterator = iter.iterator();
        e1 = iterator.next();
        System.out.println("--------------------------------------------------");
        e1.getTracks().forEach(out::println);
        System.out.println("--------------------------------------------------");
        System.out.println(e1.getArtist().getName());
        System.out.println(e1.getTracks().iterator().next() );

        e2 = iterator.next();
        e2 = iterator.next();
        System.out.println(e2.getArtist().getName());
        tracks = e2.getTracks().iterator();
        System.out.println(tracks.next());
        System.out.println(tracks.next());*/
//
         //service.searchVenues("lisbon")
         //       .get().forEach(out::println);

//        EventDto[] dtos = set.getEvents("33d4a8c9",1).join();
//        EventDto[] dtos2= set.getEvents("33d4a8c9",2).join();
//        for (EventDto dto: dtos) {
//            System.out.println(dto);
//        }
//
//        for (EventDto dto : dtos2){
//            System.out.println(dto);
//        }


//        Stream<Event> st = service.getEvents("33d4a8c9").get();
//        st.forEach(out::println);
        //System.out.println("finished");
        /*System.out.println(req.getCount());
        events.iterator().next();
        System.out.println(req.getCount());*/
    }
}
