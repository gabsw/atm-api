package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.entity.Bill;
import com.zinkworks.challenge.atm.machine.repository.BillRepository;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
        List<UsedBill> optimalBills = new ArrayList<>();
        int leftOverAmount = requestedAmount;

        for (Bill bill : allBills) {
            int totalBills = leftOverAmount / bill.getFaceValue();

            if (totalBills <= 0) {
                continue;
            }

            leftOverAmount -= bill.getFaceValue() * totalBills;
            optimalBills.add(new UsedBill(bill, totalBills));
        }

        if (leftOverAmount > 0) {
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
        billRepository.saveAll(billsToUpdate);
        return billsToUpdate;
    }
}
