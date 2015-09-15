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
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;

public class FactoriesTest {
    int factoryId = 10;
    int nonExistantId = 44444;
    
    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/factories"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                List<Factory> factories = Json.fromJson(node, List.class);
                assertTrue(factories.size() == 2);
            }
        });
    }

    @Test
    public void testRead() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/factories/" + factoryId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                Factory factory = Json.fromJson(node, Factory.class);
                assertEquals(factoryId, factory.id.intValue());

                // test bad read
                result = route(new RequestBuilder().method(GET).uri("/api/factories/" + nonExistantId));
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
                Result result = route(new RequestBuilder().method(GET).uri("/api/factories"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // add a new object
                Factory factory = new Factory();
                factory.name = "new";
                result = route(new RequestBuilder().method(POST).uri("/api/factories").bodyJson(Json.toJson(factory)));
                assertEquals(play.mvc.Http.Status.CREATED, result.status());

                // should be one additional object
                result = route(new RequestBuilder().method(GET).uri("/api/factories"));
                node = Json.parse(contentAsString(result));
                List<Factory> factories = Json.fromJson(node, List.class);
                assertEquals(size + 1, factories.size());
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // create initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/factories/" + factoryId));
                JsonNode node = Json.parse(contentAsString(result));
                Factory factory = Json.fromJson(node, Factory.class);
                factory.name = "updated";

                // now update object
                result = route(new RequestBuilder().method(PUT).uri("/api/factories/" + factoryId)
                        .bodyJson(Json.toJson(factory)));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // now read the updated object
                result = route(new RequestBuilder().method(GET).uri("/api/factories/" + factoryId));
                node = Json.parse(contentAsString(result));
                factory = Json.fromJson(node, Factory.class);
                assertEquals("updated", factory.name);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/factories"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // delete object
                result = route(new RequestBuilder().method(DELETE).uri("/api/factories/" + factoryId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // should now be one fewer object 
                result = route(new RequestBuilder().method(GET).uri("/api/factories"));
                node = Json.parse(contentAsString(result));
                List<Factory> factories = Json.fromJson(node, List.class);
                assertEquals(size - 1, factories.size());
            }
        });
    }
}
