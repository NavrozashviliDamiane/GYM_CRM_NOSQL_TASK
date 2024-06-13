package com.epam.trainerworkload.service;

import com.epam.trainerworkload.dto.TrainingSessionDTO;

public interface TrainerSummaryService {
    void manageTrainingSession(TrainingSessionDTO dto, String transactionId);
}
