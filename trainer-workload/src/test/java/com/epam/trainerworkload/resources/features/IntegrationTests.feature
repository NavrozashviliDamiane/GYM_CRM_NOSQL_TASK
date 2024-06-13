Feature: Microservice Integration

  Scenario: Process training session with valid JWT
    Given a valid training session message with a valid JWT
    When the message is processed
    Then the training session should be saved successfully

  Scenario: Process training session with invalid JWT
    Given a valid training session message with an invalid JWT
    When the message is processed
    Then the training session should not be saved
