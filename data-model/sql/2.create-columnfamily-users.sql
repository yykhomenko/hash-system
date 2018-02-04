USE hash_system;

CREATE TABLE users (

  id UUID PRIMARY KEY,

  login text,
  password text,
  allowedIp set<text> );