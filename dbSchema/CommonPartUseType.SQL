DROP TABLE IF EXISTS CommonPartUseType
;
CREATE TABLE CommonPartUseType
(
	cloneSetId VARCHAR(50) NOT NULL,
	typeId VARCHAR(50) NOT NULL,
	KEY (cloneSetId)
) 
;


ALTER TABLE CommonPartUseType ADD CONSTRAINT FK_CommonPartUseType_CloneSet 
	FOREIGN KEY (cloneSetId) REFERENCES CloneSet (cloneSetId)
;

