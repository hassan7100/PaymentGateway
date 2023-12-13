package com.payment.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.payment.AtomicID;
import com.payment.CommunicatorService;
import com.payment.CsvWriterReader;
import com.payment.Security.RSAKeyGenerator;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;

@Service
public class PaymentService {

    private final RSAKeyGenerator rsaKeyGenerator;
    private final CsvWriterReader csvWriterReader;
    private final CommunicatorService communicatorService;
    private final AtomicID atomicID;
    private final Cache<Long, ObjectNode> cacheCards;
    private final ObjectMapper objectMapper;

    public PaymentService(RSAKeyGenerator rsaKeyGenerator,
                          CsvWriterReader csvWriterReader,
                          CommunicatorService communicatorService,
                          AtomicID atomicID,
                          Cache<Long, ObjectNode> cacheCards, ObjectMapper objectMapper) {
        this.rsaKeyGenerator = rsaKeyGenerator;
        this.csvWriterReader = csvWriterReader;
        this.communicatorService = communicatorService;
        this.atomicID = atomicID;
        this.cacheCards = cacheCards;
        this.objectMapper = objectMapper;
    }

    public ObjectNode payment( ObjectNode authRequest) throws Exception {
        ObjectNode message=objectMapper.createObjectNode();
        PrivateKey privateKey = csvWriterReader.readMyPrivate();

        ObjectNode decryptedAuthRequest = rsaKeyGenerator.decryptObjectNode(authRequest.get("object").asText(), privateKey);
        long id = atomicID.getID();
        decryptedAuthRequest.put("ID",id);
        cacheCards.put(id, decryptedAuthRequest);
        String encryptedAuthRequest = rsaKeyGenerator.encryptObjectNode(decryptedAuthRequest,csvWriterReader.readPublic("cardNetwork"));
        ObjectNode encryptedRequest = objectMapper.createObjectNode();
        encryptedRequest.put("object",encryptedAuthRequest);
        ObjectNode cardNetworkResponse = communicatorService.authenticateCardNetwork(encryptedRequest);
        ObjectNode decryptedCardResponse= rsaKeyGenerator.decryptObjectNode(cardNetworkResponse.get("object").asText(),privateKey);
        encryptedAuthRequest = rsaKeyGenerator.encryptObjectNode(decryptedCardResponse,csvWriterReader.readPublic("backend"));

        message.put("object",encryptedAuthRequest);
        return message;
    }
    public ObjectNode authorize( ObjectNode authorizeReq) throws Exception {
        ObjectNode message=objectMapper.createObjectNode();
        PrivateKey privateKey=csvWriterReader.readMyPrivate();
        ObjectNode decryptedAuthorize = rsaKeyGenerator.decryptObjectNode(authorizeReq.get("object").asText(),privateKey);
        ObjectNode cacheRecord =cacheCards.getIfPresent(decryptedAuthorize.get("ID").asLong());
        String encryptedCacheRecord = rsaKeyGenerator.
                encryptObjectNode(cacheRecord,csvWriterReader.readPublic("cardNetwork"));
        ObjectNode encryptedNetworkResponse = communicatorService.
                authorizeCardNetwork(objectMapper.createObjectNode().put("object",encryptedCacheRecord));
        ObjectNode decryptedNetworkResponse = rsaKeyGenerator.decryptObjectNode(encryptedNetworkResponse.get("object").asText(),privateKey);
        String backendEncrypt = rsaKeyGenerator.encryptObjectNode(decryptedNetworkResponse,csvWriterReader.readPublic("backend"));
        message.put("object",backendEncrypt);
        return message;
    }
}
