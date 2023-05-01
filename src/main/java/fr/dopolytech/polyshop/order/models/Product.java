package fr.dopolytech.polyshop.order.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "order_id")
    private long orderId;

    public Product() {
    }

    public Product(String productId, long quantity, long orderId) {
        this.productId = productId;
        this.quantity = quantity;
        this.orderId = orderId;
    }

    public Product(String name, double price, long quantity, long orderId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.orderId = orderId;
    }

}
