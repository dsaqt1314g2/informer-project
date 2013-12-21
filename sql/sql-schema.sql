drop database if exists informerdb;
create database informerdb;
use informerdb;

CREATE TABLE perfiles (
	identificador int NOT NULL auto_increment PRIMARY KEY,
	username varchar(20) NOT NULL,
	name varchar(70) NOT NULL,
	correo varchar(255) NOT NULL,
	genero boolean NOT NULL,
	fecha_nacimiento Date NOT NULL,
	uni_escuela int NOT NULL,
	foto varchar(255),
	estado_civil int DEFAULT 0,
	lugar_de_residencia varchar(255) DEFAULT 'Mi casa',
	participar_GPS boolean
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE posts (
	identificador int NOT NULL auto_increment PRIMARY KEY,
	id_user int NOT NULL,
	visibilidad int NOT NULL,
	asunto varchar(50) NOT NULL DEFAULT 'Sin Asunto',
	contenido varchar(2048) NOT NULL,
	publicacion_date timestamp NOT NULL,
	numcomentarios int NOT NULL DEFAULT 0,
	calificaciones_positivas int NOT NULL DEFAULT 0,
	calificaciones_negativas int NOT NULL DEFAULT 0,
	revisado int NOT NULL DEFAULT  '0',
	who_revisado int NULL ,
	FOREIGN KEY(id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
	FOREIGN KEY(who_revisado) REFERENCES perfiles(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE comentarios (
	identificador int NOT NULL auto_increment PRIMARY KEY,
	id_post int NOT NULL,
	id_user int NOT NULL,
	visibilidad int NOT NULL,
	contenido varchar(255) NOT NULL,
	publicacion_date timestamp NOT NULL,
	revisado int NOT NULL DEFAULT  '0',
	who_revisado int NULL ,
	FOREIGN KEY(id_post) REFERENCES posts(identificador) ON DELETE CASCADE,
	FOREIGN KEY(id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
	FOREIGN KEY(who_revisado) REFERENCES perfiles(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE mensajes_chat (
	identificador int NOT NULL auto_increment PRIMARY KEY,
	id_sala int NOT NULL,
	id_user int NOT NULL,
	contenido varchar(255) NOT NULL,
	last_update timestamp NOT NULL,
	FOREIGN KEY(id_sala) REFERENCES perfiles(identificador) ON DELETE CASCADE,
	FOREIGN KEY(id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE salas_chat (
	identificador int NOT NULL auto_increment PRIMARY KEY,
	id_user int NOT NULL,
	nombre_sala varchar(70) NOT NULL,
	visibilidad int NOT NULL,
	password char(32) NOT NULL,
FOREIGN KEY(id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `amigos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `id_friend` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
  FOREIGN KEY (id_friend) REFERENCES perfiles(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `denuncias_post` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `id_post` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
  FOREIGN KEY (id_post) REFERENCES posts(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `denuncias_comentario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `id_comentario` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
  FOREIGN KEY (id_comentario) REFERENCES comentarios(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `calificacion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `id_post` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
  FOREIGN KEY (id_post) REFERENCES posts(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `rel_sala_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_user` int(11) NOT NULL,
  `id_sala` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (id_user) REFERENCES perfiles(identificador) ON DELETE CASCADE,
  FOREIGN KEY (id_sala) REFERENCES salas_chat(identificador) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1;
