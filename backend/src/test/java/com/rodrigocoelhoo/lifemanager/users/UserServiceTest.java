package com.rodrigocoelhoo.lifemanager.users;

import com.rodrigocoelhoo.lifemanager.exceptions.DuplicateFieldException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.security.dto.SignUpDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("createUser")
    class CreateUserTests {

        @Test
        @DisplayName("should create user successfully")
        void shouldCreateUserSuccessfully() {
            SignUpDTO signUp = new SignUpDTO(
                    "johndoe",
                    "John",
                    "Doe",
                    "john@example.com",
                    "Password-123"
            );

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
            when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

            UserModel result = userService.createUser(signUp);

            assertThat(result.getUsername()).isEqualTo("johndoe");
            assertThat(result.getEmail()).isEqualTo("john@example.com");
            assertThat(result.getPassword()).isNotNull();
            verify(userRepository).save(any(UserModel.class));
        }

        @Test
        @DisplayName("should throw DuplicateFieldException if username exists")
        void shouldThrowIfUsernameExists() {
            SignUpDTO signUp = new SignUpDTO(
                    "johndoe", "John", "Doe", "john@example.com", "password123"
            );

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(new UserModel()));

            DuplicateFieldException exception = assertThrows(
                    DuplicateFieldException.class,
                    () -> userService.createUser(signUp)
            );

            assertThat(exception.getMessage()).contains("username");
        }

        @Test
        @DisplayName("should throw DuplicateFieldException if email exists")
        void shouldThrowIfEmailExists() {
            SignUpDTO signUp = new SignUpDTO(
                    "johndoe", "John", "Doe", "john@example.com", "password123"
            );

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new UserModel()));

            DuplicateFieldException exception = assertThrows(
                    DuplicateFieldException.class,
                    () -> userService.createUser(signUp)
            );

            assertThat(exception.getMessage()).contains("email");
        }
    }

    @Nested
    @DisplayName("getUser")
    class GetUserTests {

        @Test
        @DisplayName("should return user by ID")
        void shouldReturnUserById() {
            UserModel user = new UserModel();
            user.setId(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            UserModel result = userService.getUser(1L);

            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if ID not found")
        void shouldThrowIfIdNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> userService.getUser(1L)
            );

            assertThat(exception.getMessage()).contains("1");
        }

        @Test
        @DisplayName("should return user by username")
        void shouldReturnUserByUsername() {
            UserModel user = new UserModel();
            user.setUsername("johndoe");

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

            UserModel result = userService.getUser("johndoe");

            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if username not found")
        void shouldThrowIfUsernameNotFound() {
            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> userService.getUser("johndoe")
            );

            assertThat(exception.getMessage()).contains("johndoe");
        }
    }

    @Nested
    @DisplayName("getLoggedInUser")
    class GetLoggedInUserTests {

        @Mock
        SecurityContext securityContext;

        @Mock
        Authentication authentication;

        @Test
        @DisplayName("should return currently authenticated user")
        void shouldReturnLoggedInUser() {
            UserModel user = new UserModel();
            user.setUsername("johndoe");

            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("johndoe");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

            UserModel result = userService.getLoggedInUser();

            assertThat(result).isEqualTo(user);
        }


        @Test
        @DisplayName("should throw ResourceNotFound if logged-in user not found")
        void shouldThrowIfLoggedInUserNotFound() {
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("johndoe");

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            SecurityContextHolder.setContext(securityContext);

            when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> userService.getLoggedInUser()
            );

            assertThat(exception.getMessage()).isEqualTo("User with Username 'johndoe' not found");
        }

    }
}
