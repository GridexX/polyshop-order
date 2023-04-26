package fr.dopolytech.polyshop.order.messages;

public class ErrorMessage {
  String errorStatus;
  String message;
  String source;
  public long orderId;

  public ErrorMessage(String errorStatus, String message, long orderId) {
    this.errorStatus = errorStatus;
    this.message = message;
    this.source = "order";
    this.orderId = orderId;
  }
}
