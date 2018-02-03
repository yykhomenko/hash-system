use hashsystem;

drop table if exists hashesmd5;

CREATE TABLE hashesmd5 (

	cc smallint,
	ndc smallint,
	number int,

	mostSigBits bigint,
	leastSigBits bigint,

	PRIMARY KEY ((cc, ndc), number)
);