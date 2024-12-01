CREATE DATABASE polardb_catalog;
CREATE DATABASE polardb_order;

ALTER TABLE book
    ADD COLUMN publisher varchar(255);
