package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import java.io.Serializable;

public record SessionExerciseTimeDTO(
        Integer durationSecs,
        Integer distance
) implements SessionExerciseBaseDTO, Serializable { }
