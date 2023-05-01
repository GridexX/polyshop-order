package fr.dopolytech.polyshop.order.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import fr.dopolytech.polyshop.order.dtos.CreateOrderDto;
import fr.dopolytech.polyshop.order.dtos.GetOrderDto;
import fr.dopolytech.polyshop.order.dtos.exceptions.DtoException;
import fr.dopolytech.polyshop.order.dtos.exceptions.ValidationException;
import fr.dopolytech.polyshop.order.models.Order;
import fr.dopolytech.polyshop.order.repositories.OrderRepository;
import fr.dopolytech.polyshop.order.services.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping(produces = "application/json")
    public Iterable<GetOrderDto> findAll() {
        return orderService.findAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public GetOrderDto findById(Long id) {
        return orderService.findById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Order create(@RequestBody CreateOrderDto dto) throws DtoException {
        try {
            dto.validate();
            return orderService.createOrder(dto.toModel());
        } catch (ValidationException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
