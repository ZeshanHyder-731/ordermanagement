package com.example.paymentservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.paymentservice.dto.OrderDto;
import com.example.paymentservice.dto.OrderStatus;
import com.example.paymentservice.dto.PaymentStatus;
import com.example.paymentservice.model.UserBalance;
import com.example.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

@Autowired
private PaymentRepository paymentRepository;

private final KafkaTemplate<String, OrderDto> kafkaTemplate;

// Pull the orderDto from the orderCreatedTopic topic
@KafkaListener(topics = "orderCreatedTopic", groupId = "orderEventGroup" )
public void processpayment(OrderDto orderDto){
    log.info("Log message - recieved from order topic: {} ", orderDto.toString());

UserBalance userBalance = paymentRepository.findById(orderDto.getUserId()).get();
    log.info("Log message - userBalance: {} ", userBalance.getBalance());

    //both values are BigDecimal, and we can compare them using value1.compareTo(value2). For value1 and value2 the result is -1, 0, or 1 as value2 is numerically less than, equal to, or greater than value1.
    if (userBalance.getBalance().compareTo(orderDto.getPrice()) == 1) {
        // again both values are BigDecimal, and we can subtract one of them from the other using  value1.subtract(value2).
        userBalance.setBalance(userBalance.getBalance().subtract(orderDto.getPrice()));
        // update the user balance in the database
        paymentRepository.save(userBalance);

        log.info("Log message - current user balance is: {} ",  userBalance.getBalance());

        // update payment and order status after successful payment
        orderDto.setPaymentStatus(PaymentStatus.PAYMENT_COMPLETED);
        orderDto.setOrderStatus(OrderStatus.ORDER_COMPLETED);

        log.info("Log message - a payment for order id: {} has been completed successfully", orderDto.toString());

        // Send the outcome of payment as an object to the  paymentTopic
        kafkaTemplate.send("paymentTopic", orderDto);

      } else {
        log.info("Log message - a payment for order {} has been failed ", orderDto.toString());
        
        // update payment and order status after payment failure
        orderDto.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
        orderDto.setOrderStatus(OrderStatus.ORDER_CANCELLED);

        // Send the outcome of payment as an object to the  paymentTopic
        kafkaTemplate.send("paymentTopic", orderDto);
      } 
  }
}