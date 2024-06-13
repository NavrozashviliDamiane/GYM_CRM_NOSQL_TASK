package com.epam.trainerworkload.cucumber.steps;

import com.epam.trainerworkload.config.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IntegrationTestSteps {

    @Autowired
    private JwtService jwtService;

    private String jwtToken;
    private boolean isValid;

    @Given("a valid JWT token")
    public void aValidJwtToken() {
        jwtToken = "validToken";
    }

    @Given("an invalid JWT token")
    public void anInvalidJwtToken() {
        jwtToken = "invalidToken";
    }

    @When("the token is verified")
    public void theTokenIsVerified() {
        isValid = jwtService.validateToken(jwtToken);
    }

    @Then("the token should be valid")
    public void theTokenShouldBeValid() {
        assertTrue(isValid);
    }

    @Then("the token should be invalid")
    public void theTokenShouldBeInvalid() {
        assertFalse(isValid);
    }
}
