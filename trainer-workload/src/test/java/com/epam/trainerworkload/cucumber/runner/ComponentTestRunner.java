package com.epam.trainerworkload.cucumber.runner;

import com.epam.trainerworkload.TrainerWorkloadApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadApplication.class)
public class ComponentTestRunner {
}
