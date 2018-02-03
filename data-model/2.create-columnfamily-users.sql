use hashsystem;

DROP table IF exists users;

CREATE table IF NOT exists users(
id UUID primary key,
login text,
password text,
allowedIp SET <text>
);