INSERT INTO atm.bills (face_value, quantity)
VALUES (50, 10),
       (20, 30),
       (10, 30),
       (5, 20);


INSERT INTO atm.accounts (account_number, pin, overdraft, balance, created_at)
VALUES ('123456789', '$2a$10$lbZQx9HMVIQSVlF.VS.CS.dOJW7ltWs0mmDqZCUA6vhP37sNV3op6', 200, 800, CURRENT_TIMESTAMP),
       ('987654321', '$2a$10$hzbzXcCOCJf.NQYvyPoe9ulG5fGQiIhyLtrTsH626ti5BFUJlSmO6', 150, 1230, CURRENT_TIMESTAMP);
