package org.example.employeems.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.employeems.kafka.KafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageController {

    KafkaProducer kafkaProducer;

    @GetMapping
    public ResponseEntity<String> publish(@RequestParam UUID id, @RequestParam String message) {
        kafkaProducer.sendMessage(id.toString(),message);
        return ResponseEntity.ok("Message published");
    }


}
