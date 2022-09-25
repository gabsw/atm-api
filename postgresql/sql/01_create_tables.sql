CREATE SCHEMA atm;

CREATE TABLE atm.accounts
(
    id             serial       NOT NULL,
    account_number varchar(255) NOT NULL,
    pin            varchar      NOT NULL,
    overdraft      integer      NOT NULL,
    balance        integer      NOT NULL,
    created_at     timestamp    NOT NULL default now(),
    updated_at     timestamp    NOT NULL default now(),
    CONSTRAINT PK_ACCOUNT_ID PRIMARY KEY (id),
    CONSTRAINT UQ_NUMBER UNIQUE (account_number),
    CONSTRAINT NON_NEG_OVERDRAFT CHECK (overdraft >= 0),
    CONSTRAINT CURRENT_BALANCE CHECK (balance >= -overdraft)
);

CREATE TABLE atm.bills
(
    id         serial    NOT NULL,
    face_value integer   NOT NULL,
    quantity   integer   NOT NULL,
    created_at timestamp NOT NULL default now(),
    updated_at timestamp NOT NULL default now(),
    CONSTRAINT PK_BILL_ID PRIMARY KEY (id),
    CONSTRAINT POSITIVE_VALUE CHECK (face_value > 0),
    CONSTRAINT NON_NEG_QUANTITY CHECK (quantity >= 0)
);

CREATE TABLE atm.withdrawals
(
    id         serial    NOT NULL,
    account_id integer   NOT NULL,
    amount     integer   NOT NULL,
    created_at timestamp NOT NULL default now(),
    CONSTRAINT PK_WITHDRAWAL_ID PRIMARY KEY (id),
    CONSTRAINT POSITIVE_AMOUNT CHECK (amount >= 0),
    CONSTRAINT FK_ACCOUNT_ID FOREIGN KEY (account_id) REFERENCES atm.accounts (id)
);

CREATE TABLE atm.dispensed_bills
(
    id            serial    NOT NULL,
    bill_id       integer   NOT NULL,
    withdrawal_id integer   NOT NULL,
    quantity      integer   NOT NULL,
    created_at    timestamp NOT NULL default now(),
    CONSTRAINT PK_DISPENSED_ID PRIMARY KEY (id),
    CONSTRAINT FK_BILL_ID FOREIGN KEY (bill_id) REFERENCES atm.bills (id),
    CONSTRAINT FK_WITHDRAWAL_ID FOREIGN KEY (withdrawal_id) REFERENCES atm.withdrawals (id)
);