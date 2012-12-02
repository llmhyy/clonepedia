DROP TABLE IF EXISTS DiffPartAccess
;
CREATE TABLE DiffPartAccess
(
	cloneInstanceId VARCHAR(50) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	fieldName VARCHAR(255) NOT NULL,
	counterRelationId VARCHAR(50) NOT NULL,
	KEY (cloneInstanceId),
	KEY (fieldName, ownerId)
) 
;


ALTER TABLE DiffPartAccess ADD CONSTRAINT FK_DiffPartAccess_CloneInstance 
	FOREIGN KEY (cloneInstanceId) REFERENCES CloneInstance (cloneInstanceId)
;

ALTER TABLE DiffPartAccess ADD CONSTRAINT FK_DiffPartAccess_Field 
	FOREIGN KEY (fieldName, ownerId) REFERENCES Field (fieldName, ownerId)
;

