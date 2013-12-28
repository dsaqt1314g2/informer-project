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
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('2', '2', 'McD0n3ld', '1', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '1', 'ropnom');
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('3', '4', 'McD0n3ld', '1', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('4', '4', 'McD0n3ld', '1', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('5', '4', 'McD0n3ld', '2', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('6', '9', 'McD0n3ld', '2', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('7', '9', 'McD0n3ld', '3', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);
INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `username`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('8', '9', 'McD0n3ld', '10', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '0', NULL);

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
INSERT INTO `informerdb`.`denuncias_comentario` (`id`, `username`, `id_comentario`) VALUES ('2', 'alicia', '6');

INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '6');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '7');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('McD0n3ld', '8');
INSERT INTO `informerdb`.`denuncias_post` (`username`, `id_post`) VALUES ('blas', '5');

INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('1', 'ropnom', 'Sala EETAC', '0', MD5('EETAC'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('2', 'McD0n3ld', 'Aeronauticas Calientes', '1', MD5('pass'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('3', 'ropnom', 'Aeronauticas en bolas', '2', MD5('dificil'));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('4', 'ropnom', 'Aeronautica', '0', MD5(''));
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `username`, `nombre_sala`, `visibilidad`, `password`) VALUES ('5', 'alicia', 'Sala 1337', '0', MD5(''));

INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('1', 'ropnom', '1', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('2', 'McD0n3ld', '1', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('3', 'McD0n3ld', '2', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('4', 'ropnom', '3', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('5', 'McD0n3ld', '3', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('6', 'ropnom', '4', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('7', 'ropnom', '3', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('8', 'ropnom', '4', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('9', 'ropnom', '5', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('10', 'McD0n3ld', '5', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('11', 'alicia', '5', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('12', 'Creador', '5', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `username`, `id_sala`, `estado`) VALUES ('13', 'blas', '5', '1');


INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'Hola', CURRENT_TIMESTAMP);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'Hay alguien ahi?', CURRENT_TIMESTAMP+1);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'McD0n3ld', 'Si, que pasa?', CURRENT_TIMESTAMP+2);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', '...', CURRENT_TIMESTAMP+3);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'McD0n3ld', 'te cuento un chiste?', CURRENT_TIMESTAMP+4);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'Nononono', CURRENT_TIMESTAMP+5);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'Resulta que he hecho un fetch y...', CURRENT_TIMESTAMP+6);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'me ha dado un conflicto de la ostia', CURRENT_TIMESTAMP+7);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'he tenido que borrar todo y volverlo a hacer', CURRENT_TIMESTAMP+8);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'McD0n3ld', 'vamos que las has liado parda no?', CURRENT_TIMESTAMP+9);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'mas o menos', CURRENT_TIMESTAMP+10);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'un poco mas y se crea un agujero negro en el server de github', CURRENT_TIMESTAMP+11);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'McD0n3ld', 'jajajajjaa', CURRENT_TIMESTAMP+12);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('1', 'ropnom', 'fin de la conversacion. :)', CURRENT_TIMESTAMP+13);

INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'Hola', CURRENT_TIMESTAMP);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'Hay alguien ahi?', CURRENT_TIMESTAMP+1);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'yo, dime XD', CURRENT_TIMESTAMP+2);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'alicia', 'yo tambien ando por aqui, que pasa?', CURRENT_TIMESTAMP+3);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'nada, el Creador, que se ha enfadado', CURRENT_TIMESTAMP+4);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'blas', 'Creador? que pasa?', CURRENT_TIMESTAMP+5);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'Creador', 'que ropnom hizo un fetch y dio fail', CURRENT_TIMESTAMP+6);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'conflictos?', CURRENT_TIMESTAMP+7);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'Si', CURRENT_TIMESTAMP+8);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'Creador', 'ha hecho un agujero negro en el server de githun........', CURRENT_TIMESTAMP+9);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'alicia', 'no pasa nada. que nos absorba', CURRENT_TIMESTAMP+10);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'Creador', '-.-', CURRENT_TIMESTAMP+11);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'blas', 'jajajajjaa', CURRENT_TIMESTAMP+12);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'alicia', 'jajajjajaa', CURRENT_TIMESTAMP+13);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'jajajajjaja', CURRENT_TIMESTAMP+14);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'jajajajjajaj', CURRENT_TIMESTAMP+15);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'hola caracola', CURRENT_TIMESTAMP+16);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'alicia', '-.-', CURRENT_TIMESTAMP+17);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'blas', 'ya no sabes que poner?', CURRENT_TIMESTAMP+18);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'si', CURRENT_TIMESTAMP+19);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'ropnom', 'estais mal de la chota?', CURRENT_TIMESTAMP+20);
INSERT INTO `informerdb`.`mensajes_chat` (`id_sala`, `username`, `contenido`, `last_update`) VALUES ('5', 'McD0n3ld', 'fin de la conversacion ;)', CURRENT_TIMESTAMP+21);
