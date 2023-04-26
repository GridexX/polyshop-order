package fr.dopolytech.polyshop.order.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.netflix.config.validation.ValidationException;

import fr.dopolytech.polyshop.order.dtos.CreatePostOrderDto;
import fr.dopolytech.polyshop.order.dtos.PostOrder;
import fr.dopolytech.polyshop.order.dtos.exceptions.DtoException;
import jakarta.ws.rs.core.MediaType;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/")
public class GenericController {

  // TODO : add a repository
  @PostMapping(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public PostOrder create(@RequestBody CreatePostOrderDto dto) throws DtoException {
    try {
        dto.validate();
        // return orderRepository.save(dto.toModel());
        return null;
    } catch (ValidationException e) {
        throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
    }
}
}