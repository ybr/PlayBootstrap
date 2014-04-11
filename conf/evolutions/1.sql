# Account schema

# --- !Ups

CREATE TABLE account(
  id        BIGSERIAL PRIMARY KEY,
  login     VARCHAR(255) NOT NULL,
  password  VARCHAR(255) NOT NULL
);

# --- !Downs

DROP TABLE account;
