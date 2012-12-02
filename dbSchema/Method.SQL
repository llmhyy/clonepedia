CREATE TABLE IF NOT EXISTS Method
(
	methodId VARCHAR(255) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	methodName VARCHAR(255) NOT NULL,
	returnType VARCHAR(255),
	returnTypeCategory VARCHAR(50),
	PRIMARY KEY (methodId)
) 
;


