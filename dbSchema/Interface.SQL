DROP TABLE IF EXISTS Interface
;
CREATE TABLE Interface
(
	interfaceId VARCHAR(50) NOT NULL,
	interfaceFullName VARCHAR(255) NOT NULL,
	projectName VARCHAR(50),
	PRIMARY KEY (interfaceId)
) 
;


