package com.epam.trainerworkload.unit.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.Mockito.*;

public class MessageConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private MessageConsumer consumer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReceiveTrainingSession() throws Exception {
        String jsonSessionDTO = "{\"trainerUsername\":\"test\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"isActive\":true,\"trainingDate\":\"2022-06-01T12:00:00.000Z\",\"trainingDuration\":60,\"actionType\":\"ADD\"}";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(jsonSessionDTO);
        when(textMessage.getStringProperty("Authorization")).thenReturn("Bearer jwtToken");

        Message message = textMessage;

        consumer.receiveTrainingSession(message);

        verify(jmsTemplate, times(1)).convertAndSend(eq("Queue.intermediate"), eq(jsonSessionDTO), any());
    }

    @Test
    public void testReceiveTrainingSession_NonTextMessage() throws Exception {
        Message message = mock(Message.class);

        consumer.receiveTrainingSession(message);

        verify(jmsTemplate, times(0)).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    public void testReceiveTrainingSession_Exception() throws Exception {
        String jsonSessionDTO = "{\"trainerUsername\":\"test\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"isActive\":true,\"trainingDate\":\"2022-06-01T12:00:00.000Z\",\"trainingDuration\":60,\"actionType\":\"ADD\"}";

        TextMessage textMessage = mock(TextMessage.class);
        when(textMessage.getText()).thenReturn(jsonSessionDTO);

        Message message = textMessage;

        doThrow(new RuntimeException("Error")).when(jmsTemplate).convertAndSend(anyString(), anyString(), any());

        consumer.receiveTrainingSession(message);
    }
}
