CREATE TABLE IF NOT EXISTS users (
  id            VARCHAR(100) UNIQUE,
  email         VARCHAR(500) NOT NULL UNIQUE,
  password_hash VARCHAR(300) NOT NULL,
  first_name    VARCHAR(300) NULL,
  last_name     VARCHAR(300) NULL,
  created       BIGINT,
  roles         VARCHAR(50)[],
  PRIMARY KEY (id)
);
