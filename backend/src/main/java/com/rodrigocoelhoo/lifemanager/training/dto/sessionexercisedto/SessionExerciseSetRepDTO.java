package com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto;

public record SessionExerciseSetRepDTO(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer setNumber,
        Integer reps,
        Double weight
) implements SessionExerciseBaseDTO { }