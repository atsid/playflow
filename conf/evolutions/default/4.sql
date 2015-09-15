# --- !Ups

 
 
INSERT INTO factory (id, name) VALUES 
  (10, 'normal factory'),
  (20, 'one station factory');

  
 
INSERT INTO station (id,   name, is_first, is_last, next_id, factory_id) VALUES
  (10,  'station 3: normal factory',  false, true, NULL, 10),
  (20,  'station 2: normal factory',  false, false, 10, 10),
  (30,  'station 1: normal factory',  true, false, 20, 10),
  (40,  'station 1: one station factory', true, true, null, 20);
  

INSERT INTO user (id, email, first_name, last_name) VALUES
  (10, 'pat.le.blanch@hotmail.com','Pat Le','Blanch'),
  (20, 'john.smith@hotmail.com','John','Smith'),
  (30, 'mike.roberts@gmail.com','Mike','Roberts'),
  (40, 'jill.miller@yahoo.com','Jill','Miller'),
  (50, 'bill.johnson@hotmail.com','Bill','Johnson'),
  (60, 'phil.brown@gmail.com', 'Phil','Brown'),
  (70, 'david.jackson@yahoo.com','David','Jackson'),
  (80, 'janice.wilson@hotmail.com','Janice','Wilson'),
  (90, 'john.rodgers@hotmail.com','John','Rodgers');
  
  
INSERT INTO station_user (station_id, user_id) VALUES 
  (10, 10),
  (10, 20),
  (20, 10),
  (30, 30); 


INSERT INTO project (id, name) VALUES
  (10, 'initial project'),
  (20, 'partially initial project'),
  (30, 'completed project');
  
INSERT INTO work_item (id, name, state, project_id, station_id, assignee_id) VALUES
  (10, 'work item 1: initial project', 'INITIAL', 10, null, null),
  (20, 'work item 2: initial project', 'INITIAL', 10, null, null),
  (30, 'work item initial: partially initial project', 'INITIAL', 20, null, null),
  (40, 'work item unassigned: partially initial project', 'UNASSIGNED', 20, 20, null),
  (50, 'work item assigned: partially initial project', 'IN_PROCESS', 20, 20, 10),
  (60, 'work item processed: partially initial project', 'PROCESSED', 20, 20, 20),
  (70, 'work item completed: partially initial project', 'COMPLETED', 20, null, null),
  (80, 'work item 1: completed project', 'COMPLETED', 30, 10, null),
  (90, 'work item 2: completed project', 'COMPLETED', 30, 10, null);
 
INSERT INTO work_item_state_transition (id, work_item_id, from_station_id, to_station_id, from_state, to_state, from_assignee_id,to_assignee_id, transitioner_id, transition_date, comment) VALUES
  (10, 10, 40, null, 'UNASSIGNED', 'INITIAL', 10, null, null, '2013-03-15 00:00:00', 'transitioned out of factory');
  
# --- !Downs
