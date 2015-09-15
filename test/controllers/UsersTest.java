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

import models.User;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;

public class UsersTest {
    int userId = 10;
    int nonExistantId = 44444;
    
    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/users"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                List<User> users = Json.fromJson(node, List.class);
                assertTrue(users.size() == 9);
            }
        });
    }

    @Test
    public void testRead() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/users/" + userId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                User user = Json.fromJson(node, User.class);
                assertEquals(userId, user.id.intValue());

                // test bad read
                result = route(new RequestBuilder().method(GET).uri("/api/users/" + nonExistantId));
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
                Result result = route(new RequestBuilder().method(GET).uri("/api/users"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // add a new object
                User user = new User();
                user.email = "new";
                result = route(new RequestBuilder().method(POST).uri("/api/users").bodyJson(Json.toJson(user)));
                assertEquals(play.mvc.Http.Status.CREATED, result.status());

                // should be one additional object
                result = route(new RequestBuilder().method(GET).uri("/api/users"));
                node = Json.parse(contentAsString(result));
                List<User> users = Json.fromJson(node, List.class);
                assertEquals(size + 1, users.size());
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // create initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/users/" + userId));
                JsonNode node = Json.parse(contentAsString(result));
                User user = Json.fromJson(node, User.class);
                user.email = "updated";

                // now update object
                result = route(
                        new RequestBuilder().method(PUT).uri("/api/users/" + userId).bodyJson(Json.toJson(user)));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // now read the updated object
                result = route(new RequestBuilder().method(GET).uri("/api/users/" + userId));
                node = Json.parse(contentAsString(result));
                user = Json.fromJson(node, User.class);
                assertEquals("updated", user.email);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/users"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // delete object
                result = route(new RequestBuilder().method(DELETE).uri("/api/users/" + userId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // should now be one fewer object
                result = route(new RequestBuilder().method(GET).uri("/api/users"));
                node = Json.parse(contentAsString(result));
                List<User> users = Json.fromJson(node, List.class);
                assertEquals(size - 1, users.size());
            }
        });
    }
}
