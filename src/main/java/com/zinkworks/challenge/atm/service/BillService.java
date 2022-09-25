package com.zinkworks.challenge.atm.service;

import com.zinkworks.challenge.atm.entity.Bill;
import com.zinkworks.challenge.atm.pojo.UsedBill;
import com.zinkworks.challenge.atm.repository.BillRepository;
import com.zinkworks.challenge.atm.validation.NotEnoughBillsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.common.collect.Comparators;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

@Service
@Slf4j
public class BillService {

    final BillRepository billRepository;

    public BillService(final BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> fetchAllBillsByDescendingFaceValue() {
        return billRepository.findAllByOrderByFaceValueDesc();
    }

    public List<UsedBill> optimalBillsCombination(final List<Bill> allBills, final int requestedAmount)
        throws NotEnoughBillsException {

        final boolean isOrdered = Comparators.isInOrder(allBills, comparing(Bill::getFaceValue).reversed());
        if (!isOrdered) {
            log.error("optimalBillsCombination received unordered list of all bills");
            throw new IllegalArgumentException("allBills must be ordered by their faceValue descending");
        }

        List<UsedBill> optimalBills = new ArrayList<>();
        int leftOverAmount = requestedAmount;

        for (Bill bill : allBills) {
            if (bill.getQuantity() == 0) {
                continue;
            }

            int totalBills = leftOverAmount / bill.getFaceValue();

            if (totalBills <= 0) {
                continue;
            }

            leftOverAmount -= bill.getFaceValue() * totalBills;
            optimalBills.add(new UsedBill(bill, totalBills));
        }

        if (leftOverAmount > 0) {
            log.error("Failed to proceed with withdrawal of {} due to the lack of bills in the value of {}",
                      requestedAmount,
                      leftOverAmount);
            throw new NotEnoughBillsException("Not enough bills in the ATM to proceed with the operation.");
        }
        return optimalBills;
    }

    // O (n)
    public List<Bill> updateBills(final List<UsedBill> usedBills) {
        for (UsedBill usedBill : usedBills) {
            final Bill originalBill = usedBill.getBill();
            final int newQuantity = originalBill.getQuantity() - usedBill.getQuantity();
            originalBill.setQuantity(newQuantity);
        }

        final List<Bill> billsToUpdate = usedBills.stream().map(UsedBill::getBill).toList();
        return billRepository.saveAll(billsToUpdate);
    }
}
