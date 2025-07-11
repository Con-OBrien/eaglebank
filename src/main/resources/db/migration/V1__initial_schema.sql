CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name TEXT NOT NULL,
                       email TEXT UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bank_accounts (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               account_number TEXT UNIQUE NOT NULL,
                               balance NUMERIC(15, 2) DEFAULT 0,
                               created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
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

INSERT INTO users (id, name, email, password) VALUES
                                                  ('11111111-1111-1111-1111-111111111111', 'Alice Example', 'alice@example.com', '$2a$10$FUZLIjwcfI4OYXNUJETjXuOnWZyRRbKmxUTTV15FQn7QSMbkaR6be'),
                                                  ('22222222-2222-2222-2222-222222222222', 'Bob Example', 'bob@example.com', '$2a$10$5m7MzU9MpFJbIxCZqjfT1e01ZKphdWHU13Jk0R5pXv.puO4Ir1seK');

INSERT INTO bank_accounts (id, user_id, account_number, balance) VALUES
                                                                     ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'ACCT1001', 1000.00),
                                                                     ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'ACCT1002', 2500.00);

INSERT INTO transactions (id, account_id, amount, transaction_type, description) VALUES
                                                                                     ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 200.00, 'deposit', 'Initial deposit'),
                                                                                     ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', -150.00, 'withdrawal', 'ATM withdrawal');
