package fr.dopolytech.polyshop.order.dtos;


import java.util.List;

import fr.dopolytech.polyshop.order.dtos.exceptions.DtoException;
import fr.dopolytech.polyshop.order.dtos.exceptions.ValidationException;
import fr.dopolytech.polyshop.order.messages.ProductItem;

public class CreatePostOrderDto implements Dto<PostOrder> {

  List<ProductItem> products;

  @Override
  public void validate() throws DtoException {
    if (this.products == null || this.products.isEmpty()) {
      throw new ValidationException("products cannot be null");
    }
  }

  @Override
  public PostOrder toModel() {
    return new PostOrder();
  }

  @Override
  public Dto<PostOrder> fromModel(PostOrder model) {
    return this;
  }
}