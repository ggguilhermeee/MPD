package event.app;

/**
 * Created by user on 15/06/2017.
 */
import event.VibeService;
import event.VibeServiceCache;
import event.controllers.EventController;
import event.data.LastfmApi;
import event.data.SetlistApi;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import util.HttpRequest;
import util.HttpServer;

import static java.lang.ClassLoader.getSystemResource;

public class EventWebApp {
    public static void main(String[] args) throws Exception {
        try(HttpRequest http = new HttpRequest()) {
            VibeService service = new VibeService(http);
            EventController eventCtr = new EventController(service);

            ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
            String resPath = getSystemResource("public").toString();
            holderHome.setInitParameter("resourceBase", resPath);
            holderHome.setInitParameter("dirAllowed", "true");
            holderHome.setInitParameter("pathInfoOnly", "true");

            new HttpServer(5000)
                    .addHandler("/searchVenues", eventCtr::searchVenues) //receives String venuesName in query string
                    .addHandler("/events", eventCtr::events) // String venueId in query string
                    .addHandler("/eventDetails", eventCtr::eventDetails) //string eventid in query string
                    .addHandler("/artistDetail", eventCtr::artistDetail) //String artistId in query string
                    .addHandler("/songs",eventCtr::songs)// String artistName, String trackName in query string
                    .addServletHolder("/public/*", holderHome)
                    .run();
        }
    }
}