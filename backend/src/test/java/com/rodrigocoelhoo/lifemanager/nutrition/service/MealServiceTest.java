package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealIngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.*;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.MealRepository;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("MealService Tests")
class MealServiceTest {

    @Mock
    private IngredientService ingredientService;

    @Mock
    private UserService userService;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    @Mock
    private RedisCacheService redisCacheService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserModel();
        user.setId(1L);
        user.setUsername("testuser");

        when(userService.getLoggedInUser()).thenReturn(user);
        doNothing().when(redisCacheService).evictUserCache(anyString());
        doNothing().when(redisCacheService).evictUserCacheSpecific(anyString(), anyString());
    }

    @Nested
    @DisplayName("getAllMeals")
    class GetAllMealsTests {

        @Test
        @DisplayName("should return all meals for logged-in user")
        void shouldReturnAllMeals() {
            MealModel meal = MealModel.builder()
                    .user(user)
                    .ingredients(new HashSet<>())
                    .build();

            Page<MealModel> page = new PageImpl<>(List.of(meal));
            when(mealRepository.findAllByUser(user, Pageable.unpaged())).thenReturn(page);

            List<MealDetailsDTO> result = mealService.getAllMeals(Pageable.unpaged()).toList();

            assertThat(result).hasSize(1);
            assertThat(result).containsExactlyInAnyOrder(MealDetailsDTO.fromEntities(
                    meal,
                    new LinkedHashMap<>()
                    )
            );

            verify(mealRepository).findAllByUser(user, Pageable.unpaged());
        }
    }

    @Nested
    @DisplayName("getMeal")
    class GetMealTests {

        @Test
        @DisplayName("should return meal")
        void shouldReturnMealIfBelongsToUser() {
            MealModel meal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .build();

            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.ofNullable(meal));

            MealModel result = mealService.getMeal(1L);

            assertThat(result).isNotNull();

            verify(mealRepository).findByUserAndId(user, 1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if meal does not exist or belongs to another user")
        void shouldThrowIfMealNotFoundOrOtherUser() {
            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> mealService.getMeal(1L)
            );

            assertThat(exception.getMessage()).isEqualTo("Meal with ID '1' doesn't belong to the current user");
            verify(mealRepository).findByUserAndId(user, 1L);
        }
    }

    @Nested
    @DisplayName("createMeal")
    class CreateMealTests {

        @Test
        @DisplayName("should create meal successfully with valid ingredients and units")
        void shouldCreateMealSuccessfully() {
            IngredientModel ingredientModel = IngredientModel.builder()
                    .user(user)
                    .id(1L)
                    .name("Egg")
                    .brands(new HashSet<>())
                    .build();

            IngredientBrandModel ingredientBrandModel = IngredientBrandModel.builder()
                    .ingredient(ingredientModel)
                    .id(1L)
                    .build();

            ingredientModel.getBrands().add(ingredientBrandModel);

            MealIngredientDTO mealIngredientDTO = new MealIngredientDTO(1L, 1L, 50.0, "G");
            MealDTO mealDTO = new MealDTO(
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    List.of(mealIngredientDTO)
            );

            when(ingredientService.getIngredients(List.of(1L)))
                    .thenReturn(List.of(ingredientModel));

            when(mealRepository.save(any(MealModel.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            MealModel meal = mealService.createMeal(mealDTO);

            assertThat(meal).isNotNull();
            assertThat(meal.getUser()).isEqualTo(user);
            assertThat(meal.getDate()).isEqualTo(mealDTO.date());
            assertThat(meal.getIngredients()).hasSize(1);

            MealIngredientModel mealIngredient = meal.getIngredients().iterator().next();
            assertThat(mealIngredient.getIngredient()).isEqualTo(ingredientModel);
            assertThat(mealIngredient.getBrand()).isEqualTo(ingredientBrandModel);
            assertThat(mealIngredient.getAmount()).isEqualTo(50.0);
            assertThat(mealIngredient.getUnit()).isEqualTo(Unit.G);

            verify(ingredientService).getIngredients(List.of(1L));
            verify(mealRepository).save(any(MealModel.class));
        }


        @Test
        @DisplayName("should throw BadRequestException for invalid units")
        void shouldThrowForInvalidUnits() {
            MealIngredientDTO mealIngredientDTO1 = new MealIngredientDTO(1L, 1L, 100.0, "Wrong Unit 1");
            MealIngredientDTO mealIngredientDTO2 = new MealIngredientDTO(2L, 1L, 100.0, "Wrong Unit 2");
            MealDTO mealDTO = new MealDTO(
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    List.of(mealIngredientDTO1, mealIngredientDTO2)
            );

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> mealService.createMeal(mealDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Invalid unit types: Wrong Unit 1, Wrong Unit 2");

            verifyNoInteractions(ingredientService, mealRepository);
        }

        @Test
        @DisplayName("should throw BadRequestException if brand data is missing")
        void shouldThrowIfBrandDataMissing() {
            IngredientModel ingredientModel = IngredientModel.builder()
                    .user(user)
                    .id(1L)
                    .brands(new HashSet<>())
                    .build();

            MealIngredientDTO mealIngredientDTO = new MealIngredientDTO(1L, 999L, 100.0, "G");
            MealDTO mealDTO = new MealDTO(
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    List.of(mealIngredientDTO)
            );

            when(ingredientService.getIngredients(List.of(1L)))
                    .thenReturn(List.of(ingredientModel));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> mealService.createMeal(mealDTO)
            );

            assertThat(exception.getMessage()).isEqualTo("Brand with ID '999' doesn't belong to the ingredient with ID '1'");

            verify(ingredientService).getIngredients(List.of(1L));
            verifyNoInteractions(mealRepository);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if any ingredient does not belong to user")
        void shouldThrowIfIngredientNotFound() {
            MealIngredientDTO mealIngredientDTO = new MealIngredientDTO(1L, 1L, 100.0, "G");
            MealDTO mealDTO = new MealDTO(
                    LocalDateTime.of(2026, 1, 1, 0, 0),
                    List.of(mealIngredientDTO)
            );

            when(ingredientService.getIngredients(List.of(1L))).thenThrow(new ResourceNotFound("Some ingredients do not belong to the current user"));

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> mealService.createMeal(mealDTO)
            );

            assertThat(exception.getMessage()).isEqualTo("Some ingredients do not belong to the current user");

            verify(ingredientService).getIngredients(List.of(1L));
            verifyNoInteractions(mealRepository);
        }
    }

    @Nested
    @DisplayName("updateMeal")
    class UpdateMealTests {

        @Test
        @DisplayName("should update meal date and ingredients successfully")
        void shouldUpdateMealSuccessfully() {
            MealModel existingMeal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .date(LocalDateTime.of(2026, 1, 1, 0, 0))
                    .ingredients(new HashSet<>())
                    .build();

            MealIngredientDTO mealIngredientDTO = new MealIngredientDTO(1L, 1L, 50.0, "G");
            MealDTO updateDTO = new MealDTO(
                    LocalDateTime.of(2026, 1, 2, 0, 0),
                    List.of(mealIngredientDTO)
            );

            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .user(user)
                    .brands(new HashSet<>())
                    .build();

            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .ingredient(ingredient)
                    .build();

            ingredient.getBrands().add(brand);

            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existingMeal));
            when(ingredientService.getIngredients(List.of(1L))).thenReturn(List.of(ingredient));
            when(mealRepository.save(any(MealModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MealModel result = mealService.updateMeal(1L, updateDTO);

            assertThat(result).isNotNull();
            assertThat(result.getDate()).isEqualTo(updateDTO.date());
            assertThat(result.getIngredients()).hasSize(1);
            assertThat(result.getIngredients())
                    .first()
                            .satisfies(n -> {
                                assertThat(n.getIngredient()).isEqualTo(ingredient);
                                assertThat(n.getBrand()).isEqualTo(brand);
                            });

            verify(mealRepository).findByUserAndId(user, 1L);
            verify(ingredientService).getIngredients(List.of(1L));
            verify(mealRepository).save(any(MealModel.class));
        }

        @Test
        @DisplayName("should throw BadRequestException for invalid units on update")
        void shouldThrowForInvalidUnitsOnUpdate() {
            MealIngredientDTO dto1 = new MealIngredientDTO(1L, 1L, 100.0, "WrongUnit1");
            MealDTO updateDTO = new MealDTO(LocalDateTime.now(), List.of(dto1));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> mealService.updateMeal(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Invalid unit types: WrongUnit1");

            verifyNoInteractions(ingredientService, mealRepository);
        }

        @Test
        @DisplayName("should throw BadRequestException if brand data is missing on update")
        void shouldThrowIfBrandDataMissingOnUpdate() {
            MealModel existingMeal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .ingredients(new HashSet<>())
                    .build();

            MealIngredientDTO dto = new MealIngredientDTO(1L, 999L, 100.0, "G");
            MealDTO updateDTO = new MealDTO(LocalDateTime.now(), List.of(dto));

            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .user(user)
                    .brands(new HashSet<>())
                    .build();

            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existingMeal));
            when(ingredientService.getIngredients(List.of(1L))).thenReturn(List.of(ingredient));

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> mealService.updateMeal(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Brand with ID '999' doesn't belong to the ingredient with ID '1'");

            verify(mealRepository).findByUserAndId(user, 1L);
            verify(ingredientService).getIngredients(List.of(1L));
            verify(mealRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if meal does not exist")
        void shouldThrowIfMealDoesNotExist() {
            MealDTO updateDTO = new MealDTO(LocalDateTime.now(), List.of());

            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> mealService.updateMeal(1L, updateDTO)
            );

            assertThat(exception.getMessage())
                    .isEqualTo("Meal with ID '1' doesn't belong to the current user");

            verify(mealRepository).findByUserAndId(user, 1L);
            verifyNoInteractions(ingredientService);
        }
    }


    @Nested
    @DisplayName("deleteMeal")
    class DeleteMealTests {

        @Test
        @DisplayName("should delete meal successfully")
        void shouldDeleteMealSuccessfully() {
            MealModel existingMeal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .date(LocalDateTime.now())
                    .build();

            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.of(existingMeal));

            mealService.deleteMeal(1L);

            verify(mealRepository).findByUserAndId(user, 1L);
            verify(mealRepository).delete(existingMeal);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if meal does not exist")
        void shouldThrowIfMealDoesNotExist() {
            when(mealRepository.findByUserAndId(user, 1L)).thenReturn(Optional.empty());

            ResourceNotFound exception = assertThrows(
                    ResourceNotFound.class,
                    () -> mealService.deleteMeal(1L)
            );

            assertThat(exception.getMessage()).isEqualTo("Meal with ID '1' doesn't belong to the current user");

            verify(mealRepository).findByUserAndId(user, 1L);
            verify(mealRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("getNutritionalLabel")
    class GetNutritionalLabelTests {

        @Test
        @DisplayName("should return correct nutritional label for meal")
        void shouldReturnCorrectNutritionalLabel() {
            IngredientModel ingredient = IngredientModel.builder()
                    .id(1L)
                    .user(user)
                    .name("Egg")
                    .brands(new HashSet<>())
                    .build();

            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .ingredient(ingredient)
                    .nutritionalValues(new HashSet<>())
                    .build();

            brand.getNutritionalValues().addAll(List.of(
                    NutritionalValueModel.builder()
                            .tag(NutritionalTag.CALORIES)
                            .per100units(200.0)
                            .build(),
                    NutritionalValueModel.builder()
                            .tag(NutritionalTag.PROTEIN)
                            .per100units(10.0)
                            .build()
            ));
            ingredient.getBrands().add(brand);

            MealIngredientModel mealIngredient = MealIngredientModel.builder()
                    .ingredient(ingredient)
                    .brand(brand)
                    .amount(100.0)
                    .unit(Unit.G)
                    .build();

            MealModel meal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .ingredients(new HashSet<>(List.of(mealIngredient)))
                    .build();

            LinkedHashMap<NutritionalTag, Double> label = mealService.getNutritionalLabel(meal);

            assertThat(label).containsEntry(NutritionalTag.CALORIES, 200.0);
            assertThat(label).containsEntry(NutritionalTag.PROTEIN, 10.0);
        }

        @Test
        @DisplayName("should return empty label if meal has no ingredients")
        void shouldReturnEmptyLabelIfNoIngredients() {
            MealModel meal = MealModel.builder()
                    .id(1L)
                    .user(user)
                    .ingredients(new HashSet<>())
                    .build();

            LinkedHashMap<NutritionalTag, Double> label = mealService.getNutritionalLabel(meal);
            assertThat(label).isEmpty();
        }
    }
}
