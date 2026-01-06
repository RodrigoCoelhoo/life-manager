package com.rodrigocoelhoo.lifemanager.nutrition.service;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.IngredientRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IngredientService ingredientService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("RodrigoCoelho");

        when(userService.getLoggedInUser()).thenReturn(user);
    }

    @Nested
    @DisplayName("getAllIngredients")
    class GetAllIngredientsTests {

        @Test
        @DisplayName("Should return all ingredients when name is null")
        void shouldReturnAllIngredientsWhenNameIsNull() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            IngredientModel ingredient2 = IngredientModel.builder()
                    .id(2L)
                    .name("Cheese")
                    .user(user)
                    .build();

            Page<IngredientModel> page = new PageImpl<>(List.of(ingredient1, ingredient2));

            when(ingredientRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<IngredientModel> result = ingredientService.getAllIngredients(Pageable.unpaged(), null);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(ingredient1, ingredient2);
        }

        @Test
        @DisplayName("should return all ingredients when name is blank")
        void shouldReturnAllIngredientsWhenNameIsBlank() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            IngredientModel ingredient2 = IngredientModel.builder()
                    .id(2L)
                    .name("Cheese")
                    .user(user)
                    .build();

            Page<IngredientModel> page = new PageImpl<>(List.of(ingredient1, ingredient2));

            when(ingredientRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<IngredientModel> result = ingredientService.getAllIngredients(Pageable.unpaged(), "");

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(ingredient1, ingredient2);
        }

        @Test
        @DisplayName("Should return ingredients filtered by name ignoring case")
        void shouldReturnIngredientsFilteredByName() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            IngredientModel ingredient2 = IngredientModel.builder()
                    .id(2L)
                    .name("Cheese")
                    .user(user)
                    .build();

            Page<IngredientModel> page = new PageImpl<>(List.of(ingredient1));

            when(ingredientRepository.findByUserAndNameContainingIgnoreCase(user, "eg", Pageable.unpaged())).thenReturn(page);

            Page<IngredientModel> result = ingredientService.getAllIngredients(Pageable.unpaged(), "eg");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent()).containsExactlyInAnyOrder(ingredient1);
        }

        @Test
        @DisplayName("Should only fetch ingredients for the logged-in user")
        void shouldNotReturnIngredientsOfOtherUsers() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            IngredientModel ingredient2 = IngredientModel.builder()
                    .id(2L)
                    .name("Cheese")
                    .user(user)
                    .build();

            Page<IngredientModel> page = new PageImpl<>(List.of(ingredient1, ingredient2));

            when(ingredientRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<IngredientModel> result = ingredientService.getAllIngredients(Pageable.unpaged(), null);

            verify(ingredientRepository).findAllByUser(user, Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(ingredient1, ingredient2);
        }
    }

    @Nested
    @DisplayName("getIngredient")
    class GetIngredientTests {

        @Test
        @DisplayName("should return ingredient when it belongs to logged-in user")
        void shouldReturnIngredientWhenBelongsToUser() {
            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            when(ingredientRepository.findByUserAndId(user, 1L)).thenReturn(Optional.ofNullable(ingredient));
            IngredientModel result = ingredientService.getIngredient(1L);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(ingredient);
        }

        @Test
        @DisplayName("Should throw ResourceNotFound when ingredient does not exist or belongs to another user")
        void shouldThrowWhenIngredientNotFoundOrBelongsToAnotherUser() {
            when(ingredientRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
                ingredientService.getIngredient(1L);
            });

            assertThat(exception.getMessage()).isEqualTo("Ingredient with ID '1' does not belong to the current user");
            verify(ingredientRepository).findByUserAndId(user, 1L);
        }
    }

    @Nested
    @DisplayName("createIngredient")
    class CreateIngredientTests {

        @Test
        @DisplayName("should create ingredient with correct user and empty brands list")
        void shouldCreateIngredientWithCorrectUserAndEmptyBrands() {
            IngredientDTO data = new IngredientDTO("Egg");

            IngredientModel savedIngredient = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .brands(new ArrayList<>())
                    .build();

            when(ingredientRepository.save(any(IngredientModel.class))).thenReturn(savedIngredient);

            IngredientModel result = ingredientService.createIngredient(data);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Egg");
            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getBrands()).isEmpty();

            verify(ingredientRepository).save(argThat(ingredient ->
                    ingredient.getName().equals("Egg") &&
                            ingredient.getUser().equals(user) &&
                            ingredient.getBrands() != null &&
                            ingredient.getBrands().isEmpty()
            ));
        }
    }

    @Nested
    @DisplayName("updateIngredient")
    class UpdateIngredientTests {

        @Test
        @DisplayName("Should update ingredient name successfully")
        void shouldUpdateIngredientNameSuccessfully() {
            Long ingredientId = 1L;
            IngredientDTO updateData = new IngredientDTO("Updated Egg");

            IngredientModel existingIngredient = IngredientModel.builder()
                    .id(ingredientId)
                    .name("Egg")
                    .user(user)
                    .brands(new ArrayList<>())
                    .build();

            when(ingredientRepository.findByUserAndId(user, ingredientId)).thenReturn(Optional.of(existingIngredient));
            when(ingredientRepository.save(existingIngredient)).thenReturn(existingIngredient);

            IngredientModel result = ingredientService.updateIngredient(ingredientId, updateData);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ingredientId);
            assertThat(result.getName()).isEqualTo("Updated Egg");
            assertThat(result.getUser()).isEqualTo(user);

            verify(ingredientRepository).save(existingIngredient);
        }
    }

    @Nested
    @DisplayName("deleteIngredient")
    class DeleteIngredientTests {

        @Test
        @DisplayName("should delete ingredient successfully")
        void shouldDeleteIngredientSuccessfully() {
            IngredientModel existingIngredient = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .brands(new ArrayList<>())
                    .build();

            when(ingredientRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existingIngredient));

            ingredientService.deleteIngredient(1L);
            verify(ingredientRepository).delete(existingIngredient);
        }
    }

    @Nested
    @DisplayName("getIngredientsById")
    class GetIngredientsTests {

        @Test
        @DisplayName("should return all ingredients by IDs")
        void shouldReturnAllIngredientsByIds() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            IngredientModel ingredient2 = IngredientModel.builder()
                    .id(2L)
                    .name("Cheese")
                    .user(user)
                    .build();

            when(ingredientRepository.findAllByUserAndIdIn(user, List.of(1L, 2L))).thenReturn(List.of(ingredient1, ingredient2));
            List<IngredientModel> result = ingredientService.getIngredients(List.of(1L, 2L));

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(ingredient1, ingredient2);
            verify(ingredientRepository).findAllByUserAndIdIn(user, List.of(1L, 2L));
        }

        @Test
        @DisplayName("should return empty list when IDs are null")
        void shouldReturnEmptyListWhenIdsAreNull() {
            when(ingredientRepository.findAllByUserAndIdIn(user, List.of())).thenReturn(List.of());
            List<IngredientModel> result = ingredientService.getIngredients(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when IDs are empty")
        void shouldReturnEmptyListWhenIdsAreEmpty() {
            when(ingredientRepository.findAllByUserAndIdIn(user, null)).thenReturn(List.of());
            List<IngredientModel> result = ingredientService.getIngredients(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should throw ResourceNotFound when some IDs do not belong to user")
        void shouldThrowWhenSomeIdsDoNotBelongToUser() {
            IngredientModel ingredient1 = IngredientModel.builder()
                    .id(1L)
                    .name("Egg")
                    .user(user)
                    .build();

            when(ingredientRepository.findAllByUserAndIdIn(user, List.of(1L, 2L))).thenReturn(List.of(ingredient1));

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
                ingredientService.getIngredients(List.of(1L, 2L));
            });

            assertThat(exception.getMessage())
                    .isEqualTo("Some ingredients do not belong to the current user");

            verify(ingredientRepository).findAllByUserAndIdIn(user, List.of(1L, 2L));
        }
    }
}
