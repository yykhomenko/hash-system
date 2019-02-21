CREATE KEYSPACE hash_system
WITH replication = { 'class': 'SimpleStrategy', 'replication_factor' : 2 };