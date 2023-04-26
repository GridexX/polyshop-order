package fr.dopolytech.polyshop.order.messages;

import java.util.List;

public class ShoppingCartMessage {
  public List<ProductItem> products;
  
  public ShoppingCartMessage() {
  }

  public ShoppingCartMessage(List<ProductItem> products) {
    this.products = products;
  }
}