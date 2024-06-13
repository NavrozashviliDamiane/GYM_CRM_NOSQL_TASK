package com.epam.trainerworkload.cucumber.steps;

import com.epam.trainerworkload.TrainerWorkloadApplication;
import com.epam.trainerworkload.messaging.MessageConsumer;
import com.epam.trainerworkload.messaging.IntermediateQueueConsumer;
import com.epam.trainerworkload.service.TrainerSummaryService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TrainerWorkloadApplication.class)
public class IntegrationTestSteps {

    @Autowired
    private JmsTemplate jmsTopicTemplate;

    @Autowired
    private MessageConsumer messageConsumer;

    @MockBean
    private IntermediateQueueConsumer intermediateQueueConsumer;

    @MockBean
    private TrainerSummaryService trainerSummaryService;

    @Autowired
    private MessageConverter messageConverter;

    private TextMessage jmsMessage;

    private String message;

    @Given("a valid training session message in Topic.example")
    public void a_valid_training_session_message_in_Topic_example() {
        message = "{ \"trainerUsername\": \"trainer1\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"isActive\": true, \"trainingDate\": \"2024-06-12\", \"trainingDuration\": 60, \"actionType\": \"ADD\" }";
        jmsTopicTemplate.convertAndSend("Topic.example", message);
    }

    @Given("an invalid training session message in Topic.example")
    public void an_invalid_training_session_message_in_topic_example() {
        String invalidMessage = "{ \"trainerUsername\": \"trainer1\", \"firstName\": \"John\" }";
        jmsTopicTemplate.convertAndSend("Topic.example", invalidMessage);
    }

    @Then("it should be stored in the intermediate queue")
    public void it_should_be_stored_in_the_intermediate_queue() {
        assertNotNull(intermediateQueueConsumer);
        assertNotNull(intermediateQueueConsumer.getReceivedMessage());
    }

    @When("the message is processed by the consumer")
    public void the_message_is_processed_by_the_consumer() throws JMSException {
        TextMessage textMessage = Mockito.mock(TextMessage.class);

        Mockito.when(textMessage.getText()).thenReturn("{ \"trainerUsername\": \"trainer1\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"isActive\": true, \"trainingDate\": \"2024-06-12\", \"trainingDuration\": 60, \"actionType\": \"ADD\" }");

        messageConsumer.receiveTrainingSession(textMessage);
    }

    @Then("it should be processed and stored by the trainer summary service")
    public void it_should_be_processed_and_stored_by_the_trainer_summary_service() {
        assertNotNull(trainerSummaryService);
        assertEquals(1, trainerSummaryService.getTrainingSessions().size());
    }

    @Then("it should be rejected")
    public void it_should_be_rejected() {
        assertNull(intermediateQueueConsumer);
        assertNull(intermediateQueueConsumer.getReceivedMessage());
    }
}
