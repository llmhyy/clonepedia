DROP TABLE IF EXISTS Project
;
CREATE TABLE Project
(
	projectName VARCHAR(50) NOT NULL,
	programmingLanguage VARCHAR(50),
	projectFilePath VARCHAR(255) NOT NULL,
	PRIMARY KEY (projectName)
) 
;


