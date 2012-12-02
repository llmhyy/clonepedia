DROP TABLE IF EXISTS DiffPartCall
;
CREATE TABLE DiffPartCall
(
	cloneInstanceId VARCHAR(255) NOT NULL,
	methodId VARCHAR(255) NOT NULL,
	counterRelationId VARCHAR(50) NOT NULL,
	KEY (cloneInstanceId),
	KEY (methodId)
) 
;


ALTER TABLE DiffPartCall ADD CONSTRAINT FK_DiffPartCall_CloneInstance 
	FOREIGN KEY (cloneInstanceId) REFERENCES CloneInstance (cloneInstanceId)
;

ALTER TABLE DiffPartCall ADD CONSTRAINT FK_DiffPartCall_Method 
	FOREIGN KEY (methodId) REFERENCES Method (methodId)
;

