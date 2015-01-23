CREATE TABLE contact (
	id IDENTITY,
	first VARCHAR(254) NOT NULL,
	last VARCHAR(254),
	cell VARCHAR(30),
	email VARCHAR(254),
	birthday date
);