package event.model;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by dmigu on 30/03/2017.
 */
public class Venue {
    private String venueId;
    private String name;
    private Supplier<Stream<Event>> events;

    public Venue(String name, Supplier<Stream<Event>> events,String venueId) {
        this.venueId=venueId;
        this.name = name;
        this.events = events;
    }

    public String getName() {
        return name;
    }

    public Stream<Event> getEvents() {
        return events.get();
    }

    public String getVenueId() {
        return venueId;
    }

    //TODO:This toString need's to be changed to actualy show the events in the iterable
    @Override
    public String toString() {
        return "Venue{" +
                "name='" + name + '\'' +
                ", events=" + events +
                '}';
    }
}
