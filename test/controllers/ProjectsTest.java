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
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;

public class ProjectsTest {
    int projectId = 10;
    int factoryId = 20;
    int stationId = 40;
    int nonExistantId = 44444;
    
    @Test
    public void testList() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/projects"));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                List<Project> projects = Json.fromJson(node, List.class);
                assertTrue(projects.size() == 3);
            }
        });
    }

    @Test
    public void testRead() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result result = route(new RequestBuilder().method(GET).uri("/api/projects/" + projectId));
                assertEquals(play.mvc.Http.Status.OK, result.status());
                JsonNode node = Json.parse(contentAsString(result));
                Project project = Json.fromJson(node, Project.class);
                assertEquals(projectId, project.id.intValue());

                // test bad read
                result = route(new RequestBuilder().method(GET).uri("/api/projects/" + nonExistantId));
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
                Result result = route(new RequestBuilder().method(GET).uri("/api/projects"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // add a new object
                Project project = new Project();
                project.name = "new";
                result = route(new RequestBuilder().method(POST).uri("/api/projects").bodyJson(Json.toJson(project)));
                assertEquals(play.mvc.Http.Status.CREATED, result.status());

                // should be one additional object
                result = route(new RequestBuilder().method(GET).uri("/api/projects"));
                node = Json.parse(contentAsString(result));
                List<Project> projects = Json.fromJson(node, List.class);
                assertEquals(size + 1, projects.size());
            }
        });
    }

    @Test
    public void testUpdate() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                // create initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/projects/" + projectId));
                JsonNode node = Json.parse(contentAsString(result));
                Project project = Json.fromJson(node, Project.class);
                project.name = "updated";

                // now update object
                result = route(new RequestBuilder().method(PUT).uri("/api/projects/" + projectId)
                        .bodyJson(Json.toJson(project)));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // now read the updated object
                result = route(new RequestBuilder().method(GET).uri("/api/projects/" + projectId));
                node = Json.parse(contentAsString(result));
                project = Json.fromJson(node, Project.class);
                assertEquals("updated", project.name);
            }
        });
    }

    @Test
    public void testDelete() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read initial object
                Result result = route(new RequestBuilder().method(GET).uri("/api/projects"));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // delete object
                result = route(new RequestBuilder().method(DELETE).uri("/api/projects/" + projectId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // should now be one fewer object
                result = route(new RequestBuilder().method(GET).uri("/api/projects"));
                node = Json.parse(contentAsString(result));
                List<Project> projects = Json.fromJson(node, List.class);
                assertEquals(size - 1, projects.size());
            }
        });
    }

    @Test
    public void testAssign() {
        running(fakeApplication(), new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                // read workitems at first station before project assigned
                Result result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                JsonNode node = Json.parse(contentAsString(result));
                int size = Json.fromJson(node, List.class).size();

                // assign project to factory
                result = route(new RequestBuilder().method(POST)
                        .uri("/api/projects/" + projectId + "/assign?factoryId=" + factoryId));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read workitems at first station after project assigned
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                node = Json.parse(contentAsString(result));
                List<WorkItem> stationWorkItems = Json.fromJson(node, List.class);
                assertEquals(size + 2, stationWorkItems.size());

                // unassign project
                result = route(new RequestBuilder().method(POST).uri("/api/projects/" + projectId + "/assign"));
                assertEquals(play.mvc.Http.Status.OK, result.status());

                // read workitems at first station after project unassigned
                result = route(new RequestBuilder().method(GET).uri("/api/workItems?stationId=" + stationId));
                node = Json.parse(contentAsString(result));
                stationWorkItems = Json.fromJson(node, List.class);
                assertEquals(size, stationWorkItems.size());
            }
        });
    }

}
