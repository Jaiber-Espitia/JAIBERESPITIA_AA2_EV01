package eshop.prod.database.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eshop.prod.database.entities.Order;
import eshop.prod.database.entities.OrderItem;
import eshop.prod.database.entities.dto.OrderDTO;
import eshop.prod.database.entities.dto.OrderItemDTO;
import eshop.prod.database.entities.mappers.OrderItemMapper;
import eshop.prod.database.entities.mappers.OrderMapper;
import eshop.prod.database.repository.CustomerRepository;
import eshop.prod.database.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
    /* C-> CREATE R-> READ U-> UPDATE D-> DELETE */
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;

    // READ ALL
    public List<OrderDTO> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            return orders.stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList();
        } catch (Exception e) {
            log.error("Error getting all orders", e);
        }
        return List.of();
    }

    // Read by ID
    public OrderDTO getOrderById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            Order order = orderRepository.findById(id).orElse(null);
            return OrderMapper.INSTANCE.orderToOrderDTO(order);
        } catch (Exception e) {
            log.error("Error getting order by id", e);
        }
        return null;
    }

    // CREATE
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            if (orderDTO.getId_order() != null) {
                throw new IllegalArgumentException("Id will be generated by database");
            }
            Order order = OrderMapper.INSTANCE.orderDTOToOrder(orderDTO, customerRepository);
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            order = orderRepository.save(order);
            return OrderMapper.INSTANCE.orderToOrderDTO(order);
        } catch (Exception e) {
            log.error("Error creating order", e);
        }
        return null;
    }

    // UPDATE
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            Order orderFromDB = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
            Order order = OrderMapper.INSTANCE.orderDTOToOrder(orderDTO, customerRepository);
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            orderFromDB.updateOnlyNecessary(order);
            Order savedOrder=orderRepository.save(orderFromDB);
            return OrderMapper.INSTANCE.orderToOrderDTO(savedOrder);
        } catch (Exception e) {
            log.error("Error updating order", e);
        }
        return null;
    }

    // delete
    public boolean deleteOrder(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            orderRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting order", e);
        }
        return false;
    }

    /*
     * métodos del repository
     * (search for orders between two dates)
     */

    public List<OrderDTO> findByDate(Timestamp order1, Timestamp order2) {
        try {
            List<Order> orders = orderRepository.findByDateBetween(order1, order2).orElse(null);
            if (orders == null) {
                throw new IllegalArgumentException("Orders cannot be null");
            }
            return orders.stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList();
        } catch (Exception e) {
            log.error("Error getting orders by date", e);
        }
        return List.of();
    }

    /* order by id CUSTOMER */
    public List<OrderDTO> findAllOrderByCustomerId(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            List<Order> orders = orderRepository.findAllOrderByCustomerId(id).orElse(null);
            if (orders == null) {
                throw new IllegalArgumentException("Orders cannot be null");
            }
            return orders.stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList();
        } catch (Exception e) {
            log.error("Error getting orders by customer", e);
        }
        return List.of();
    }

    /* Search orders by customer and a status */
    public List<OrderDTO> findByCustomerAndStatus(Long customer, String status) {
        try {
            List<Order> orders = orderRepository.findByCustomerAndStatus(customer, status).orElse(null);
            if (orders == null) {
                throw new IllegalArgumentException("Orders cannot be null");
            }
            return orders.stream().map(OrderMapper.INSTANCE::orderToOrderDTO).toList();
        } catch (Exception e) {
            log.error("Error getting orders by customer and status", e);
        }
        return List.of();
    }

    /* retrieve orders with their items using JOIN fetch */
    public List<OrderItemDTO> findByIdWithOrderItems(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            List<OrderItem> order = orderRepository.findByIdWithOrderItems(id).orElse(null);
            if (order == null) {
                throw new IllegalArgumentException("Order not found");
            }
            return order.stream().map(OrderItemMapper.INSTANCE::orderItemToOrderItemDTO).toList();
        } catch (Exception e) {
            log.error("Error getting order by id with order items", e);
        }
        return List.of();
    }
}