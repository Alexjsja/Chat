create table users
(
    id int auto_increment,
    name varchar(45) not null,
    password varchar(45) not null,
    `cookie-hash` int not null,
    constraint users_pk
        primary key (id)
);

create table messages
(
    id int auto_increment,
    author varchar(45) not null,
    receiver varchar(45) not null,
    `send-datetime` datetime not null,
    constraint messages_pk
        primary key (id)
);

create procedure containsUser(IN nick varchar(45),OUT success BOOL)
begin
    set success = exists(select * from users where name=nick);
end;

create procedure containsUser(IN hash int,OUT success BOOL)
begin
    set success = exists(select * from users where `cookie-hash`=hash);
end;

create procedure userLogin(in usrname varchar(45),in pass varchar(45),out success bool)
begin
    set success = exists(select * from users where name=usrname and password=pass);
end;
