# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table factory (
  id                        bigint not null,
  is_active                 boolean default true not null,
  name                      varchar(255) not null,
  description               varchar(255),
  constraint uq_factory_name unique (name),
  constraint pk_factory primary key (id))
;

create table project (
  id                        bigint not null,
  is_active                 boolean default true not null,
  name                      varchar(255) not null,
  description               varchar(255),
  constraint uq_project_name unique (name),
  constraint pk_project primary key (id))
;

create table station (
  id                        bigint not null,
  is_active                 boolean default true not null,
  name                      varchar(255) not null,
  description               varchar(255),
  next_id                   bigint,
  is_last                   boolean default false,
  is_first                  boolean default false,
  factory_id                bigint,
  constraint pk_station primary key (id))
;

create table user (
  id                        bigint not null,
  is_active                 boolean default true not null,
  email                     varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table work_item (
  id                        bigint not null,
  is_active                 boolean default true not null,
  name                      varchar(255) not null,
  description               varchar(255),
  state                     varchar(10) not null,
  project_id                bigint,
  station_id                bigint,
  assignee_id               bigint,
  constraint ck_work_item_state check (state in ('UNASSIGNED','COMPLETED','IN_PROCESS','INITIAL','PROCESSED')),
  constraint pk_work_item primary key (id))
;

create table work_item_state_transition (
  id                        bigint not null,
  is_active                 boolean default true not null,
  work_item_id              bigint,
  from_station_id           bigint,
  to_station_id             bigint,
  from_state                varchar(10) not null,
  to_state                  varchar(10) not null,
  from_assignee_id          bigint,
  to_assignee_id            bigint,
  transitioner_id           bigint,
  transition_date           timestamp not null,
  comment                   varchar(255),
  constraint ck_work_item_state_transition_from_state check (from_state in ('UNASSIGNED','COMPLETED','IN_PROCESS','INITIAL','PROCESSED')),
  constraint ck_work_item_state_transition_to_state check (to_state in ('UNASSIGNED','COMPLETED','IN_PROCESS','INITIAL','PROCESSED')),
  constraint pk_work_item_state_transition primary key (id))
;


create table station_user (
  station_id                     bigint not null,
  user_id                        bigint not null,
  constraint pk_station_user primary key (station_id, user_id))
;
create sequence factory_seq;

create sequence project_seq;

create sequence station_seq;

create sequence user_seq;

create sequence work_item_seq;

create sequence work_item_state_transition_seq;

alter table station add constraint fk_station_next_1 foreign key (next_id) references station (id) on delete restrict on update restrict;
create index ix_station_next_1 on station (next_id);
alter table station add constraint fk_station_factory_2 foreign key (factory_id) references factory (id) on delete restrict on update restrict;
create index ix_station_factory_2 on station (factory_id);
alter table work_item add constraint fk_work_item_project_3 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_work_item_project_3 on work_item (project_id);
alter table work_item add constraint fk_work_item_station_4 foreign key (station_id) references station (id) on delete restrict on update restrict;
create index ix_work_item_station_4 on work_item (station_id);
alter table work_item add constraint fk_work_item_assignee_5 foreign key (assignee_id) references user (id) on delete restrict on update restrict;
create index ix_work_item_assignee_5 on work_item (assignee_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition__6 foreign key (work_item_id) references work_item (id) on delete restrict on update restrict;
create index ix_work_item_state_transition__6 on work_item_state_transition (work_item_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition__7 foreign key (from_station_id) references station (id) on delete restrict on update restrict;
create index ix_work_item_state_transition__7 on work_item_state_transition (from_station_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition__8 foreign key (to_station_id) references station (id) on delete restrict on update restrict;
create index ix_work_item_state_transition__8 on work_item_state_transition (to_station_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition__9 foreign key (from_assignee_id) references user (id) on delete restrict on update restrict;
create index ix_work_item_state_transition__9 on work_item_state_transition (from_assignee_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition_10 foreign key (to_assignee_id) references user (id) on delete restrict on update restrict;
create index ix_work_item_state_transition_10 on work_item_state_transition (to_assignee_id);
alter table work_item_state_transition add constraint fk_work_item_state_transition_11 foreign key (transitioner_id) references user (id) on delete restrict on update restrict;
create index ix_work_item_state_transition_11 on work_item_state_transition (transitioner_id);



alter table station_user add constraint fk_station_user_station_01 foreign key (station_id) references station (id) on delete restrict on update restrict;

alter table station_user add constraint fk_station_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists factory;

drop table if exists project;

drop table if exists station;

drop table if exists station_user;

drop table if exists user;

drop table if exists work_item;

drop table if exists work_item_state_transition;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists factory_seq;

drop sequence if exists project_seq;

drop sequence if exists station_seq;

drop sequence if exists user_seq;

drop sequence if exists work_item_seq;

drop sequence if exists work_item_state_transition_seq;

