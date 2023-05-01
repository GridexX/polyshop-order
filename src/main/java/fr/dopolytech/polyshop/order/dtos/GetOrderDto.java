package fr.dopolytech.polyshop.order.dtos;

import java.time.LocalDateTime;
import java.util.List;

import fr.dopolytech.polyshop.order.messages.ProductItem;
import fr.dopolytech.polyshop.order.models.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class GetOrderDto {
  long id;
  LocalDateTime date;
  OrderStatus status;
  List<ProductItem> products;
}
