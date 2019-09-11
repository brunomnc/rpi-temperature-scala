create table "Temperatures" (
  "id" bigint generated by default as identity(start with 1) not null primary key,
  "temperature" int not null,
  "humidity" int not null
);

drop table "Temperatures" if exists;