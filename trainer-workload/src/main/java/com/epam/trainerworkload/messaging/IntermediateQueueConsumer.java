package com.epam.trainerworkload.messaging;

import com.epam.trainerworkload.dto.TrainingSessionDTO;
import com.epam.trainerworkload.service.TrainerSummaryService;
import com.epam.trainerworkload.config.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IntermediateQueueConsumer {

    private final ObjectMapper objectMapper;
    private final TrainerSummaryService trainerSummaryService;
    private final JwtService jwtService;

    @Value("${jwt.token.header}")
    private String jwtTokenHeader;

    public IntermediateQueueConsumer(ObjectMapper objectMapper, TrainerSummaryService trainerSummaryService, JwtService jwtService) {
        this.objectMapper = objectMapper;
        this.trainerSummaryService = trainerSummaryService;
        this.jwtService = jwtService;
    }

    @JmsListener(destination = "Queue.intermediate", containerFactory = "queueListenerFactory")
    public void processTrainingSession(Message message) {
        try {
            if (message instanceof TextMessage) {
                String jsonSessionDTO = ((TextMessage) message).getText();
                TrainingSessionDTO sessionDTO = objectMapper.readValue(jsonSessionDTO, TrainingSessionDTO.class);
                log.info("Processing training session: {}", sessionDTO);

                String jwtToken = message.getStringProperty("Authorization");
                log.info("Received JWT token: {}", jwtToken);

                if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                    jwtToken = jwtToken.substring(7);
                }

                if (jwtService.validateToken(jwtToken)) {
                    log.info("JWT token is valid");
                    trainerSummaryService.manageTrainingSession(sessionDTO, jwtToken);
                } else {
                    log.warn("Invalid JWT token: {}", jwtToken);
                }
            } else {
                log.warn("Received message is not a TextMessage. Skipping processing.");
            }
        } catch (Exception e) {
            log.error("Error occurred while processing the received message: {}", e.getMessage());
        }
    }
}
