drop KEYSPACE IF EXISTS hashsystem;
CREATE KEYSPACE IF NOT EXISTS hashsystem
WITH replication = { 'class': 'SimpleStrategy', 'replication_factor' : 2 };