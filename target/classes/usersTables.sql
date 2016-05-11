create table users (
	IDUser int auto_increment, 
		CONSTRAINT idPrimary primary key (IDUser),
	Username varchar(32) not null,
		CONSTRAINT usernameUnique UNIQUE (Username), 
	Password varchar(60) not null,
    Blocked boolean default false not null,
    TimeOfBlock datetime default null,
    LastPasswordChange timestamp not null
) DEFAULT CHARSET = utf8;

SET NAMES UTF8;
SET CHARACTER SET UTF8;

create table blockedUsers (
	IDUser int auto_increment, 
		 CONSTRAINT idPrimary primary key (IDUser),
	SerialNumber varchar(32),
    TimeOfBlock timestamp not null
);