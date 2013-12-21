insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('ropnom','Rodrigo', true,'1991-02-05','rodri@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('McD0n3ld','Raul',true,'1991-11-17','raul@go.com',1);
insert into perfiles(username, name, genero,fecha_nacimiento, correo, uni_escuela) values ('Creador','Sergi',true,'1980-06-06','ser@go.com',1);

insert into posts(id_user, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('3',0,'Seguiremos los pasos del creador',1,3,0,0);
insert into posts(id_user, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('3',0,'Los sicarios os persiguen',1,0,3,0);
insert into posts(id_user, visibilidad, contenido, numcomentarios, calificaciones_positivas, calificaciones_negativas, revisado) values ('3',0,'Los POJOS son la ostia',1,2,1,0);

INSERT INTO `informerdb`.`comentarios` (`identificador`, `id_post`, `id_user`, `visibilidad`, `contenido`, `publicacion_date`, `revisado`, `who_revisado`) VALUES ('1', '1', '1', '0', 'Esto es un comentario', CURRENT_TIMESTAMP, '1', '1'), ('2', '2', '2', '0', 'Esto es uncomentario 2', CURRENT_TIMESTAMP, '1', '1');

INSERT INTO `informerdb`.`amigos` (`id`, `id_user`, `id_friend`) VALUES ('1', '1', '2'), ('2', '2', '3');

INSERT INTO `informerdb`.`calificacion` (`id`, `id_user`, `id_post`, `estado`) VALUES ('1', '1', '1', '1');

INSERT INTO `informerdb`.`denuncias_comentario` (`id`, `id_user`, `id_comentario`) VALUES ('1', '2', '1');

INSERT INTO `informerdb`.`denuncias_post` (`id`, `id_user`, `id_post`) VALUES ('1', '2', '2');

INSERT INTO `informerdb`.`salas_chat` (`identificador`, `id_user`, `nombre_sala`, `visibilidad`, `password`) VALUES ('1', '1', 'Sala EETAC', '0', 'EETAC');
INSERT INTO `informerdb`.`salas_chat` (`identificador`, `id_user`, `nombre_sala`, `visibilidad`, `password`) VALUES ('2', '2', 'Aeronauticas Calientes', '1', '');

INSERT INTO `informerdb`.`rel_sala_user` (`id`, `id_user`, `id_sala`, `estado`) VALUES ('1', '1', '1', '1');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `id_user`, `id_sala`, `estado`) VALUES ('2', '2', '1', '0');
INSERT INTO `informerdb`.`rel_sala_user` (`id`, `id_user`, `id_sala`, `estado`) VALUES ('3', '2', '2', '1');

INSERT INTO `informerdb`.`mensajes_chat` (`identificador`, `id_sala`, `id_user`, `contenido`, `last_update`) VALUES ('1', '1', '1', 'Este es un ejemplod e mensaje', CURRENT_TIMESTAMP);
