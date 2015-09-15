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

import models.Project;
import models.WorkItem;
import models.WorkItemStateType;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;

public class WorkItemsTest {
    int projectId = 10;
    int workItemId = 10;
    int stationId = 40;
    int station3Id = 10;
    int processedWorkItemId = 60;
    int userId = 10;
    int inProcessWorkItemId = 50;
    int station2Id = 20;
    int nonExistantId = 44444;

    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 9);

                result = route(new RequestBuilder().method(GET).uri("/api/workItems?projectId=" + projectId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 2);

                result = route(new RequestBuilder().method(GET).uri("/api/workItems?assigneeId=" + userId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 1);

                result = route(new RequestBuilder().method(GET).uri("/api/workItems?assigneeId=" + userId + "&stationId=" + station2Id));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 1);

                result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + station2Id));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 3);

                result = route(new RequestBuilder().method(GET).uri("/api/workItems?state=COMPLETED"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertTrue(workItems.size() == 3);

            }
        });
    }

    @Test
    public void testRead() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems/" + workItemId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                WorkItem workItem = Json.fromJson(node, WorkItem.class);
                assertEquals(workItemId, workItem.id.intValue());
                // test bad read
                result = route(new RequestBuilder().method(GET).uri("/api/workItems/" + nonExistantId));
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
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems?projectId=" + projectId));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // add a new object
                WorkItem workItem = new WorkItem();
                workItem.name = "new";
                result = route(new RequestBuilder().method(GET).uri("/api/factories/" + projectId));
                node = Json.parse(contentAsString(result));
                Project project = Json.fromJson(node, Project.class);
                workItem.project = project;

                result = route(new RequestBuilder().method(POST).uri("/api/workItems").bodyJson(Json.toJson(workItem)));
                assertEquals(play.mvc.Http.Status.CREATED, result.status());

                // should be one additional object
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?projectId=" + projectId));
                node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertEquals(size + 1, workItems.size());
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // create initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems/" + workItemId));
                JsonNode node = Json.parse(contentAsString(result));
                WorkItem workItem = Json.fromJson(node, WorkItem.class);
                workItem.name = "updated";

                // now update object
                result = route(new RequestBuilder().method(PUT).uri("/api/workItems/" + workItemId)
                        .bodyJson(Json.toJson(workItem)));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // now read the updated object
                result = route(new RequestBuilder().method(GET).uri("/api/workItems/" + workItemId));
                node = Json.parse(contentAsString(result));
                workItem = Json.fromJson(node, WorkItem.class);
                assertEquals("updated", workItem.name);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // delete object
                result = route(new RequestBuilder().method(DELETE).uri("/api/workItems/" + workItemId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // should now be one fewer object
                result = route(new RequestBuilder().method(GET).uri("/api/workItems"));
                node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertEquals(size - 1, workItems.size());
            }
        });
    }

    @Test
    public void testNext() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read workitems at station before workItem assigned
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // move workItem to the next station
                result = route(new RequestBuilder().method(POST)
                        .uri("/api/workItems/" + workItemId + "/next?nextStationId=" + stationId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read workitems at station after workItem assigned to it
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertEquals(size + 1, workItems.size());

                // move workItem out of any station
                result = route(new RequestBuilder().method(POST).uri("/api/workItems/" + workItemId + "/next"));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read workitems at station after workItem assigned to it
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertEquals(size, workItems.size());
            }
        });
    }

    @Test
    public void testAssign() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read user's workItems before workItem assigned
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems?assigneeId=" + userId));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // assign workItem to the user
                result = route(new RequestBuilder().method(POST)
                        .uri("/api/workItems/" + workItemId + "/assign?assigneeId=" + userId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read user's workitems after workItem assigned to user
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?assigneeId=" + userId));
                node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertEquals(size + 1, workItems.size());

                // unassign the work item just assigned
                result = route(new RequestBuilder().method(POST).uri("/api/workItems/" + workItemId + "/assign"));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read user's workitems after workItem unassigned
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?assigneeId=" + userId));
                node = Json.parse(contentAsString(result));
                workItems = Json.fromJson(node, List.class);
                assertEquals(size, workItems.size());
            }
        });
    }

    @Test
    public void testProcess() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read user's workItems before workItem assigned
                Result result = route(
                        new RequestBuilder().method(GET).uri("/api/workItems?state=" + WorkItemStateType.PROCESSED));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // unassign one of the user's work items
                result = route(
                        new RequestBuilder().method(POST).uri("/api/workItems/" + inProcessWorkItemId + "/process"));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read user's workitems after workItem assigned to user
                result = route(
                        new RequestBuilder().method(GET).uri("/api/workItems?state=" + WorkItemStateType.PROCESSED));
                node = Json.parse(contentAsString(result));
                List<WorkItem> workItems = Json.fromJson(node, List.class);
                assertEquals(size + 1, workItems.size());
            }
        });
    }

}
