package com.hanaro.shop.service;

public interface SchedulerService {
    
    void updateDeliveryStatusToPreparing();
    
    void updateDeliveryStatusToShipping();
    
    void updateDeliveryStatusToCompleted();
}