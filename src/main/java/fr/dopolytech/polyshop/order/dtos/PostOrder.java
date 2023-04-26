package fr.dopolytech.polyshop.order.dtos;

import java.time.LocalDateTime;
import java.util.List;

import fr.dopolytech.polyshop.order.messages.ProductItem;
import fr.dopolytech.polyshop.order.models.Order;

public class PostOrder extends Order {

  public List<ProductItem> products;

  public PostOrder() {
  }

  public PostOrder(List<ProductItem> products) {
    super(LocalDateTime.now());
    this.products = products;
  }
}