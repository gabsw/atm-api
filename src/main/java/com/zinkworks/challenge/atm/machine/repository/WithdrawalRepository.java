package com.zinkworks.challenge.atm.machine.repository;

import com.zinkworks.challenge.atm.machine.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {
}
