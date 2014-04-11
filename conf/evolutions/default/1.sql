# Account schema

# --- !Ups

CREATE TABLE account(
  id            BIGSERIAL PRIMARY KEY,
  login         VARCHAR(255) NOT NULL,
  password      VARCHAR(255) NOT NULL,
  creation      TIMESTAMP NOT NULL DEFAULT NOW(),
  modification  TIMESTAMP NOT NULL DEFAULT NOW(),
  CONSTRAINT unique_login UNIQUE(login)
);

# --- !Downs

DROP TABLE account;
