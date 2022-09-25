package com.zinkworks.challenge.atm.repository;

import com.zinkworks.challenge.atm.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    public List<Bill> findAllByOrderByFaceValueDesc();
}
