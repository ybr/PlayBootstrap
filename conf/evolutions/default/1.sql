# Account schema

# --- !Ups

CREATE TABLE credentials(
  id            BIGSERIAL PRIMARY KEY,
  login         VARCHAR(255) NOT NULL,
  password      VARCHAR(88) NOT NULL,
  salt          VARCHAR(36) NOT NULL,
  creation      TIMESTAMP NOT NULL DEFAULT NOW(),
  modification  TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT unique_login UNIQUE(login)
);

# --- !Downs

DROP TABLE credentials;
