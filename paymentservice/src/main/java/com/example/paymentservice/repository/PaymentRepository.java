package com.example.paymentservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.paymentservice.model.UserBalance;

public interface PaymentRepository extends JpaRepository<UserBalance, Integer> {
}
