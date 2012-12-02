DROP TABLE IF EXISTS Field
;
CREATE TABLE Field
(
	fieldName VARCHAR(255) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	fieldType VARCHAR(255) NOT NULL,
	fieldTypeCategory VARCHAR(50) NOT NULL,
	PRIMARY KEY (fieldName, ownerId)
) 
;


