package com.payment.payment.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private long amount;
    private String status; // CREATED, PAID, CANCELED
    private Instant createdAt = Instant.now();

}
