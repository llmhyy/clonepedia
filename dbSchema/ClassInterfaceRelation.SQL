DROP TABLE IF EXISTS ImplementRelation
;
CREATE TABLE ImplementRelation
(
	classId VARCHAR(50) NOT NULL,
	interfaceId VARCHAR(50) NOT NULL,
	PRIMARY KEY (classId, interfaceId),
	KEY (classId),
	KEY (interfaceId)
) 
;


ALTER TABLE ImplementRelation ADD CONSTRAINT FK_ImplementRelation_Class 
	FOREIGN KEY (classId) REFERENCES Class (classId)
;

ALTER TABLE ImplementRelation ADD CONSTRAINT FK_ImplementRelation_Interface 
	FOREIGN KEY (interfaceId) REFERENCES Interface (interfaceId)
;

