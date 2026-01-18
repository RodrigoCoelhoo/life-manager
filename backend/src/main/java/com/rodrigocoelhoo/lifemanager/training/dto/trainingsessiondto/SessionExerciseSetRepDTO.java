package com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto;

import java.io.Serializable;

public record SessionExerciseSetRepDTO(
        Integer setNumber,
        Integer reps,
        Double weight
) implements SessionExerciseBaseDTO, Serializable { }