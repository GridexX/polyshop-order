package fr.dopolytech.polyshop.order.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.dopolytech.polyshop.order.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByOrderId(long orderId);
}
