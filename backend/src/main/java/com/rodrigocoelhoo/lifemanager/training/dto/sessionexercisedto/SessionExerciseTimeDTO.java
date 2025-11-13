package com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto;

public record SessionExerciseTimeDTO(
        Long id,
        Long sessionId,
        Long exerciseId,
        Integer durationSecs,
        Integer distance
) implements SessionExerciseBaseDTO { }
