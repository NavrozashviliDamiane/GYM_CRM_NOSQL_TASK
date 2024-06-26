package com.epam.crmgym.messaging.activemq;

import com.epam.crmgym.dto.client.TrainingSessionDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public MessageProducer(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessageToTopicWithJwt(String jwtToken, TrainingSessionDTO sessionDTO) {
        try {
            String jsonSessionDTO = objectMapper.writeValueAsString(sessionDTO);

            jmsTemplate.setSessionTransacted(true);
            jmsTemplate.convertAndSend("Topic.example", jsonSessionDTO, message -> {
                message.setStringProperty("Authorization", "Bearer " + jwtToken);
                return message;
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
