package com.global_solution.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.global_solution.api.model.Diploma;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("diplomasQueue")
    private String queueName;

    @Async("taskExecutor")
    public void send(Diploma diploma) {
        try {
            String message = convertToJson(diploma);
            amqpTemplate.convertAndSend(queueName, message);
            System.out.println("Mensagem enviada para fila: " + message);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao converter objeto para JSON: " + e.getMessage());
        }
    }

    private String convertToJson(Diploma diploma) throws JsonProcessingException {
        return objectMapper.writeValueAsString(diploma);
    }
}
