insert into users values('alicia', MD5('alicia'), 'Alicia', 'alicia@acme.com');
insert into user_roles values ('alicia', 'registered');

insert into users values('blas', MD5('blas'), 'Blas', 'blas@acme.com');
insert into user_roles values ('blas', 'registered');

insert into users values('Administrador', MD5('Administrador'), 'Administrador', 'admin@admin.com');
insert into user_roles values ('Administrador', 'admin');


insert into users(username, userpass, name, email) values ('ropnom',md5('ropnom'),'Rodrigo','rodri@go.com');
insert into users(username, userpass, name, email) values ('McD0n3ld',md5('McD0n3ld'),'Raul','raul@go.com');
insert into users(username, userpass, name, email) values ('Creador',md5('Creador'),'Sergi','ser@go.com');
insert into users(username, userpass, name, email) values ('roc',md5('roc'),'Roc Messeger','roc@go.com');

insert into user_roles(username, rolename) values ('Creador','registered');
insert into user_roles(username, rolename) values ('McD0n3ld','registered');
insert into user_roles(username, rolename) values ('roc','registered');
insert into user_roles(username, rolename) values ('ropnom','admin');

