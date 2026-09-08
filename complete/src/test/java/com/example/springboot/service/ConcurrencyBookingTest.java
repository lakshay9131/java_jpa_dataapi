package com.example.springboot.service;

import com.example.springboot.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class ConcurrencyBookingTest {

    @Autowired
    private BookingService bookingService;

    @Test
    void testOversellingPrevention_10kRequests() throws InterruptedException {
        int totalRequests = 10_000;
        int concurrentThreads = 100;
        
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(totalRequests);

        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);

        String ticketId = "TICKET-FLASH-SALE-1"; // Initial stock = 100

        for (int i = 0; i < totalRequests; i++) {
            final String userId = "USER-" + i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Hold all threads until signal is given
                    boolean success = bookingService.bookTicket(ticketId, userId);
                    if (success) {
                        successfulBookings.incrementAndGet();
                    } else {
                        failedBookings.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedBookings.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // FIRE ALL 100 THREADS AT ONCE
        finishLatch.await();    // Wait for all 10,000 requests to finish
        executorService.shutdown();

        // Assert exactly 100 seats sold, zero double-bookings
        Assertions.assertEquals(100, successfulBookings.get(), "Overselling occurred! More seats sold than inventory.");
        Assertions.assertEquals(9900, failedBookings.get(), "Failed requests calculation mismatch.");
    }
}