[![Build Status](https://travis-ci.org/atsid/playflow.svg?branch=master)](https://travis-ci.org/atsid/playflow)
[![Coverage Status](https://coveralls.io/repos/atsid/playflow/badge.svg?branch=master&service=github)](https://coveralls.io/github/atsid/playflow?branch=master)

# Playflow Web Services

Playflow defines RESTful web services for a kanban-style factory workflow using
the Play Framework, version 2.4.2. 


#### Install

1. install java 1.8
2. install Play Framework activator 2.4.2 <http://www.playframework.com/>
3. `cd` to playflow project directory
4. do `activator test` to run tests
5. do `activator run` to run the application
6. go to <http://localhost:9000/api> to view the api


#### Motivation

There are many open source factory workflow projects, but they are complex 
and difficult to  adapt to specific projects.  Playflow is basic enough 
to be easily adaptable to any workflow where users complete work at a given 
set of stations.  Work items are moved from state to state and from station 
to station using RESTful API calls.  An auditing trail is automatically 
recorded for all state transitions.  This project provides a serious head-start 
for projects with these kinds of workflow requirements, and, since it 
is implemented in Play Framework, it is also fun and good for your health.


#### Definitions

* Kanban workflow: a style of workflow where workers can choose what they 
  want to work on from a 'free queue' (see below).

* Project: Has a set of Work Items to be processed.

* Work Item: a piece of work that is completed via the set of stations in 
  a factory.  It may, but doesn't need to be, part of a project.

* Work Item History: an auditing record of a work item's State transitions.
  A transition is added to the history every time a work item is processed, 
  changes assignee, or moves to a different station (service calls
  `assign`, `process`, and `next`--see below-- all invoke state transitions).

* Work Item State: Can be `INITIAL` (not yet in a factory), `UNASSIGNED` (at a
  a factory station but not yet assigned to station worker), `IN_PROCESS` 
  (being worked on by station worker), `PROCESSED` (work completed by a station 
  worker), or `COMPLETED` (all work at at all stations in the factory has been 
  completed).
  
* Project State: Can be `INITIAL` (at least one project work item not yet in 
  a factory), `IN_PROCESS` (no initial work items and at least one work item 
  still being worked on at factory stations), or `COMPLETED` (all work items 
  in the project have been completed). Note that projects do not have a separate
  audit history as project history is just the sum of all the histories of its
  work items. 

* Factory: Has an assembly line for processing a project's work items.

* Assembly Line: Has a set of stations in which work items are 
  processed.

* Station: Has workers that process work items.
 
* Worker: a user that processes work items at a particular station.

* Free Queue: a station's work items that have not yet been claimed by a 
  Worker (i.e. work items without an `assignee`)

* Project `assign` Service Call: service call that assigns a project
  to a factory (puts the project's work items into the first station in the
  factory's assembly line).
  
* Work item `assign` Service Call: service call that assigns a work 
  item to a worker (`assignee`). 

* Work item `process` Service Call: service call that indicates that a work item 
  has been processed by a worker at a station.

* Work item `next` Service Call: service call that transitions a work item
  to the next station.


#### USE CASES
 
Admin needs to see all the factories, with all the stations, with all the work
items at those stations:
* use `GET:/api/factories` service to get all factories
* use `GET:/api/stations?factoryId=<factoryId>` service to get the stations at 
  each of those factories
* use `GET:/api/workItems?stationId=<stationId>` to get the list of work 
  items at each station.

Admin needs to create a new factory:
* use `POST:/api/factories` service with factory payload to create a factory
* use `POST:/api/stations` service with station payload specifying previously
  created factory
   
Admin needs to see which projects are complete:
* use `GET:/api/projects?state=completed`

Admin needs to check which stations a project is at:
* use `GET:/api/stations?projectId=<projectId>`

Admin needs to create a new project with work items:
* use `POST:/api/projects` service with project payload to create a project.
* use `POST:/api/workItems` service with work item payload specifying
  previously created project.

Admin needs to move a project into a factory:
* use `POST:/api/projects/<projectId>/assign?factoryId=<factoryId>`

Admin needs to move a project out of all factories:
* use `POST:/api/projects/<projectId>/assign` service without specifying a factoryId

Worker needs to see work items he has been assigned but has not completed:
* use `GET:/api/workItems?state=in_process&assigneeId=<userId>`

Worker needs to see which stations she is responsible for and process work at 
one of those stations:
* use `GET:/api/stations?workerId=<userId>` to get the list of user stations
* use `GET:/api/workItems?stationId=<stationId>&state=unassigned` to get 
  the list of unassigned items at a particular station.
* use `POST:/api/workItems/<workItemId>/assign?assigneeId=<userId>` to
  assign one of those unassigned work items to the worker.
* use `POST:/api/workItems/<workItemId>/process` service to indicate item
  has been processed.
* use `GET:/api/stations/<stationId>` to get the `nextStationId`.
* use `POST:/api/workItems/<workItemId>/next?nextStationId=<nextStationId>` 
  service to move the item to the next station.

Admin needs to move a worker's items back to the station's free queue because
the worker is sick.
* use `GET:/api/workItems?assigneeId=<userId>` to get all 
  the sick worker's work items.
* use `POST:/api/workItems/<workItemId>/assign` service without specifying 
  an assigneeId to move them back to an unassigned state at the current
  station.


#### FAQ

Which databases are compatible with playflow?
* An in-memory H2 database is built-in for testing.  Your application can use
  any SQL database.  Just configure play with the jdbc driver and url.
  
Can a Factory have QA?
* Yes, just add a station for QA directly after the station that needs to be 
  QAed.  From the QA station, use the `next` service with the previous 
  station's ID specified as a parameter to reject a Work Item to the 
  previous station.

Can a Factory have a gating Station before Work Items enter an Assembly Line 
or before Work Items are released from the factory?
* Yes,  a factory manager, for example, can be assigned as the sole worker in 
  the first Station.  He can then be the one to gate work for the rest of the
  assembly line.  Likewise, a factory manager can be assigned as the sole 
  worker in the last station gating release of the project.

Do stations in an assembly line need to be ordered? 
* No. A station object may or may not specify a next station and even 
  if it does specify a next station, nothing enforces this ordering
  (your application can either use or ignore this information when 
  specifying a `nextStationId` parameter for the `next` service call).

Can a Factory have branching and merging stations?
* Yes. The branch `stationId` must be specified in the parameters of `next` 
  service call. Merging is handled in the same way (by specifying the 
  merge `stationId` in the `next` service call).

Can a Factory have two assembly lines?
* No. A factory has only one assembly line, but you can simulate the presence 
  of multiple assembly lines by branching (see above).

Can work items be transfered to a different factory?
* Yes. Any `stationId` at any factory can be specified in the `next` service 
  call.
  
Can a project be split between two or more factories?
* Yes. Any work item can be assigned to any station at any factory via the 
  `next` service call.  The project `assign` convenience method, however, 
  assigns the entire project to a single factory.
  
Can work items be reassigned to a different worker?
* Yes. Use the `assign` service to reassign Work Items.

Do projects move sequentially or concurrently through the assembly line?
* Concurrently (they move at whatever pace the workers at the various stations 
  finish the project's work items).  This means that a single worker at a 
  single station may simultaneously have work items from several different 
  projects in his or her work queue.  Use projectId to distinguish the 
  different projects returned from the Work Items services. 

Are any of the objects validated?  For example, does a Factory need to be 
created with a first and last station? Or do Work Items need to have a 
particular state before they can be the subject of an `assign`, 
`process` or `next` service call?
* No, objects are not validated at creation time, nor at any other time 
  excepting when a project is assigned to a factory that doesn't have a 
  first station or a service is being called for an object that doesn't
  exist. Your application can add additional validation.
  
Where are completed work items stored?
* A workItem can only be completed after being processed in a
  factory's last station(s) so completed work items will always be
  stored at that last station(s).
  
#### Routes 
(Services expect and return JSON. Specifics at <http://localhost:9000/api>)

Projects:
* `GET     /api/projects`                                                       
* `GET     /api/projects/<projectId>`                                              
* `POST    /api/projects`                                                         
* `PUT     /api/projects/<projectId>`                                              
* `DELETE  /api/projects/<projectId>`                                              
* `POST    /api/projects/<projectId>/assign`                                       

Work Items:
* `GET     /api/workItems`                                                        
* `GET     /api/workItems/<workItemId>`                                            
* `POST    /api/workItems`                                                        
* `PUT     /api/workItems/<workItemId>`                                            
* `DELETE  /api/workItems/<workItemId>`                                           
* `POST    /api/workItems/<workItemId>/assign`                                     
* `POST    /api/workItems/<workItemId>/process`                                    
* `POST    /api/workItems/<workItemId>/next`                                       

WorkItemHistory:
* `GET     /api/workItems/<workItemId>/history`                                    

Factories:
* `GET     /api/factories`                                                        
* `GET     /api/factories/<factoryId>`                                            
* `POST    /api/factories`                                                        
* `PUT     /api/factories/<factoryId>`                                             
* `DELETE  /api/factories/<factoryId>`                                             

Stations:
* `GET     /api/stations`                                                         
* `GET     /api/stations/<stationId>`                                              
* `POST    /api/stations`                                                         
* `PUT     /api/stations/<stationId>`                                              
* `DELETE  /api/stations/<stationId>`                                              

Users
* `GET     /api/users`                                                            
* `GET     /api/users/<userId>`                                                    
* `POST    /api/users`                                                            
* `PUT     /api/users/<userId>`                                                   
* `DELETE  /api/users/<userId>`                                                    
