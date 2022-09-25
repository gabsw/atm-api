package com.zinkworks.challenge.atm.machine.service;

import com.zinkworks.challenge.atm.machine.entity.Bill;
import com.zinkworks.challenge.atm.machine.repository.BillRepository;
import com.zinkworks.challenge.atm.machine.validation.NotEnoughBillsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class BillServiceTests {
    private final static Instant createdAt = Instant.parse("2022-09-25T10:00:00Z");

    @Mock
    BillRepository billRepository;

    @InjectMocks
    BillService billService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAllBillsByDescendingFaceValue() {
        when(billRepository.findAllByOrderByFaceValueDesc()).thenReturn(createStartingBills());

        List<Bill> fetchedBills = billService.fetchAllBillsByDescendingFaceValue();
        List<Integer> sortedFaceValues = fetchedBills.stream().map(Bill::getFaceValue).toList();

        assertEquals(4, fetchedBills.size());
        assertEquals(List.of(50, 20, 10, 5), sortedFaceValues);
    }

    @Test
    void updateBills() {
        when(billRepository.saveAll(createBillsToUpdate())).thenReturn(createUpdatedBills());

        final List<Bill> updatedBills = billService.updateBills(createUsedBills());

        final List<Integer> sortedFaceValues = updatedBills.stream().map(Bill::getFaceValue).toList();
        final List<Integer> sortedQuantities = updatedBills.stream().map(Bill::getQuantity).toList();

        verify(billRepository, times(1)).saveAll(createBillsToUpdate());

        assertEquals(List.of(10, 5), sortedFaceValues);
        assertEquals(List.of(29, 19), sortedQuantities);
    }

    @Test
    void optimalBillsCombination() throws NotEnoughBillsException {
        List<UsedBill> optimalBills = billService.optimalBillsCombination(createStartingBills(), 85);

        List<Integer> sortedFaceValues = optimalBills.stream()
                                                     .map(UsedBill::getBill)
                                                     .map(Bill::getFaceValue)
                                                     .toList();

        List<Integer> sortedQuantities = optimalBills.stream()
                                                     .map(UsedBill::getQuantity)
                                                     .toList();

        assertEquals(List.of(50, 20, 10, 5), sortedFaceValues);
        assertEquals(List.of(1, 1, 1, 1), sortedQuantities);
    }

    @Test
    void wronglySortedBillsInput() {
        final List<Bill> ascendingStartingBills = createStartingBills().stream()
                                                                       .sorted(Comparator.comparing(Bill::getFaceValue))
                                                                       .toList();
        assertThrows(IllegalArgumentException.class,
                     () -> billService.optimalBillsCombination(ascendingStartingBills, 15));
    }

    @Test
    void notEnoughBills() {
        assertThrows(NotEnoughBillsException.class,
                     () -> billService.optimalBillsCombination(createStartingBills(), 999999999));
    }

    private List<Bill> createStartingBills() {
        final Bill fiftyBill = Bill.builder()
                                   .id(123)
                                   .faceValue(50)
                                   .quantity(10)
                                   .createdAt(createdAt)
                                   .build();

        final Bill twentyBill = Bill.builder()
                                    .id(456)
                                    .faceValue(20)
                                    .quantity(30)
                                    .createdAt(createdAt)
                                    .build();

        final Bill tenBill = Bill.builder()
                                 .id(789)
                                 .faceValue(10)
                                 .quantity(30)
                                 .createdAt(createdAt)
                                 .build();

        final Bill fiveBill = Bill.builder()
                                  .id(321)
                                  .faceValue(5)
                                  .quantity(20)
                                  .createdAt(createdAt)
                                  .build();

        return List.of(fiftyBill, twentyBill, tenBill, fiveBill);
    }

    private List<UsedBill> createUsedBills() {
        final Bill fiveBill = Bill.builder()
                                  .id(321)
                                  .faceValue(5)
                                  .quantity(20)
                                  .createdAt(createdAt)
                                  .build();

        final UsedBill fiveUsedBill = UsedBill.builder()
                                              .bill(fiveBill)
                                              .quantity(1)
                                              .build();

        final Bill tenBill = Bill.builder()
                                 .id(789)
                                 .faceValue(10)
                                 .quantity(30)
                                 .createdAt(createdAt)
                                 .build();

        final UsedBill tenUsedBill = UsedBill.builder()
                                             .bill(tenBill)
                                             .quantity(1)
                                             .build();

        return List.of(fiveUsedBill, tenUsedBill);
    }

    private List<Bill> createBillsToUpdate() {
        final Bill updatedTenBill = Bill.builder()
                                        .id(789)
                                        .faceValue(10)
                                        .quantity(29)
                                        .createdAt(createdAt)
                                        .build();

        final Bill updatedFiveBill = Bill.builder()
                                         .id(321)
                                         .faceValue(5)
                                         .quantity(19)
                                         .createdAt(createdAt)
                                         .build();

        return List.of(updatedFiveBill, updatedTenBill);
    }

    private List<Bill> createUpdatedBills() {
        final Bill updatedTenBill = Bill.builder()
                                        .id(789)
                                        .faceValue(10)
                                        .quantity(29)
                                        .createdAt(createdAt)
                                        .updatedAt(createdAt.plus(1, SECONDS))
                                        .build();

        final Bill updatedFiveBill = Bill.builder()
                                         .id(321)
                                         .faceValue(5)
                                         .quantity(19)
                                         .createdAt(createdAt)
                                         .updatedAt(createdAt.plus(1, SECONDS))
                                         .build();

        return List.of(updatedTenBill, updatedFiveBill);
    }
}
