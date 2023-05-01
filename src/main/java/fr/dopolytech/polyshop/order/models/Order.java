package fr.dopolytech.polyshop.order.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@ToString
@Table(name = "order_placed")
public class Order {
    @Id
    @GeneratedValue
    public long id;

    @Column(name = "date", columnDefinition = "TIMESTAMP")
    public LocalDateTime date;

    @Enumerated(EnumType.ORDINAL)
    public OrderStatus status;

    public Order() {
        this.date = LocalDateTime.now();
        System.out.println("Order created at " + date);
        this.status = OrderStatus.created;
    }

    public Order(LocalDateTime date) {
        System.out.println("Order created at " + date);
        this.date = date;
        this.status = OrderStatus.created;
    }
}
