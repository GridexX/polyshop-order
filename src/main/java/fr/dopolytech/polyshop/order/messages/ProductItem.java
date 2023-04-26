package fr.dopolytech.polyshop.order.messages;

public class ProductItem {
  public long productId;
  public long amount;

  public ProductItem() {
  }

  public ProductItem(long productId, long amount) {
    this.productId = productId;
    this.amount = amount;
  }
}