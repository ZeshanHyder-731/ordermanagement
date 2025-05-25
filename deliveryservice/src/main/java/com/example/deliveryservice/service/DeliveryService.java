package com.example.deliveryservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.deliveryservice.dto.OrderDto;
import com.example.deliveryservice.dto.PaymentStatus;
import com.example.deliveryservice.model.DeliveryStatus;  // Ensure this enum exists in dto package or import correctly
import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.repository.DeliveryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, OrderDto> kafkaTemplate;  // Kafka producer

    @KafkaListener(topics = "paymentTopic", groupId = "orderDeliveryGroup")
    public void processDelivery(OrderDto orderDto) {
        log.info("Received payment status: {}", orderDto.getPaymentStatus());
        
        if (orderDto.getPaymentStatus() == PaymentStatus.PAYMENT_COMPLETED) {
            // Create delivery record
            Delivery delivery = Delivery.builder()
                .orderId(orderDto.getId())
                .deliveryStatus(DeliveryStatus.DELIVERY_INITIATED)
                .build();
            deliveryRepository.save(delivery);
            
            // Update and send the DTO
            orderDto.setDeliveryStatus(DeliveryStatus.DELIVERY_INITIATED);
            log.info("Sending delivery status update: {}", orderDto);
            kafkaTemplate.send("deliveryTopic", orderDto);
        }
    }
}
