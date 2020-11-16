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
                                                        `role` ENUM('user', 'admin') NULL DEFAULT 'user',
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
-- -----------------------------------------------------
-- Table `server-database`.`chat`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `server-database`.`chat` (
                                                        `id` INT NOT NULL,
                                                        PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `server-database`.`chat_has_user`
-- -----------------------------------------------------
CREATE TABLE `chat_has_user` (
                                 `chat_id` int NOT NULL,
                                 `user_id` int NOT NULL,
                                 PRIMARY KEY (`chat_id`,`user_id`),
                                 KEY `fk_chat_has_user_user1_idx` (`user_id`),
                                 KEY `fk_chat_has_user_chat1_idx` (`chat_id`),
                                 CONSTRAINT `fk_chat_has_user_chat1` FOREIGN KEY (`chat_id`) REFERENCES `chat` (`id`),
                                 CONSTRAINT `fk_chat_has_user_user1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = admin@`%` procedure containsCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from users where cookie=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure containsNewChatMessages(IN chatname varchar(45), IN lastmsgtime datetime(3),
                                                               OUT success tinyint(1))
begin
    set success = exists(
            select a.name as author
            from `message-author-receiver` as mar
                     inner join users as a on a.id=mar.`author-id`
                     inner join users as r on r.id=mar.`receiver-id`
                     inner join messages as msg on msg.id=mar.`messages-id`
            where r.name=chatname and sendtime>lastmsgtime);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure containsNewMessages(IN usr1 varchar(45), IN usr2 varchar(45),
                                                           IN lastmsgtime datetime(3), OUT success tinyint(1))
begin
    set success = exists(
            select a.name as author
            from `message-author-receiver` as mar
                     inner join users as a on a.id=mar.`author-id`
                     inner join users as r on r.id=mar.`receiver-id`
                     inner join messages as msg on msg.id=mar.`messages-id`
            where sendtime>lastmsgtime and a.name=usr1 and r.name=usr2
               or sendtime>lastmsgtime and a.name=usr2 and r.name=usr1
        );
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure containsMail(IN mail1 varchar(64), OUT success tinyint(1))
begin
    set success = exists(select * from users where mail=mail1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure containsUserWithCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from users where cookie=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure getCookie(IN mail1 varchar(64), OUT cookie1 varchar(200))
begin
    select cookie into cookie1 from users where mail=mail1;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure getNewChatMessages(IN chatname varchar(45), IN lastmsgtime datetime(3))
begin
    select a.name as author,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where r.name=chatname and sendtime>lastmsgtime
    order by sendtime limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure getNewMessages(IN usr1 varchar(45), IN usr2 varchar(45),
                                                      IN lastmsgtime datetime(3))
begin
    select a.name as author,
           msg.text,msg.sendtime
    from `message-author-receiver` as mar
             inner join users as a on a.id=mar.`author-id`
             inner join users as r on r.id=mar.`receiver-id`
             inner join messages as msg on msg.id=mar.`messages-id`
    where sendtime>lastmsgtime and a.name=usr1 and r.name=usr2
       or sendtime>lastmsgtime and a.name=usr2 and r.name=usr1
    order by sendtime limit 20;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = admin@`%` procedure putMessage(IN authorCookie varchar(45), IN receiver varchar(45), IN msgtext varchar(100))
begin
    declare authorid varchar(45);
    declare receiverid varchar(45);
    declare msgid varchar(45);
    set authorid= (select id from users where cookie=authorCookie);
    set receiverid=(select id from users where name=receiver);
    insert into messages(text, sendtime) values(msgtext,now(3));
    set msgid = (select last_insert_id() from messages limit 1);
    insert into `message-author-receiver`(`messages-id`, `author-id`, `receiver-id`)
    values (msgid,authorid,receiverid);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = root@localhost procedure userLogin(IN mail1 varchar(64), IN pass varchar(45), OUT success tinyint(1))
begin
    set success = exists(select * from users where mail=mail1 and password=pass);
end;




