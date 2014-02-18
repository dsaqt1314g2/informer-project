source realmdb-schema.sql

insert into users values('registrado', MD5('informer91'), 'registrado', 'registrado@informer.es');
insert into user_roles values ('blas', 'registered');

insert into users values('administrador', MD5('informer91'), 'administrador', 'admin@informer.es');
insert into user_roles values ('administrador', 'admin');

insert into users values('moderador', MD5('informer91'), 'moderador', 'moderador@informer.es');
insert into user_roles values ('moderador', 'moderador');

insert into users(username, userpass, name, email) values ('ropnom',md5('informer91'),'Rodrigo','rodrigo.sampedro@estudiant.upc.es');
insert into users(username, userpass, name, email) values ('McD0n3ld',md5('informer91'),'Raul','raul.suarez.marin@estudiant.upc.es');
insert into users(username, userpass, name, email) values ('registrado',md5('informer91'),'Registrado','estudiante.registrado@estudiant.upc.es');

insert into user_roles(username, rolename) values ('registrado','registered');
insert into user_roles(username, rolename) values ('McD0n3ld','moderador');
insert into user_roles(username, rolename) values ('ropnom','moderador');

insert into escuelas(escuela, correo) values ("Escola d'Enginyeria de Telecomunicació i Aeroespacial de Castelldefels (UPC)", '@estudiant.upc.es');

