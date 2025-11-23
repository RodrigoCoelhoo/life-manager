package com.rodrigocoelhoo.lifemanager.training.mapper;

import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.SessionExerciseBaseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.SessionExerciseSetRepDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.SessionExerciseTimeDTO;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import org.springframework.stereotype.Component;

@Component
public class SessionExerciseMapper {

    public SessionExerciseBaseDTO toDTO(SessionExerciseModel model) {
        if("SET_REP".equals(model.getExercise().getType().toString())) {
            return new SessionExerciseSetRepDTO(
                    model.getSetNumber(),
                    model.getReps(),
                    model.getWeight()
            );
        } else {
            return new SessionExerciseTimeDTO(
                    model.getDurationSecs(),
                    model.getDistance()
            );
        }
    }
}
