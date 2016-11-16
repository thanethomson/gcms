CREATE TABLE IF NOT EXISTS migrations (
  id         VARCHAR(100) UNIQUE,
  type_name  VARCHAR(300),
  type_spec  TEXT,
  drop_type  BOOLEAN DEFAULT FALSE,
  by_user    VARCHAR(100),
  created    BIGINT,
  applied    BIGINT,
  FOREIGN KEY(by_user) REFERENCES users(id)
);
