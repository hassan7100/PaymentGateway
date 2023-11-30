package com.payment.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.payment.CsvWriterReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class InfoSender {
    @Autowired
    private CsvWriterReader csvWriterReader;
    @GetMapping("/getPublic")
    public ObjectNode getPublicKey(){
        return new ObjectMapper().createObjectNode()
                .put("PublicKey",csvWriterReader.readMyPublic().getEncoded());
    }
}
