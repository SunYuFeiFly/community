CREATE TABLE USER
(
    ID int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ACCOUNTID VARCHAR(100),
    NAME VARCHAR(50),
    TOKEN VARCHAR(36),
    GMTCREATE BIGINT,
    GMTMODIFIED BIGINT
);