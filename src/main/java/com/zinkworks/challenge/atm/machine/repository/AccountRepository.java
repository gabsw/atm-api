package com.zinkworks.challenge.atm.machine.repository;

import com.zinkworks.challenge.atm.machine.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByNumber(final String accountNumber);
}
