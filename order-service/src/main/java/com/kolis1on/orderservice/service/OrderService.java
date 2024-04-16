package com.kolis1on.orderservice.service;

import com.kolis1on.orderservice.dto.InventoryResponse;
import com.kolis1on.orderservice.dto.OrderLineItemsDTO;
import com.kolis1on.orderservice.dto.OrderRequest;
import com.kolis1on.orderservice.model.Order;
import com.kolis1on.orderservice.model.OrderLineItems;
import com.kolis1on.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

       List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDTOList()
                .stream()
                .map(this::mapToDTO)
                .toList();

       order.setOrderLineItemsList(orderLineItems);

       List<String> skuCodes = order.getOrderLineItemsList().stream()
               .map(OrderLineItems::getSkuCode)
               .toList();

       InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
               .uri("http://inventory-service/api/inventory",
                       uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
               .retrieve()
               .bodyToMono(InventoryResponse[].class)
               .block();

       boolean allProductsInStock = Arrays.stream(inventoryResponsesArray).allMatch(InventoryResponse::isInStock);
       if(allProductsInStock)
         orderRepository.save(order);
       else{
           throw new IllegalArgumentException("Product is not in stock, please try again later");
       }
    }

    private OrderLineItems mapToDTO(OrderLineItemsDTO orderLineItemsDTO){
        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setId(orderLineItemsDTO.getId());
        orderLineItems.setPrice(orderLineItemsDTO.getPrice());
        orderLineItems.setQuantity(orderLineItemsDTO.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());

        return orderLineItems;
    }

}
