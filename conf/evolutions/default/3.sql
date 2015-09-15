# --- !Ups

-- test data sequences 

drop sequence factory_seq;
create sequence factory_seq start with 1000;

drop sequence station_seq;
create sequence station_seq start with 1000;

drop sequence project_seq;
create sequence project_seq start with 1000;

drop sequence work_item_seq;
create sequence work_item_seq start with 1000;

drop sequence user_seq;
create sequence user_seq start with 1000;


# --- !Downs



