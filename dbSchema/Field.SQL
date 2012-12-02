CREATE TABLE  IF NOT EXISTS Field
(
	fieldName VARCHAR(255) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	fieldType VARCHAR(255) NOT NULL,
	fieldTypeCategory VARCHAR(50) NOT NULL,
	PRIMARY KEY (fieldName, ownerId)
) 
;


