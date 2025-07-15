-- Extend users table
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bank_accounts;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name TEXT NOT NULL,
                       email TEXT UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       phone_number TEXT NOT NULL,
                       created_timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE address (
                         id UUID PRIMARY KEY,
                         user_id UUID UNIQUE NOT NULL,
                         line1 TEXT NOT NULL,
                         line2 TEXT,
                         line3 TEXT,
                         town TEXT NOT NULL,
                         county TEXT NOT NULL,
                         postcode TEXT NOT NULL,
                         CONSTRAINT fk_user_address FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE bank_accounts (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               account_number TEXT UNIQUE NOT NULL,
                               sort_code TEXT NOT NULL DEFAULT '10-10-10',
                               name TEXT NOT NULL,
                               account_type TEXT NOT NULL,
                               balance NUMERIC(15, 2) NOT NULL DEFAULT 0,
                               currency TEXT NOT NULL DEFAULT 'GBP',
                               created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              account_id UUID NOT NULL,
                              amount NUMERIC(15, 2) NOT NULL,
                              transaction_type TEXT NOT NULL,
                              description TEXT,
                              transaction_date TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_account FOREIGN KEY(account_id) REFERENCES bank_accounts(id)
);