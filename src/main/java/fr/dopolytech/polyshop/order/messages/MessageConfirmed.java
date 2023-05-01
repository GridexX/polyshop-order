package fr.dopolytech.polyshop.order.messages;

public class MessageConfirmed {
  public long orderId;
  public String status;

  public MessageConfirmed() {
  }

  public MessageConfirmed(long orderId, String status) {
    this.orderId = orderId;
    this.status = status;
  }
}