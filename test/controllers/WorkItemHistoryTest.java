package controllers;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.route;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import models.WorkItemStateTransition;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;

public class WorkItemHistoryTest {
    int workItemId = 10;
    int stationId = 30;

    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read user's workItems before workItem assigned
                Result result = route(
                        new RequestBuilder().method(GET).uri("/api/workItems/" + workItemId + "/history"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();
                assertEquals(1, size);

                // move workItem into a station
                result = route(new RequestBuilder().method(POST)
                        .uri("/api/workItems/" + workItemId + "/next?nextStationId=" + stationId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read user's workitems after workItem assigned to user
                result = route(new RequestBuilder().method(GET).uri("/api/workItems/" + workItemId + "/history"));
                node = Json.parse(contentAsString(result));
                List<WorkItemStateTransition> history = Json.fromJson(node, List.class);
                assertEquals(size + 1, history.size());
            }
        });
    }

}
