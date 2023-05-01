package fr.dopolytech.polyshop.order.models;

public enum OrderStatus {
  created,
  confirmed,
  missing_stock,
  paid,
  missing_payment,
  shipped,
  missing_shipping,
}
