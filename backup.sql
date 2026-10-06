PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE subject (id integer, subject_name varchar(255), primary key (id));
INSERT INTO subject VALUES(1,'General 1');
INSERT INTO subject VALUES(2,'General 2');
CREATE TABLE vocab (id integer, eng varchar(255), esp varchar(255), subject_id bigint, primary key (id));
INSERT INTO vocab VALUES(1,'Hello','Hola',1);
COMMIT;
