-- File: ddl-script-v3.sql

DROP TABLE IF EXISTS otp;

CREATE TABLE otp (
    otp_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE
);
