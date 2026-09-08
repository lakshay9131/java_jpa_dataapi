package com.example.springboot.factory;

import org.springframework.stereotype.Component;

class EmailNotificationSender implements NotificationSender {
    @Override
    public String send(String message) {
        return "Email Sent: " + message;
    }
}

class SMSNotificationSender implements NotificationSender {
    @Override
    public String send(String message) {
        return "SMS Sent: " + message;
    }
}

@Component
public class ProductNotificationFactory {
    public NotificationSender getSender(String type) {
        if ("SMS".equalsIgnoreCase(type)) {
            return new SMSNotificationSender();
        }
        return new EmailNotificationSender();
    }
}