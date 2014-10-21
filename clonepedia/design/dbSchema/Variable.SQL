CREATE TABLE IF NOT EXISTS Variable
(
	variableName VARCHAR(255) NOT NULL,
	variableType VARCHAR(255) NOT NULL,
	variableTypeCategory VARCHAR(50) NOT NULL,
	useType VARCHAR(50) NOT NULL,
	ownerId VARCHAR(50) NOT NULL,
	counterRelationId VARCHAR(50)
) 
;


