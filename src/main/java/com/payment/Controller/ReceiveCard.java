package com.payment.Controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReceiveCard {
    @PostMapping("/receiveCard")
    public ObjectNode receiveCard(@RequestBody ObjectNode cardRequest){
        System.out.println(cardRequest);
        return cardRequest;
    }
}
