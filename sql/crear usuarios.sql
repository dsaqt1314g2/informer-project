create user 'realm'@'localhost' identified by 'realm';
grant all privileges on realm-db.* to 'realm'@'localhost';
flush privileges;

create user 'informer'@'localhost' identified by 'informer';
grant all privileges on informerdb.* to 'informer'@'localhost';
flush privileges;
exit;