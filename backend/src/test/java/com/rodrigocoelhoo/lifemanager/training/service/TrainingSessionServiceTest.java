package com.rodrigocoelhoo.lifemanager.training.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.training.dto.trainingsessiondto.*;
import com.rodrigocoelhoo.lifemanager.training.mapper.SessionExerciseMapper;
import com.rodrigocoelhoo.lifemanager.training.model.*;
import com.rodrigocoelhoo.lifemanager.training.repository.TrainingSessionRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TrainingSessionServiceTest {

    @InjectMocks
    private TrainingSessionService service;

    @Mock
    private TrainingSessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExerciseService exerciseService;

    @Mock
    private SessionExerciseMapper mapper;

    @Mock
    private RedisCacheService redisCacheService;

    private UserModel user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new UserModel();
        user.setId(1L);
        when(userService.getLoggedInUser()).thenReturn(user);
        doNothing().when(redisCacheService).evictUserCache(anyString());
        doNothing().when(redisCacheService).evictUserCacheSpecific(anyString(), anyString());
    }

    @Test
    @DisplayName("should return all sessions for logged-in user")
    void shouldReturnAllSessions() {
        TrainingSessionModel session = TrainingSessionModel.builder()
                .user(user)
                .exercises(new ArrayList<>())
                .build();
        Page<TrainingSessionModel> page = new PageImpl<>(List.of(session));

        when(sessionRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

        List<TrainingSessionResponseDTO> result = service.getAllSessions(Pageable.unpaged()).toList();

        assertThat(result).hasSize(1).containsExactly(TrainingSessionResponseDTO.fromEntity(session));
        verify(sessionRepository).findAllByUser(user, Pageable.unpaged());
    }

    @Test
    @DisplayName("should throw ResourceNotFound if session does not belong to user")
    void shouldThrowIfSessionNotFound() {
        when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        ResourceNotFound ex = assertThrows(ResourceNotFound.class, () -> service.getSession(1L));
        assertThat(ex.getMessage()).isEqualTo("Session with ID '1' does not belong to the current user");

        verify(sessionRepository).findByIdAndUser(1L, user);
    }

    @Nested
    @DisplayName("createSession")
    class CreateSessionTests {

        @Test
        @DisplayName("should create session successfully")
        void shouldCreateSession() {
            TrainingSessionDTO dto = new TrainingSessionDTO(LocalDateTime.now(), new ArrayList<>());
            TrainingSessionModel saved = TrainingSessionModel.builder()
                    .user(user)
                    .date(LocalDateTime.now())
                    .build();

            when(sessionRepository.save(any())).thenReturn(saved);

            TrainingSessionModel result = service.createSession(dto);

            assertThat(result).isNotNull();
            verify(sessionRepository).save(any());
        }
    }

    @Nested
    @DisplayName("updateSession")
    class UpdateSessionTests {

        @Test
        @DisplayName("should update existing session")
        void shouldUpdateSession() {
            TrainingSessionModel existing = TrainingSessionModel.builder()
                    .user(user)
                    .exercises(new ArrayList<>())
                    .build();
            TrainingSessionDTO dto = new TrainingSessionDTO(LocalDateTime.now(), new ArrayList<>());

            when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));
            when(sessionRepository.save(existing)).thenReturn(existing);

            TrainingSessionModel result = service.updateSession(1L, dto);

            assertThat(result).isEqualTo(existing);
            verify(sessionRepository).save(existing);
        }
    }

    @Nested
    @DisplayName("deleteSession")
    class DeleteSessionTests {

        @Test
        @DisplayName("should delete session successfully")
        void shouldDeleteSession() {
            TrainingSessionModel existing = TrainingSessionModel.builder()
                    .user(user)
                    .date(LocalDateTime.now())
                    .build();
            when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(existing));

            service.deleteSession(1L);

            verify(sessionRepository).delete(existing);
        }
    }

    @Nested
    @DisplayName("getSessionDetails")
    class GetSessionDetailsTests {

        @Test
        @DisplayName("should return session details DTO")
        void shouldReturnSessionDetails() {
            TrainingSessionModel session = TrainingSessionModel.builder()
                    .user(user)
                    .exercises(List.of())
                    .build();

            when(sessionRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(session));

            SessionDetailsDTO dto = service.getSessionDetails(1L);

            assertThat(dto).isNotNull();
        }
    }
}
