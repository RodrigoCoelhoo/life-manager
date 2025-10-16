package com.rodrigocoelhoo.lifemanager.exercise.dto.sessionexercisedto;

public record SessionExerciseSetRepDTO(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer setNumber,
        Integer reps,
        Double weight
) implements SessionExerciseBaseDTO { }