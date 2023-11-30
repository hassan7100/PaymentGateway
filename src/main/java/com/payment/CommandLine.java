package com.payment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.Security.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.KeyPair;

@Component
public class CommandLine implements CommandLineRunner {
    @Autowired
    private RSAKeyGenerator rsaKeyGenerator;
    @Autowired
    private CsvWriterReader csvWriterReader;
    @Override
    public void run(String... args) throws Exception {
        KeyPair keyPair= rsaKeyGenerator.generateKeyPair();
        csvWriterReader.writeKeys(keyPair);
        Thread.sleep(30000);
        WebClient webClient = WebClient.create();
        ObjectNode objectNode = webClient.get()
                .uri("http://backend:8080/getPublic")
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        ObjectNode objectNode1 = webClient.get()
                .uri("http://cardNetwork:8080/getPublic")
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
        csvWriterReader.writePublicKey("backend", objectNode.get("PublicKey").asText());
        csvWriterReader.writePublicKey("cardNetwork", objectNode1.get("PublicKey").asText());
    }
}
