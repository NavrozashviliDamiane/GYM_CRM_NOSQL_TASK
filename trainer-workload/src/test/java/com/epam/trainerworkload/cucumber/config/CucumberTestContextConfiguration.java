package com.epam.trainerworkload.cucumber.config;


import com.epam.trainerworkload.TrainerWorkloadApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadApplication.class)
public class CucumberTestContextConfiguration {
}

