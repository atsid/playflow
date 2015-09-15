package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.DELETE;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.route;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import models.Factory;
import models.Station;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;

public class StationsTest {
    int factoryId = 10;
    int stationId = 10;
    int userId = 10;
    int projectId = 20;
    int nonExistantId = 44444;

    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/stations"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                List<Station> stations = Json.fromJson(node, List.class);
                assertTrue(stations.size() == 4);

                result = route(new RequestBuilder().method(GET).uri("/api/stations?factoryId=" + factoryId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                stations = Json.fromJson(node, List.class);
                assertTrue(stations.size() == 3);

                result = route(new RequestBuilder().method(GET).uri("/api/stations?workerId=" + userId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                stations = Json.fromJson(node, List.class);
                assertTrue(stations.size() == 2);

                result = route(new RequestBuilder().method(GET).uri("/api/stations?projectId=" + projectId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                stations = Json.fromJson(node, List.class);
                assertTrue(stations.size() == 1);

            }
        });
    }

    @Test
    public void testRead() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/stations/" + stationId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                Station station = Json.fromJson(node, Station.class);
                assertEquals(stationId, station.id.intValue());
                // test bad read
                result = route(new RequestBuilder().method(GET).uri("/api/stations/" + nonExistantId));
                assertEquals(play.mvc.Http.Status.NOT_FOUND, result.status());
            }
        });
    }

    @Test
    public void testCreate() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial number of objects
                Result result = route(new RequestBuilder().method(GET).uri("/api/stations?factoryId=" + factoryId));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // add a new object
                Station station = new Station();
                station.name = "new";
                result = route(new RequestBuilder().method(GET).uri("/api/factories/" + factoryId));
                node = Json.parse(contentAsString(result));
                Factory factory = Json.fromJson(node, Factory.class);
                station.factory = factory;

                result = route(new RequestBuilder().method(POST).uri("/api/stations").bodyJson(Json.toJson(station)));
                assertEquals(play.mvc.Http.Status.CREATED, result.status());

                // should be one additional object.
                result = route(new RequestBuilder().method(GET).uri("/api/stations?factoryId=" + factoryId));
                node = Json.parse(contentAsString(result));
                List<Station> stations = Json.fromJson(node, List.class);
                assertEquals(size + 1, stations.size());
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // create initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/stations/" + stationId));
                JsonNode node = Json.parse(contentAsString(result));
                Station station = Json.fromJson(node, Station.class);
                station.name = "updated";

                // now update object
                result = route(new RequestBuilder().method(PUT).uri("/api/stations/" + stationId)
                        .bodyJson(Json.toJson(station)));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // now read the updated object
                result = route(new RequestBuilder().method(GET).uri("/api/stations/" + stationId));
                node = Json.parse(contentAsString(result));
                station = Json.fromJson(node, Station.class);
                assertEquals("updated", station.name);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/stations"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // delete object
                result = route(new RequestBuilder().method(DELETE).uri("/api/stations/" + stationId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // should now be one fewer object
                result = route(new RequestBuilder().method(GET).uri("/api/stations"));
                node = Json.parse(contentAsString(result));
                List<Station> stations = Json.fromJson(node, List.class);
                assertEquals(size - 1, stations.size());
            }
        });
    }
}
