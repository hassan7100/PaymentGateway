package com.payment.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.payment.AtomicID;
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
public class PaymentController {
    @Autowired
    RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    CsvWriterReader csvWriterReader;
    @Autowired
    CommunicatorService communicatorService;
    @Autowired
    AtomicID atomicID;
    @Autowired
    Cache<Long,ObjectNode> cacheCards;
    @PostMapping("/payment")
    public ObjectNode payment(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode objectNode1 = rsaKeyGenerator.decryptObjectNode(objectNode.get("object").asText(),csvWriterReader.readMyPrivate());
        long id = atomicID.getID();
        objectNode1.put("ID",id);
        cacheCards.put(id, objectNode1);
        String encrypted = rsaKeyGenerator.encryptObjectNode(objectNode1,csvWriterReader.readPublic("cardNetwork"));
        objectNode1 = communicatorService.authenticateCardNetwork(new ObjectMapper().createObjectNode().put("object",encrypted));
        objectNode = rsaKeyGenerator.decryptObjectNode(objectNode1.get("object").asText(),csvWriterReader.readMyPrivate());
        encrypted = rsaKeyGenerator.encryptObjectNode(objectNode,csvWriterReader.readPublic("backend"));
        return new ObjectMapper().createObjectNode().put("object",encrypted);
    }
    @PostMapping("/authorize")
    public ObjectNode authorize(@RequestBody ObjectNode objectNode) throws Exception {
        ObjectNode objectNode1 = rsaKeyGenerator.decryptObjectNode(objectNode.get("object").asText(),csvWriterReader.readMyPrivate());
        ObjectNode objectNode2 =cacheCards.getIfPresent(objectNode1.get("ID").asLong());
        String encrypted = rsaKeyGenerator.encryptObjectNode(objectNode2,csvWriterReader.readPublic("cardNetwork"));
        objectNode1 = communicatorService.authorizeCardNetwork(new ObjectMapper().createObjectNode().put("object",encrypted));
        objectNode = rsaKeyGenerator.decryptObjectNode(objectNode1.get("object").asText(),csvWriterReader.readMyPrivate());
        encrypted = rsaKeyGenerator.encryptObjectNode(objectNode,csvWriterReader.readPublic("backend"));
        return new ObjectMapper().createObjectNode().put("object",encrypted);
    }

}
