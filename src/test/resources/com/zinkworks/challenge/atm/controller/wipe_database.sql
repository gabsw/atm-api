TRUNCATE
    atm.accounts,
    atm.bills,
    atm.withdrawals,
    atm.dispensed_bills;

ALTER SEQUENCE atm.accounts_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.bills_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.dispensed_bills_id_seq RESTART WITH 1;
ALTER SEQUENCE atm.withdrawals_id_seq RESTART WITH 1;
