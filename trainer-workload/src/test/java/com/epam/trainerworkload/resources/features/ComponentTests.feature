Feature: JWT Authentication

  Scenario: Valid JWT token
    Given a valid JWT token
    When the token is verified
    Then the token should be valid

  Scenario: Invalid JWT token
    Given an invalid JWT token
    When the token is verified
    Then the token should be invalid
