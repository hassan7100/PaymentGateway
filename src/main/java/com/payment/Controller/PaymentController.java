package com.payment.Controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode authenticationRequest) throws Exception {
        return paymentService.payment(authenticationRequest);
    }
    @PostMapping("/authorize")
    public ObjectNode authorize(@RequestBody ObjectNode authorizeRequest) throws Exception {
        return paymentService.authorize(authorizeRequest);
    }

}
