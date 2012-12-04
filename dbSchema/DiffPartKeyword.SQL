CREATE TABLE IF NOT EXISTS DiffPartKeyword
(
	cloneinstanceId VARCHAR(255) NOT NULL,
	keyword VARCHAR(50) NOT NULL,
	counterRelationId VARCHAR(50) NOT NULL,
	KEY (cloneinstanceId)
) 
;


#ALTER TABLE DiffPartKeyword ADD CONSTRAINT FK_DiffPartKeyword_CloneInstance 
#	FOREIGN KEY (cloneinstanceId) REFERENCES CloneInstance (cloneInstanceId)
#;

