package com.example.deliveryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliveryservice.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
}

