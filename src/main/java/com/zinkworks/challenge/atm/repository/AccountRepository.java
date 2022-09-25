package com.zinkworks.challenge.atm.repository;

import com.zinkworks.challenge.atm.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByNumber(String accountNumber);
}
