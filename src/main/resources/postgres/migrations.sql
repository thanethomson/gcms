CREATE TABLE IF NOT EXISTS migrations (
  id         VARCHAR(100) UNIQUE,
  type_name  VARCHAR(300),
  type_spec  TEXT NULL,
  drop_type  BOOLEAN DEFAULT FALSE,
  by_user    VARCHAR(100) REFERENCES users(id),
  created    BIGINT,
  applied    BIGINT,
  PRIMARY KEY (id)
);
