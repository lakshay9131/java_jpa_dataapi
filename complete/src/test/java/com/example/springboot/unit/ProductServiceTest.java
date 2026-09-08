package com.example.springboot.unit;

import com.example.springboot.factory.NotificationSender;
import com.example.springboot.factory.ProductNotificationFactory;
import com.example.springboot.model.Product;
import com.example.springboot.repository.ProductRepository;
import com.example.springboot.service.AsyncNotificationService;
import com.example.springboot.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductNotificationFactory notificationFactory;

    @Mock
    private AsyncNotificationService asyncNotificationService;

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductAndSendNotification() {
        // Arrange
        Product inputProduct = new Product(null, "Laptop", 1200.0, "ELECTRONICS");
        Product savedProduct = new Product("prod-1", "Laptop", 1200.0, "ELECTRONICS");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(notificationFactory.getSender("EMAIL")).thenReturn(notificationSender);
        when(asyncNotificationService.sendAsyncAuditLog(anyString()))
                .thenReturn(CompletableFuture.completedFuture("Audit Logged"));

        // Act
        Product result = productService.createProduct(inputProduct);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(inputProduct);
        verify(notificationFactory, times(1)).getSender("EMAIL");
        verify(asyncNotificationService, times(1)).sendAsyncAuditLog("Product Created: Laptop");
    }

    @Test
    void shouldGetProductById() {
        Product mockProduct = new Product("prod-1", "Phone", 800.0, "ELECTRONICS");
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(mockProduct));

        Product result = productService.getProductById("prod-1");

        assertNotNull(result);
        assertEquals("Phone", result.getName());
    }
}