-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema server-database
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema server-database
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `server-database` DEFAULT CHARACTER SET utf8 ;
USE `server-database` ;

-- -----------------------------------------------------
-- Table `server-database`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `server-database`.`users` (
                                                        `id` INT NOT NULL AUTO_INCREMENT,
                                                        `name` VARCHAR(45) NOT NULL,
                                                        `password` VARCHAR(45) NOT NULL,
                                                        `mail` VARCHAR(64) NULL,
                                                        `cookie` VARCHAR(200) NOT NULL,
                                                        `icon` LONGBLOB NULL,
                                                        `role` ENUM('user', 'admin','chat') NULL DEFAULT 'user',
                                                        PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `server-database`.`message`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `server-database`.`messages` (
                                                           `id` INT NOT NULL AUTO_INCREMENT,
                                                           `text` VARCHAR(100) NOT NULL,
                                                           `sendtime` DATETIME(3) NOT NULL,
                                                           PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `server-database`.`message-author-receiver`
-- -----------------------------------------------------
CREATE TABLE `message-author-receiver` (
                                           `messages-id` int NOT NULL,
                                           `author-id` int NOT NULL,
                                           `receiver-id` int NOT NULL,
                                           PRIMARY KEY (`messages-id`,`author-id`,`receiver-id`),
                                           KEY `fk_message-author-receiver_user_idx` (`receiver-id`),
                                           KEY `fk_message-author-receiver_user1_idx` (`author-id`),
                                           CONSTRAINT `fk_message-author-receiver_message1` FOREIGN KEY (`messages-id`) REFERENCES `messages` (`id`),
                                           CONSTRAINT `fk_message-author-receiver_user` FOREIGN KEY (`receiver-id`) REFERENCES `users` (`id`),
                                           CONSTRAINT `fk_message-author-receiver_user1` FOREIGN KEY (`author-id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure containsCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from users where cookie=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure containsNewChatMessages(IN chatId int, IN lastmsgtime datetime(3),
                                                          OUT success tinyint(1))
begin
    set success = exists(
            select a.name as author
            from `message-author-receiver` as mar
                     inner join users as a on a.id=mar.`author-id`
                     inner join users as r on r.id=mar.`receiver-id`
                     inner join messages as msg on msg.id=mar.`messages-id`
            where r.id=chatId and sendtime>lastmsgtime);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure containsNewMessages(IN usr1Cookie varchar(45), IN usr2Id int,
                                                      IN lastmsgtime datetime(3), OUT success tinyint(1))
begin
    set success = exists(
            select a.name as author
            from `message-author-receiver` as mar
                     inner join users as a on a.id=mar.`author-id`
                     inner join users as r on r.id=mar.`receiver-id`
                     inner join messages as msg on msg.id=mar.`messages-id`
            where sendtime>lastmsgtime and a.cookie=usr1Cookie and r.id=usr2Id
               or sendtime>lastmsgtime and a.id=usr2Id and r.cookie=usr1Cookie
        );
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure containsMail(IN mail1 varchar(64), OUT success tinyint(1))
begin
    set success = exists(select * from users where mail=mail1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure containsUserWithCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from users where cookie=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure getCookie(IN mail1 varchar(64), OUT cookie1 varchar(200))
begin
    select cookie into cookie1 from users where mail=mail1;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure getNewChatMessages(IN chatid int, IN lastmsgtime datetime(3))
begin
    select a.name as author,a.role,a.id,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where r.id=chatid and sendtime>lastmsgtime
    order by sendtime desc limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = admin@`%` procedure getNewMessages(IN usr1Cookie varchar(45), IN usr2Id varchar(45), IN lastmsgtime datetime(3))
begin
    select a.name as author,a.role,a.id,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where sendtime>lastmsgtime and a.cookie=usr1Cookie and r.id=usr2Id
       or sendtime>lastmsgtime and a.id=usr2Id and r.cookie=usr1Cookie
    order by sendtime desc limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure putMessage(IN authorCookie varchar(45), IN receiverid int,
                                             IN msgtext varchar(100))
begin
    declare authorid varchar(45);
    declare msgid varchar(45);
    set authorid= (select id from users where cookie=authorCookie limit 1);
    insert into messages(text, sendtime) values(msgtext,now(3));
    set msgid = (select last_insert_id() from messages limit 1);
    insert into `message-author-receiver`(`messages-id`, `author-id`, `receiver-id`)
    values (msgid,authorid,receiverid);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure userLogin(IN mail1 varchar(64), IN pass varchar(45), OUT success tinyint(1))
begin
    set success = exists(select * from users where mail=mail1 and password=pass);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure getStartChatMessages(IN chatid int)
begin
    select a.name as author,a.role,a.id,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where r.id=chatid
    order by sendtime desc limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure getUserInfo(in userid int)
begin
    select name,mail,id,role from users where id=userid;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure containsUserById(in userid int,out success boolean)
begin
    set success = exists(select * from users where id=userid);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    procedure getStartMessages(IN userId int,in authorCookie varchar(200))
begin
    select a.name as author,a.role,a.id,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where r.id=userId and a.cookie=authorCookie or
                a.id=userId and r.cookie=authorCookie
    order by sendtime desc limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
insert into users (id, name, password, mail, cookie, role)
values (1,'home-chat','home-chat','home-chat','home-chat','chat');




