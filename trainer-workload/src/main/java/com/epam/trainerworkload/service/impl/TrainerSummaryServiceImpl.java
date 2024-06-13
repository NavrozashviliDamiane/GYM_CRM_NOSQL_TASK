package com.epam.trainerworkload.service.impl;

import com.epam.trainerworkload.dto.TrainingSessionDTO;
import com.epam.trainerworkload.entity.TrainerSummary;
import com.epam.trainerworkload.repository.TrainerSummaryRepository;
import com.epam.trainerworkload.service.TrainerSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TrainerSummaryServiceImpl implements TrainerSummaryService {

    private final TrainerSummaryRepository repository;

    @Autowired
    public TrainerSummaryServiceImpl(TrainerSummaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void manageTrainingSession(TrainingSessionDTO dto, String transactionId) {
        if (!"ADD".equalsIgnoreCase(dto.getActionType()) && !"DELETE".equalsIgnoreCase(dto.getActionType())) {
            throw new IllegalArgumentException("Invalid action type: " + dto.getActionType());
        }

        Optional<TrainerSummary> optionalSummary = repository.findByTrainerUsername(dto.getTrainerUsername());

        if ("DELETE".equalsIgnoreCase(dto.getActionType())) {
            if (optionalSummary.isPresent()) {
                repository.delete(optionalSummary.get());
                log.info("Transaction ID: {} - Deleted training summary for trainer: {}", transactionId, dto.getTrainerUsername());
            } else {
                log.info("Transaction ID: {} - No training summary found for trainer: {}", transactionId, dto.getTrainerUsername());
            }
            return;
        }

        TrainerSummary summary;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dto.getTrainingDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        if (optionalSummary.isEmpty()) {
            summary = new TrainerSummary();
            summary.setTrainerUsername(dto.getTrainerUsername());
            summary.setFirstName(dto.getFirstName());
            summary.setLastName(dto.getLastName());
            summary.setIsActive(dto.isActive());
            summary.setYearlySummary(new HashMap<>());

            Map<Integer, Integer> monthlySummary = new HashMap<>();
            monthlySummary.put(month, dto.getTrainingDuration());
            summary.getYearlySummary().put(year, monthlySummary);

            repository.save(summary);
            log.info("Transaction ID: {} - Created new training summary for trainer: {}", transactionId, dto.getTrainerUsername());
        } else {
            summary = optionalSummary.get();
            Map<Integer, Integer> monthlySummary = summary.getYearlySummary().getOrDefault(year, new HashMap<>());
            int newDuration = monthlySummary.getOrDefault(month, 0) + dto.getTrainingDuration();
            monthlySummary.put(month, newDuration);
            summary.getYearlySummary().put(year, monthlySummary);

            repository.save(summary);
            log.info("Transaction ID: {} - Updated training summary for trainer: {}", transactionId, dto.getTrainerUsername());
        }
    }
}
