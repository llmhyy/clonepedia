CREATE TABLE IF NOT EXISTS Class 
(
	classId VARCHAR(50) NOT NULL,
	classFullName VARCHAR(255) NOT NULL,
	projectName VARCHAR(255) NOT NULL,
	superClassId VARCHAR(50),
	outerClassId VARCHAR(50),
	PRIMARY KEY (classId),
	KEY (superClassId),
	KEY (outerClassId),
	KEY (projectName)
) 
;


#ALTER TABLE Class ADD CONSTRAINT FK_Class_Extend_Class 
#	FOREIGN KEY (superClassId) REFERENCES Class (classId)
#;

#ALTER TABLE Class ADD CONSTRAINT FK_Class_Inner_Class 
#	FOREIGN KEY (outerClassId) REFERENCES Class (classId)
#;

#ALTER TABLE Class ADD CONSTRAINT FK_Class_Project 
#	FOREIGN KEY (projectName) REFERENCES Project (projectName)
#;

