package com.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CommunicatorService {
    public ObjectNode authenticateCardNetwork(ObjectNode objectNode){
        WebClient webClient = WebClient.create();
        return webClient.post()
                .uri("http://cardnetwork:8080/payment")
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
    public ObjectNode authorizeCardNetwork(ObjectNode objectNode){
        WebClient webClient = WebClient.create();
        return webClient.post()
                .uri("http://cardnetwork:8080/authorize")
                .bodyValue(objectNode)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }
}
