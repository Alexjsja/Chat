SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
# ////////////////////////////////////////////////////////////////////////////////////////////////////
CREATE SCHEMA IF NOT EXISTS `serverdatabase` DEFAULT CHARACTER SET utf8 ;
USE `serverdatabase` ;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
CREATE TABLE IF NOT EXISTS `serverdatabase`.`users` (
                                                        `id` INT NOT NULL AUTO_INCREMENT,
                                                        `name` VARCHAR(45) NOT NULL,
                                                        `password` VARCHAR(45) NOT NULL,
                                                        `cookie` VARCHAR(200) NOT NULL,
                                                        `mail` VARCHAR(320) NULL,
                                                        `role` ENUM('admin', 'user') NOT NULL DEFAULT 'user',
                                                        `icon` LONGBLOB NULL,
                                                        PRIMARY KEY (`id`))
    ENGINE = InnoDB;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
CREATE TABLE IF NOT EXISTS `serverdatabase`.`messages` (
                                                           `id` INT NOT NULL AUTO_INCREMENT,
                                                           `author` VARCHAR(45) NOT NULL,
                                                           `receiver` VARCHAR(45) NOT NULL,
                                                           `text` VARCHAR(200) NOT NULL,
                                                           `sendtime` DATETIME NOT NULL,
                                                           `users_id` INT NOT NULL,
                                                           PRIMARY KEY (`id`),
                                                           INDEX `fk_messages_users_idx` (`users_id` ASC) VISIBLE,
                                                           CONSTRAINT `fk_messages_users`
                                                               FOREIGN KEY (`users_id`)
                                                                   REFERENCES `serverdatabase`.`users` (`id`)
                                                                   ON DELETE NO ACTION
                                                                   ON UPDATE NO ACTION)
    ENGINE = InnoDB;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure containsUser(IN nick varchar(45),OUT success BOOL)
begin
    set success = exists(select * from users where name=nick);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure containsUserCookie(IN cookie1 varchar(45),OUT success BOOL)
begin
    set success = exists(select * from users where `cookie`=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure userLogin(
    in usrname varchar(45),
    in pass varchar(45),
    out success bool)
begin
    set success = exists(select * from users where name=usrname and password=pass);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure getMore50personalMessages(IN time1 datetime, IN user1 varchar(45),
                                           IN user2 varchar(45), OUT outAuthor varchar(45),
                                           OUT outReceiver varchar(45), OUT outTime datetime,
                                           OUT outText varchar(200))
begin
    select @outAuthor:=author,
           @outReceiver:=receiver,
           @outTime:=sendtime,
           @outText:=text from messages
    where sendtime<time1 and author=user1 and receiver=user2
       or author=user2 and receiver=user1 and sendtime<time1
    order by sendtime desc limit 50;
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = admin@`%` procedure containsCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from users where cookie=cookie1);
end;