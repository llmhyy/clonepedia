CREATE TABLE IF NOT EXISTS DiffPartUseType
(
	cloneInstanceId VARCHAR(50) NOT NULL,
	typeId VARCHAR(50) NOT NULL,
	counterRelationId VARCHAR(50) NOT NULL,
	KEY (cloneInstanceId)
) 
;


#ALTER TABLE DiffPartUseType ADD CONSTRAINT FK_DiffPartUseType_CloneInstance 
#	FOREIGN KEY (cloneInstanceId) REFERENCES CloneInstance (cloneInstanceId)
#;

