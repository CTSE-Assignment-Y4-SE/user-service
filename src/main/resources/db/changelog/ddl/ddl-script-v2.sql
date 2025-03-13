-- File: ddl-script-v2.sql

ALTER TABLE users
    ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
