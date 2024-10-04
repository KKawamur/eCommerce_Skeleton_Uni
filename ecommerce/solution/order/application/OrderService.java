package thkoeln.archilab.ecommerce.solution.order.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientRepository;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPartRepository;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.ProductRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.UsableOrderParts;
import thkoeln.archilab.ecommerce.usecases.OrderUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService extends UsableOrderParts implements OrderUseCases {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderPartRepository orderPartRepository;

    @Override
    @Transactional
    public Map<UUID, Integer> getOrderHistory(EmailType clientEmail) {
        Map<UUID, Integer> orderHistory = new HashMap<>();
        if (!clientRepository.existsByEmail((Email) clientEmail)) {
            throw new ShopException("Client doesn't exist!");
        } else {
            List<Order> orders = orderRepository.findMultipleByClientEmail((Email) clientEmail);
            if (orders != null) {
                for (Order order : orders) {
                    for (OrderPart orderPart : order.getOrderParts()) {
                        if (orderHistory.containsKey(orderPart.getProduct().getId()))
                            orderHistory.compute(orderPart.getProduct().getId(), (k, oldAmount) -> orderPart.getProductAmount() + oldAmount);
                        else
                            orderHistory.put(orderPart.getProduct().getId(), orderPart.getProductAmount());
                    }
                }
            }
            return orderHistory;
        }
    }

    public void deleteAllOrders() {
        orderRepository.deleteAll();
    }

    @Override
    public List findListOfOrderPartsByProductId(UUID productId) {
        return orderRepository.findByOrderPartsProductId(productId);
    }

    @Override
    public void deleteAllOrderParts() {
        orderPartRepository.deleteAll();
        deleteAllOrders();
    }

    @Transactional
    public List<Order> getOrdersWithParts(List<UUID> orderIds) {
        return orderRepository.findOrdersWithParts(orderIds);
    }

    public Map<UUID, Integer> getProductsAndAmountInOrderParts(UUID orderId) {
        Order order = orderRepository.findByIdWithOrderParts(orderId);
        Map<UUID, Integer> productsAndAmount = new HashMap<>();

        for (OrderPart orderPart : order.getOrderParts()){
            productsAndAmount.put(orderPart.getProduct().getId(), orderPart.getProductAmount());
        }
        return productsAndAmount;
    }
}
