source sql-schema.sql
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('ropnom','Rodrigo', true,'1991-02-05','rodri@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('McD0n3ld','Raul',true,'1991-11-17','raul@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('Creador','Sergi',true,'1980-06-06','ser@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('roc','Roc Messeger',true,'1980-06-06','roc@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('alicia','Alicia',true,'1980-06-06','alicia@acme.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('blas','Blas',true,'1980-06-06','blas@acme.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('moderador','Moderador',true,'1970-06-06','moderador@moderador.com',1);

insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('ropnom',0,'Seguiremos los pasos del creador',1,3,0,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('McD0n3ld',0,'Los sicarios os persiguen',1,0,3,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('Creador',0,'Los POJOS son la ostia',1,2,1,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('McD0n3ld',0,'Van dos y se cae el del medio',3,6,6,2);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('alicia',1,'Post para los amiguetes',0,100,0,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('alicia',2,'Post publico',0,0,3,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('blas',2,'Post publico',0,2,1,0);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('blas',0,'Post anonimo',0,6,6,2);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('blas',10,'Post para revisar',0,6,6,2);
insert into posts(username, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('blas',3,'Post eliminado',0,6,6,2);

INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('1', '1', 'ropnom', '0', 'Esto es un comentario', CURRENT_TIMESTAMP, '1', 'ropnom');
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('2', '2', 'McD0n3ld', '0', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '1', 'ropnom');
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('3', '4', 'McD0n3ld', '0', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('4', '4', 'McD0n3ld', '0', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('5', '4', 'McD0n3ld', '0', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);

INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('1', 'ropnom', 'McD0n3ld','1'); 
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('2', 'McD0n3ld', 'ropnom','1');
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('3', 'McD0n3ld', 'Creador','1');
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('4', 'Creador', 'McD0n3ld','1');
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('5', 'alicia', 'McD0n3ld','1'); 
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('6', 'McD0n3ld', 'alicia','1');
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('7', 'blas', 'McD0n3ld','1'); 
INSERT INTO `informerdb`.`amigos` (`id`, `username`, `friend`,`estado`) VALUES ('8', 'McD0n3ld', 'blas','1');

INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('ropnom', '1', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('McD0n3ld', '1', '1');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '1', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '2', '1');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '3', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '4', '1');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '5', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '6', '1');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '7', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '8', '1');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '9', '2');
INSERT INTO `informerdb`.`calificacion` (`username`, `id_post`, `estado`) VALUES ('alicia', '10', '1');

INSERT INTO `informerdb`.`denuncias_comentario` (`id`, `username`, `id_comentario`) VALUES ('1', 'McD0n3ld', '1');

INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '6');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '7');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '8');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('blas', '5');

INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('1', 'ropnom', 'Sala EETAC', '0', MD5('EETAC'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('2', 'McD0n3ld', 'Aeronauticas Calientes', '1', MD5('pass'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('3', 'ropnom', 'Aeronauticas en bolas', '2', MD5('dificil'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('4', 'ropnom', 'Aeronautica', '0', MD5(''));

INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('1', 'ropnom', '1', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('2', 'McD0n3ld', '1', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('3', 'McD0n3ld', '2', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('4', 'ropnom', '3', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('5', 'McD0n3ld', '3', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('6', 'ropnom', '4', '0');

INSERT INTO `informerdb`.`mensajes_chat` (`identificador`, `id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', '1', 'ropnom', 'Este es un ejemplod e mensaje', CURRENT_TIMESTAMP);
