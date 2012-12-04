CREATE TABLE IF NOT EXISTS CloneInstance
(
	cloneInstanceId VARCHAR(50) NOT NULL,
	cloneSetId VARCHAR(50) NOT NULL,
	residingMethodId VARCHAR(255) NOT NULL,
	fileLocation TEXT NOT NULL,
	startLine VARCHAR(10) NOT NULL,
	endLine VARCHAR(10) NOT NULL,
	PRIMARY KEY (cloneInstanceId),
	KEY (cloneSetId),
	KEY (residingMethodId)
) 
;


#ALTER TABLE CloneInstance ADD CONSTRAINT FK_CloneInstance_CloneSet 
#	FOREIGN KEY (cloneSetId) REFERENCES CloneSet (cloneSetId)
#;

#ALTER TABLE CloneInstance ADD CONSTRAINT FK_CloneInstance_Method 
#	FOREIGN KEY (residingMethodId) REFERENCES Method (methodId)
#;

