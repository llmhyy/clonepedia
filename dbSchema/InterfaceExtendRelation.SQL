CREATE TABLE IF NOT EXISTS InterfaceExtendRelation
(
	subInterfaceId VARCHAR(50) NOT NULL,
	superInterfaceId VARCHAR(50) NOT NULL,
	PRIMARY KEY (subInterfaceId, superInterfaceId),
	KEY (subInterfaceId),
	KEY (superInterfaceId)
) 
;


#ALTER TABLE InterfaceExtendRelation ADD CONSTRAINT FK_InterfaceExtendRelation_Interface1 
#	FOREIGN KEY (subInterfaceId) REFERENCES Interface (interfaceId)
#;

#ALTER TABLE InterfaceExtendRelation ADD CONSTRAINT FK_InterfaceExtendRelation_Interface2 
#	FOREIGN KEY (superInterfaceId) REFERENCES Interface (interfaceId)
#;

