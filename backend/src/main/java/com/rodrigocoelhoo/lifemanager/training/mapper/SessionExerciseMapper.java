package com.rodrigocoelhoo.lifemanager.training.mapper;

import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseBaseDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseSetRepDTO;
import com.rodrigocoelhoo.lifemanager.training.dto.sessionexercisedto.SessionExerciseTimeDTO;
import com.rodrigocoelhoo.lifemanager.training.model.SessionExerciseModel;
import org.springframework.stereotype.Component;

@Component
public class SessionExerciseMapper {

    public SessionExerciseBaseDTO toDTO(SessionExerciseModel model) {
        if("SET_REP".equals(model.getExercise().getType().toString())) {
            return new SessionExerciseSetRepDTO(
                    model.getId(),
                    model.getSessionId().getId(),
                    model.getExercise().getId(),
                    model.getSetNumber(),
                    model.getReps(),
                    model.getWeight()
            );
        } else {
            return new SessionExerciseTimeDTO(
                    model.getId(),
                    model.getSessionId().getId(),
                    model.getExercise().getId(),
                    model.getDurationSecs(),
                    model.getDistance()
            );
        }
    }
}
