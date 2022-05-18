package com.swa.application.integration;

import com.swa.application.domain.ShoppingCart;
import com.swa.application.dto.Order;
import com.swa.application.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Value("${event.topics.cart-created}")
    private String cartCreatedTopic;

    @Value("${event.topics.cart-updated}")
    private String cartUpdatedTopic;

    @Value("${event.topics.cart-deleted}")
    private String cartDeletedTopic;

    @Autowired
    private KafkaTemplate<String, ShoppingCart> kafkaCartTemplate;

    private static final Logger log = LoggerFactory.getLogger(CartService.class);

    public void sendCartCreatedMsg(ShoppingCart cart) {
        log.info("Publish: " + cartCreatedTopic + " with payload: " + cart);
        kafkaCartTemplate.send(cartCreatedTopic, cart);
    }

    public void sendCartUpdatedMsg(ShoppingCart cart) {
        log.info("Publish: " + cartUpdatedTopic + " with payload: " + cart);
        kafkaCartTemplate.send(cartUpdatedTopic, cart);
    }

    public void sendCartDeletedMsg(ShoppingCart cart) {
        log.info("Publish: " + cartDeletedTopic + " with payload: " + cart);
        kafkaCartTemplate.send(cartDeletedTopic, cart);
    }

}
