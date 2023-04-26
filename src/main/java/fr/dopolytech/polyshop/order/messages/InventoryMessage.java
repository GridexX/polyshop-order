package fr.dopolytech.polyshop.order.messages;

import java.util.List;

public class InventoryMessage extends ShoppingCartMessage {
  public long orderId;
  

  public InventoryMessage() {
  }

  public InventoryMessage(long orderId, List<ProductItem> products) {
    super(products);
    this.orderId = orderId;
  }

}