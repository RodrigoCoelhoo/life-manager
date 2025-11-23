package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

public record SessionExerciseTimeDTO(
        Integer durationSecs,
        Integer distance
) implements SessionExerciseBaseDTO { }
