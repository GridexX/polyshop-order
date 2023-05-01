package fr.dopolytech.polyshop.order.messages;

import fr.dopolytech.polyshop.order.messages.ProductItem;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ErrorMessage {
  String errorStatus;
  String message;
  public String source;
  public long orderId;
  public List<ProductItem> products;

  public ErrorMessage(String errorStatus, String message, long orderId, List<ProductItem> products) {
    this.errorStatus = errorStatus;
    this.message = message;
    this.source = "order";
    this.orderId = orderId;
    this.products = products;
  }
}
