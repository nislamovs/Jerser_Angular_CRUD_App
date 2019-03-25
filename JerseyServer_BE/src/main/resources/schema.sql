CREATE SEQUENCE HIBERNATE_SEQUENCE START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL generated by default as identity PRIMARY KEY,
  firstname VARCHAR,
  lastname VARCHAR,
  address VARCHAR,
  email VARCHAR,
  phone VARCHAR,
  createdby VARCHAR,
  created_date timestamp,
  modifiedby VARCHAR,
  last_modified_date timestamp
);

CREATE TABLE IF NOT EXISTS user_photos (
  id BIGINT NOT NULL generated by default as identity PRIMARY KEY,
  photo IMAGE NOT NULL
);

alter table user_photos add constraint FKcaf6ht0hfw93lwc13ny0sdmvo foreign key (id) references user_photos(id);


---------AUDIT----------

CREATE SCHEMA AUDIT;

CREATE TABLE AUDIT.REVINFO (
    REV INTEGER GENERATED BY DEFAULT AS IDENTITY,
    REVTSTMP BIGINT,

    USERNAME VARCHAR(255),
    REVISION_DATE timestamp,

    PRIMARY KEY (REV)
);

CREATE TABLE AUDIT.USERS_AUD (
    ID BIGINT NOT NULL,
    REV INTEGER NOT NULL,
    REVTYPE TINYINT,

    firstname VARCHAR,
    lastname VARCHAR,
    address VARCHAR,
    email VARCHAR,
    phone VARCHAR,
    createdby VARCHAR,
    created_date timestamp,
    modifiedby VARCHAR,
    last_modified_date timestamp,

    PRIMARY KEY (ID, REV)
);

ALTER TABLE AUDIT.USERS_AUD ADD CONSTRAINT USERS_AUD_REVINFO FOREIGN KEY (REV) REFERENCES AUDIT.REVINFO;

CREATE TABLE AUDIT.USER_PHOTOS_AUD (
    ID BIGINT NOT NULL,
    REV INTEGER NOT NULL,
    REVTYPE TINYINT,

    photo IMAGE NOT NULL,
    createdby VARCHAR,
    created_date timestamp,
    modifiedby VARCHAR,
    last_modified_date timestamp,

    PRIMARY KEY (ID, REV)
);

ALTER TABLE AUDIT.USER_PHOTOS_AUD ADD CONSTRAINT USER_PHOTOS_AUD_REVINFO FOREIGN KEY (REV) REFERENCES AUDIT.REVINFO;

----------------------------------------------------------------------------------------------------------------