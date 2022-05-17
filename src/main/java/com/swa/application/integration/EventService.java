package com.swa.application.integration;

import com.swa.application.domain.ShoppingCart;
import com.swa.application.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventService {

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
        log.info("Sending to Kafka topic: CART-CREATED payload: " + cart);
        kafkaCartTemplate.send(cartCreatedTopic, cart);
    }

    public void sendCartUpdatedMsg(ShoppingCart cart) {
        log.info("Sending to Kafka topic: CART-UPDATED payload: " + cart);
        kafkaCartTemplate.send(cartUpdatedTopic, cart);
    }

    public void sendCartDeletedMsg(ShoppingCart cart) {
        log.info("Sending to Kafka topic: DELETE-CART payload: " + cart);
        kafkaCartTemplate.send(cartDeletedTopic, cart);
    }

    public void sendCartCheckoutMsg(ShoppingCart cart) {
        kafkaCartTemplate.send("CHECKED-OUT", cart);
    }



}
