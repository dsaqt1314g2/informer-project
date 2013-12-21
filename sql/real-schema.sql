drop database if exists realm-db;
create database realm-db;
use realm-db;

CREATE TABLE usuarios (
	username varchar(20) NOT NULL PRIMARY KEY,
	password char(32) NOT NULL,
	correo varchar(255) NOT NULL,
	name varchar(70) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE roles (
	username varchar(20) NOT NULL,
	rolename varchar(20) NOT NULL,
	FOREIGN KEY(username) REFERENCES usuarios(username) ON DELETE CASCADE,
	PRIMARY KEY (username, rolename)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

