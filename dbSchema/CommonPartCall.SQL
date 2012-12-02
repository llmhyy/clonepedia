DROP TABLE IF EXISTS CommonPartCall
;
CREATE TABLE CommonPartCall
(
	cloneSetId VARCHAR(50) NOT NULL,
	methodId VARCHAR(255) NOT NULL,
	KEY (cloneSetId),
	KEY (methodId)
) 
;


ALTER TABLE CommonPartCall ADD CONSTRAINT FK_CommonCall_CloneSet 
	FOREIGN KEY (cloneSetId) REFERENCES CloneSet (cloneSetId)
;

ALTER TABLE CommonPartCall ADD CONSTRAINT FK_CommonPartCall_Method 
	FOREIGN KEY (methodId) REFERENCES Method (methodId)
;

