DROP TABLE IF EXISTS CommonPartAccess
;
CREATE TABLE CommonPartAccess
(
	cloneSetId VARCHAR(50) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	fieldName VARCHAR(255) NOT NULL,
	KEY (cloneSetId),
	KEY (fieldName, ownerId)
) 
;


ALTER TABLE CommonPartAccess ADD CONSTRAINT FK_CommonAccess_CloneSet 
	FOREIGN KEY (cloneSetId) REFERENCES CloneSet (cloneSetId)
;

ALTER TABLE CommonPartAccess ADD CONSTRAINT FK_CommonPartAccess_Field 
	FOREIGN KEY (fieldName, ownerId) REFERENCES Field (fieldName, ownerId)
;

