TRUNCATE
    atm.accounts,
    atm.bills,
    atm.withdrawals,
    atm.dispensed_bills;

ALTER SEQUENCE atm.accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.bills_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.dispensed_bills_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.withdrawals_id_seq RESTART WITH 1;

INSERT INTO atm.bills (face_value, quantity)
VALUES (50, 0),
       (20, 0),
       (10, 0),
       (5, 0);


INSERT INTO atm.accounts (account_number, pin, overdraft, balance, created_at)
VALUES ('123456789', '$2a$10$lbZQx9HMVIQSVlF.VS.CS.dOJW7ltWs0mmDqZCUA6vhP37sNV3op6', 200, 800, CURRENT_TIMESTAMP),
       ('987654321', '$2a$10$hzbzXcCOCJf.NQYvyPoe9ulG5fGQiIhyLtrTsH626ti5BFUJlSmO6', 150, 1230, CURRENT_TIMESTAMP);