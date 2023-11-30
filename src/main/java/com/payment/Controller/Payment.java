package com.payment.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.CommunicatorService;
import com.payment.CsvWriterReader;
import com.payment.Security.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class Payment {
    @Autowired
    RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    CsvWriterReader csvWriterReader;
    @Autowired
    CommunicatorService communicatorService;
    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode objectNode1 = rsaKeyGenerator.decryptObjectNode(objectNode.get("object").asText(),csvWriterReader.readMyPrivate());
        String encrypted = rsaKeyGenerator.encryptObjectNode(objectNode1,csvWriterReader.readPublic("cardNetwork"));
        return communicatorService.sendToNetwork(new ObjectMapper().createObjectNode().put("object",encrypted));
    }
}
