package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeIngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.*;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.RecipeRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

@DisplayName("RecipeService Tests")
class RecipeServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private RecipeService recipeService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.getLoggedInUser()).thenReturn(user);
    }

    @Nested
    @DisplayName("getAllRecipes")
    class GetAllRecipesTests {

        @Test
        @DisplayName("should return all recipes when name is null")
        void shouldReturnAllRecipesWhenNameIsNull() {
            RecipeModel omelette = RecipeModel.builder()
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omelette, omeletteAndBacon));
            when(recipeRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAllRecipes(Pageable.unpaged(), null);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omelette, omeletteAndBacon);
            verify(recipeRepository).findAllByUser(user, Pageable.unpaged());
            verify(recipeRepository, never()).findByUserAndNameContainingIgnoreCase(any(), any(), any());
        }

        @Test
        @DisplayName("should return all recipes when name is blank")
        void shouldReturnAllRecipesWhenNameIsBlank() {
            RecipeModel omelette = RecipeModel.builder()
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omelette, omeletteAndBacon));
            when(recipeRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAllRecipes(Pageable.unpaged(), "");

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omelette, omeletteAndBacon);
            verify(recipeRepository).findAllByUser(user, Pageable.unpaged());
            verify(recipeRepository, never()).findByUserAndNameContainingIgnoreCase(any(), any(), any());
        }

        @Test
        @DisplayName("should return recipes filtered by name ignoring case")
        void shouldReturnRecipesFilteredByName() {
            RecipeModel omelette = RecipeModel.builder()
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omeletteAndBacon));
            when(recipeRepository.findByUserAndNameContainingIgnoreCase(user, "Bacon", Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAllRecipes(Pageable.unpaged(), "Bacon");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omeletteAndBacon);
            verify(recipeRepository, never()).findAllByUser(any(), any());
            verify(recipeRepository).findByUserAndNameContainingIgnoreCase(user, "Bacon", Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getAvailableRecipes")
    class GetAvailableRecipesTests {

        @Test
        @DisplayName("should return all recipes if ingredientIds is null")
        void shouldReturnAllRecipesIfIngredientIdsNull() {
            RecipeModel omelette = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .id(2L)
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omelette, omeletteAndBacon));
            when(recipeRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAvailableRecipes(null, Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omelette, omeletteAndBacon);
            verify(recipeRepository).findAllByUser(user, Pageable.unpaged());
            verify(recipeRepository, never()).findAvailableRecipes(any(), any(), any());
        }

        @Test
        @DisplayName("should return all recipes if ingredientIds is empty")
        void shouldReturnAllRecipesIfIngredientIdsEmpty() {
            RecipeModel omelette = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .id(2L)
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omelette, omeletteAndBacon));
            when(recipeRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAvailableRecipes(List.of(), Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omelette, omeletteAndBacon);
            verify(recipeRepository).findAllByUser(user, Pageable.unpaged());
            verify(recipeRepository, never()).findAvailableRecipes(any(), any(), any());
        }

        @Test
        @DisplayName("should return recipes containing all specified ingredients")
        void shouldReturnRecipesWithAllIngredients() {
            // Ingredients
            // 1L - Eggs    | Omelette, Omelette And bacon
            // 2L - Cheese  | Omelette, Omelette And bacon
            // 3L - Bacon   | Omelette And bacon

            RecipeModel omelette = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .id(2L)
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of(omelette));
            when(recipeRepository.findAvailableRecipes(user, List.of(1L, 2L), Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAvailableRecipes(List.of(1L, 2L), Pageable.unpaged());

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent()).containsExactlyInAnyOrder(omelette);
            verify(recipeRepository, never()).findAllByUser(any(), any());
            verify(recipeRepository).findAvailableRecipes(user, List.of(1L, 2L), Pageable.unpaged());
        }

        @Test
        @DisplayName("should return empty list if no recipes match ingredients")
        void shouldReturnEmptyIfNoRecipesMatchIngredients() {
            // Ingredients
            // 1L - Eggs    | Omelette, Omelette And bacon
            // 2L - Cheese  | Omelette, Omelette And bacon
            // 3L - Bacon   | Omelette And bacon

            RecipeModel omelette = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            RecipeModel omeletteAndBacon = RecipeModel.builder()
                    .id(2L)
                    .user(user)
                    .name("Omelette And Bacon")
                    .build();

            Page<RecipeModel> page = new PageImpl<>(List.of());
            when(recipeRepository.findAvailableRecipes(user, List.of(1L), Pageable.unpaged())).thenReturn(page);

            Page<RecipeModel> result = recipeService.getAvailableRecipes(List.of(1L), Pageable.unpaged());

            assertThat(result.getContent()).isEmpty();
            verify(recipeRepository, never()).findAllByUser(any(), any());
            verify(recipeRepository).findAvailableRecipes(user, List.of(1L), Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getRecipe")
    class GetRecipeTests {

        @Test
        @DisplayName("should return recipe if it belongs to logged-in user")
        void shouldReturnRecipeIfBelongsToUser() {
            RecipeModel recipe = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            when(recipeRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.of(recipe));

            RecipeModel result = recipeService.getRecipe(1L);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(recipe);

            verify(recipeRepository).findByUserAndId(user, 1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if recipe does not exist or belongs to another user")
        void shouldThrowIfRecipeNotFoundOrOtherUser() {
            when(recipeRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> recipeService.getRecipe(1L)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Recipe with ID '1' doesn't belong to the current user.");

            verify(recipeRepository).findByUserAndId(user, 1L);
        }
    }

    @Nested
    @DisplayName("createRecipe")
    class CreateRecipeTests {

        @Test
        @DisplayName("should create recipe with valid ingredients and units")
        void shouldCreateRecipeSuccessfully() {
            RecipeDTO recipeDTO = new RecipeDTO("Omelette", List.of());
            RecipeModel recipe = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            when(ingredientService.getIngredients(List.of()))
                    .thenReturn(List.of());

            when(recipeRepository.save(any(RecipeModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            RecipeModel result = recipeService.createRecipe(recipeDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Omelette");
            assertThat(result.getUser()).isEqualTo(user);
            assertThat(result.getIngredients()).isEmpty();

            verify(ingredientService).getIngredients(List.of());
            verify(recipeRepository).save(any(RecipeModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid units")
        void shouldThrowForInvalidUnits() {
            RecipeIngredientDTO ingredientDTO1 = new RecipeIngredientDTO(1L, 100.0, "Wrong Unit 1");
            RecipeIngredientDTO ingredientDTO2 = new RecipeIngredientDTO(1L, 100.0, "Wrong Unit 2");
            RecipeDTO recipeDTO = new RecipeDTO("Omelette", List.of(ingredientDTO1, ingredientDTO2));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> recipeService.createRecipe(recipeDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Invalid unit types: Wrong Unit 1, Wrong Unit 2");

            verifyNoInteractions(ingredientService, recipeRepository);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if any ingredient does not belong to user")
        void shouldThrowResourceNotFoundForMissingIngredients() {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO(1L, 100.0, "G");
            RecipeDTO recipeDTO = new RecipeDTO("Omelette", List.of(ingredientDTO));

            when(ingredientService.getIngredients(List.of(1L)))
                    .thenThrow(new ResourceNotFound("Some ingredients do not belong to the current user"));

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> recipeService.createRecipe(recipeDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Some ingredients do not belong to the current user");

            verify(ingredientService).getIngredients(List.of(1L));
            verify(recipeRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("updateRecipe")
    class UpdateRecipeTests {

        @Test
        @DisplayName("should update recipe name and ingredients successfully")
        void shouldUpdateRecipeSuccessfully() {
            RecipeModel existingRecipe = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .ingredients(new ArrayList<>())
                    .build();

            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO(1L, 2.0, "G");
            RecipeDTO updateDTO = new RecipeDTO("Omelette V2", List.of(ingredientDTO));

            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Egg")
                    .build();

            when(recipeRepository.findByUserAndId(user,1L)).thenReturn(Optional.ofNullable(existingRecipe));
            when(ingredientService.getIngredients(List.of(1L))).thenReturn(List.of(ingredient));
            when(recipeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            RecipeModel result = recipeService.updateRecipe(1L, updateDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Omelette V2");
            assertThat(result.getIngredients()).hasSize(1);
            assertThat(result.getIngredients().getFirst().getIngredient()).isEqualTo(ingredient);

            verify(ingredientService).getIngredients(List.of(1L));
            verify(recipeRepository).save(any());
        }


        @Test
        @DisplayName("should throw BadRequestException for invalid units")
        void shouldThrowForInvalidUnitsOnUpdate() {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO(1L, 2.0, "WRONG_UNIT");
            RecipeDTO updateDTO = new RecipeDTO("Omelette V2", List.of(ingredientDTO));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> recipeService.updateRecipe(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Invalid unit types: WRONG_UNIT");

            verify(recipeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if ingredient data is missing")
        void shouldThrowIfIngredientDataMissingOnUpdate() {
            RecipeIngredientDTO ingredientDTO = new RecipeIngredientDTO(1L, 2.0, "G");
            RecipeDTO updateDTO = new RecipeDTO("Omelette V2", List.of(ingredientDTO));

            RecipeModel existingRecipe = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .ingredients(new ArrayList<>())
                    .build();

            when(recipeRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.of(existingRecipe));

            // **Mock the exception to be thrown by the ingredient service**
            when(ingredientService.getIngredients(List.of(1L)))
                    .thenThrow(new ResourceNotFound("Some ingredients do not belong to the current user"));

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> recipeService.updateRecipe(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Some ingredients do not belong to the current user");

            verify(ingredientService).getIngredients(List.of(1L));
            verify(recipeRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if recipe does not exist")
        void shouldThrowIfRecipeDoesNotExist() {
            RecipeDTO updateDTO = new RecipeDTO("Omelette Deluxe", List.of());

            when(recipeRepository.findByUserAndId(user, 1L))
                    .thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> recipeService.updateRecipe(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Recipe with ID '1' doesn't belong to the current user.");

            verify(recipeRepository).findByUserAndId(user, 1L);
            verify(ingredientService, never()).getIngredients(any());
            verify(recipeRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("deleteRecipe")
    class DeleteRecipeTests {

        @Test
        @DisplayName("should delete recipe successfully")
        void shouldDeleteRecipeSuccessfully() {
            RecipeModel existingRecipe = RecipeModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Omelette")
                    .build();

            when(recipeRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existingRecipe));

            recipeService.deleteRecipe(1L);

            verify(recipeRepository).findByUserAndId(user, 1L);
            verify(recipeRepository).delete(existingRecipe);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if recipe does not exist")
        void shouldThrowIfRecipeDoesNotExist() {
            when(recipeRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> recipeService.deleteRecipe(1L)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Recipe with ID '1' doesn't belong to the current user.");

            verify(recipeRepository).findByUserAndId(user, 1L);
            verify(recipeRepository, never()).delete(any());
        }
    }
}
