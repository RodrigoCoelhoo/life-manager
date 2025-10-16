package com.rodrigocoelhoo.lifemanager.exercise.dto.sessionexercisedto;

public record SessionExerciseTimeDTO(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer durationSecs,
        Integer distance
) implements SessionExerciseBaseDTO { }
