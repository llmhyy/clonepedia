CREATE TABLE  IF NOT EXISTS MethodParameter
(
	parameterType VARCHAR(255) NOT NULL,
	parameterName VARCHAR(255),
	parameterOrder INTEGER NOT NULL,
	methodId VARCHAR(255) NOT NULL,
	parameterTypeCategory VARCHAR(50) NOT NULL,
	PRIMARY KEY (parameterType, parameterOrder, methodId),
	KEY (methodId)
) 
;


ALTER TABLE MethodParameter ADD CONSTRAINT FK_MethodParameter_Method 
	FOREIGN KEY (methodId) REFERENCES Method (methodId)
;

