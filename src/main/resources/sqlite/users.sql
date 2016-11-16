CREATE TABLE IF NOT EXISTS users (
  id            VARCHAR(100) UNIQUE PRIMARY KEY,
  email         VARCHAR(500) NOT NULL UNIQUE,
  password_hash VARCHAR(300) NOT NULL,
  first_name    VARCHAR(300),
  last_name     VARCHAR(300),
  created       BIGINT,
  roles         VARCHAR(500)
);
