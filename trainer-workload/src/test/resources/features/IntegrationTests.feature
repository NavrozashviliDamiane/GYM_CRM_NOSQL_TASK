Feature: Microservices Integration

  Scenario: Valid Training Session Message
    Given a valid training session message in Topic.example
    When the message is processed by the consumer
    Then it should be stored in the intermediate queue
    And it should be processed and stored by the trainer summary service

  Scenario: Invalid Training Session Message
    Given an invalid training session message in Topic.example
    When the message is processed by the consumer
    Then it should be rejected
