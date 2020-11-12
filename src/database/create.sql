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
CREATE TABLE IF NOT EXISTS `server-database`.`user` (
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
CREATE TABLE IF NOT EXISTS `server-database`.`message` (
                                                           `id` INT NOT NULL AUTO_INCREMENT,
                                                           `text` VARCHAR(100) NOT NULL,
                                                           `sendtime` DATETIME NOT NULL,
                                                           PRIMARY KEY (`id`))
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `server-database`.`message-author-receiver`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `server-database`.`message-author-receiver` (
                                                                           `messages-id` INT NOT NULL,
                                                                           `author-id` INT NOT NULL,
                                                                           `receiver-id` INT NOT NULL,
                                                                           PRIMARY KEY (`messages-id`, `author-id`, `receiver-id`),
                                                                           INDEX `fk_message-author-receiver_user_idx` (`receiver-id` ASC),
                                                                           INDEX `fk_message-author-receiver_user1_idx` (`author-id` ASC),
                                                                           CONSTRAINT `fk_message-author-receiver_user`
                                                                               FOREIGN KEY (`receiver-id`)
                                                                                   REFERENCES `server-database`.`user` (`id`)
                                                                                   ON DELETE NO ACTION
                                                                                   ON UPDATE NO ACTION,
                                                                           CONSTRAINT `fk_message-author-receiver_user1`
                                                                               FOREIGN KEY (`author-id`)
                                                                                   REFERENCES `server-database`.`user` (`id`)
                                                                                   ON DELETE NO ACTION
                                                                                   ON UPDATE NO ACTION,
                                                                           CONSTRAINT `fk_message-author-receiver_message1`
                                                                               FOREIGN KEY (`messages-id`)
                                                                                   REFERENCES `server-database`.`message` (`id`)
                                                                                   ON DELETE NO ACTION
                                                                                   ON UPDATE NO ACTION)
    ENGINE = InnoDB;


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
CREATE TABLE IF NOT EXISTS `server-database`.`chat_has_user` (
                                                                 `chat_id` INT NOT NULL,
                                                                 `user_id` INT NOT NULL,
                                                                 PRIMARY KEY (`chat_id`, `user_id`),
                                                                 INDEX `fk_chat_has_user_user1_idx` (`user_id` ASC) ,
                                                                 INDEX `fk_chat_has_user_chat1_idx` (`chat_id` ASC) ,
                                                                 CONSTRAINT `fk_chat_has_user_chat1`
                                                                     FOREIGN KEY (`chat_id`)
                                                                         REFERENCES `server-database`.`chat` (`id`)
                                                                         ON DELETE NO ACTION
                                                                         ON UPDATE NO ACTION,
                                                                 CONSTRAINT `fk_chat_has_user_user1`
                                                                     FOREIGN KEY (`user_id`)
                                                                         REFERENCES `server-database`.`user` (`id`)
                                                                         ON DELETE NO ACTION
                                                                         ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure containsUser(IN nick varchar(45),OUT success BOOL)
begin
    set success = exists(select * from user where name=nick);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure containsUserCookie(IN cookie1 varchar(45),OUT success BOOL)
begin
    set success = exists(select * from user where `cookie`=cookie1);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create procedure userLogin(
    in usrname varchar(45),
    in pass varchar(45),
    out success bool)
begin
    set success = exists(select * from user where name=usrname and password=pass);
end;
# ////////////////////////////////////////////////////////////////////////////////////////////////////
create
    definer = admin@`%` procedure containsCookie(IN cookie1 varchar(200), OUT success tinyint(1))
begin
    set success = exists(select * from user where cookie=cookie1);
end;