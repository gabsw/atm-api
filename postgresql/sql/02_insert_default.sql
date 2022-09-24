INSERT INTO atm_machine.bills (face_value, quantity)
VALUES
    (50, 10),
    (20, 30),
    (10, 30),
    (5, 20);


INSERT INTO atm_machine.accounts (unique_number, pin, overdraft, balance, created_at)
VALUES
    ('123456789', '1234', 200, 800, CURRENT_TIMESTAMP),
    ('987654321', '4321', 150, 1230, CURRENT_TIMESTAMP);
