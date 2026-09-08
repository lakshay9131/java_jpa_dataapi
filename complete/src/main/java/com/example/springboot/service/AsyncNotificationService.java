package com.example.springboot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncNotificationService {

    @Async
    public CompletableFuture<String> sendAsyncAuditLog(String logMessage) {
        try {
            Thread.sleep(500); // Simulate background processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture("LOGGED: " + logMessage);
    }
}