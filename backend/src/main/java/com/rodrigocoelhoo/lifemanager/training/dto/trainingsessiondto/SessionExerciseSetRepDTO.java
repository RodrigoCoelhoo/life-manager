package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

public record SessionExerciseSetRepDTO(
        Integer setNumber,
        Integer reps,
        Double weight
) implements SessionExerciseBaseDTO { }