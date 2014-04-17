# User schema

# --- !Ups

CREATE TABLE T_USER(
  id              BIGSERIAL PRIMARY KEY,
  first_name      VARCHAR(255) NOT NULL,
  last_name       VARCHAR(255) NOT NULL,
  creation        TIMESTAMP NOT NULL DEFAULT NOW(),
  credentials_id  BIGINT REFERENCES T_CREDENTIALS(id)
);

# --- !Downs

DROP TABLE T_USER;
