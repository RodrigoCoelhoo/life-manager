package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientBrandDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.NutritionalValueDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.IngredientBrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("IngredientBrandService Tests")
class IngredientBrandServiceTest {

    @Mock
    private IngredientService ingredientService;

    @Mock
    private IngredientBrandRepository ingredientBrandRepository;

    @InjectMocks
    private IngredientBrandService ingredientBrandService;

    private IngredientModel ingredient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ingredient = IngredientModel.builder()
                .id(1L)
                .name("Egg")
                .brands(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("getAllIngredientBrands")
    class GetAllIngredientBrandsTests {

        @Test
        @DisplayName("should return all brands for the ingredient")
        void shouldReturnAllBrandsForIngredient() {
            IngredientBrandModel brand1 = IngredientBrandModel.builder()
                    .id(1L)
                    .ingredient(ingredient)
                    .name("Continente")
                    .build();

            IngredientBrandModel brand2 = IngredientBrandModel.builder()
                    .id(2L)
                    .ingredient(ingredient)
                    .name("Pingo Doce")
                    .build();

            ingredient.getBrands().addAll(List.of(brand1, brand2));

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            List<IngredientBrandModel> result = ingredientBrandService.getAllIngredientBrands(1L);

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyInAnyOrder(brand1, brand2);

            verify(ingredientService).getIngredient(1L);
        }
    }

    @Nested
    @DisplayName("getIngredientBrand")
    class GetIngredientBrandTests {

        @Test
        @DisplayName("should return brand when it belongs to ingredient")
        void shouldReturnBrandWhenBelongsToIngredient() {
            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .ingredient(ingredient)
                    .name("Continente")
                    .build();

            ingredient.getBrands().add(brand);

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            IngredientBrandModel result = ingredientBrandService.getIngredientBrand(1L, 1L);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(brand);

            verify(ingredientService).getIngredient(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFound when brand does not belong to ingredient")
        void shouldThrowWhenBrandDoesNotBelongToIngredient() {
            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
                ingredientBrandService.getIngredientBrand(1L, 2L);
            });

            assertThat(exception.getMessage()).isEqualTo(
                    "Ingredient brand with ID '2' does not belong to the ingredient with ID '1'"
            );

            verify(ingredientService).getIngredient(1L);
        }
    }

    @Nested
    @DisplayName("createIngredientBrand")
    class CreateIngredientBrandTests {

        @Test
        @DisplayName("should create brand with provided name and nutrients")
        void shouldCreateBrandWithProvidedNameAndNutrients() {
            NutritionalValueDTO n1DTO = new NutritionalValueDTO(NutritionalTag.PROTEIN, 100.0);
            IngredientBrandDTO ingredientBrandDTO = new IngredientBrandDTO(
                    "Continente", new ArrayList<>(List.of(n1DTO))
            );

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            when(ingredientBrandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            IngredientBrandModel result = ingredientBrandService.createIngredientBrand(1L, ingredientBrandDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Continente");
            assertThat(result.getIngredient()).isEqualTo(ingredient);

            assertThat(result.getNutritionalValues())
                    .extracting(NutritionalValueModel::getTag)
                    .containsExactlyInAnyOrder(NutritionalTag.PROTEIN, NutritionalTag.CALORIES);

            verify(ingredientService).getIngredient(1L);
            verify(ingredientBrandRepository).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if ingredient does not exist")
        void shouldThrowIfIngredientDoesNotExist() {
            Long ingredientId = 2L;
            IngredientBrandDTO data = new IngredientBrandDTO("BrandName", new ArrayList<>());

            when(ingredientService.getIngredient(ingredientId)).thenThrow(new ResourceNotFound("Ingredient with ID '2' does not belong to the current user"));

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> {
                ingredientBrandService.createIngredientBrand(ingredientId, data);
            });

            assertThat(exception.getMessage())
                    .isEqualTo("Ingredient with ID '2' does not belong to the current user");

            verify(ingredientService).getIngredient(ingredientId);
            verifyNoInteractions(ingredientBrandRepository);
        }
    }

    @Nested
    @DisplayName("updateIngredientBrand")
    class UpdateIngredientBrandTests {

        @Test
        @DisplayName("should update brand name and nutrients successfully")
        void shouldUpdateBrandNameAndNutrients() {
            NutritionalValueModel n1 = NutritionalValueModel.builder()
                    .tag(NutritionalTag.CALORIES)
                    .build();

            NutritionalValueModel n2 = NutritionalValueModel.builder()
                    .tag(NutritionalTag.PROTEIN)
                    .build();

            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .name("Pingo Doce")
                    .ingredient(ingredient)
                    .nutritionalValues(new ArrayList<>(List.of(n1)))
                    .build();
            ingredient.getBrands().add(brand);

            NutritionalValueDTO n1DTO = new NutritionalValueDTO(NutritionalTag.CALORIES, 100.0);
            NutritionalValueDTO n2DTO = new NutritionalValueDTO(NutritionalTag.PROTEIN, 100.0);
            IngredientBrandDTO ingredientBrandDTO = new IngredientBrandDTO("Continente", new ArrayList<>(List.of(n1DTO, n2DTO)));

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            when(ingredientBrandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            IngredientBrandModel result = ingredientBrandService.updateIngredientBrand(1L, 1L, ingredientBrandDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Continente");
            assertThat(result.getIngredient()).isEqualTo(ingredient);

            assertThat(result.getNutritionalValues())
                    .extracting(NutritionalValueModel::getTag)
                    .containsExactlyInAnyOrder(NutritionalTag.PROTEIN, NutritionalTag.CALORIES);

            verify(ingredientService).getIngredient(1L);
            verify(ingredientBrandRepository).save(any());
        }

        @Test
        @DisplayName("should add calories if missing when updating")
        void shouldAddCaloriesIfMissingOnUpdate() {
            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .name("Pingo Doce")
                    .ingredient(ingredient)
                    .nutritionalValues(new ArrayList<>(List.of()))
                    .build();
            ingredient.getBrands().add(brand);

            IngredientBrandDTO ingredientBrandDTO = new IngredientBrandDTO("Continente", new ArrayList<>(List.of()));

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            when(ingredientBrandRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
            IngredientBrandModel result = ingredientBrandService.updateIngredientBrand(1L, 1L, ingredientBrandDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Continente");
            assertThat(result.getIngredient()).isEqualTo(ingredient);

            assertThat(result.getNutritionalValues()).hasSize(1);
            assertThat(result.getNutritionalValues().getFirst().getTag()).isEqualTo(NutritionalTag.CALORIES);
            assertThat(result.getNutritionalValues().getFirst().getPer100units()).isEqualTo(0.00);

            verify(ingredientService).getIngredient(1L);
            verify(ingredientBrandRepository).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFound if brand does not exist")
        void shouldThrowIfBrandDoesNotExist() {
            Long ingredientId = 1L;
            Long nonExistentBrandId = 99L;
            IngredientBrandDTO updateData = new IngredientBrandDTO("New Brand", new ArrayList<>());

            when(ingredientService.getIngredient(ingredientId)).thenReturn(ingredient);

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () ->
                    ingredientBrandService.updateIngredientBrand(ingredientId, nonExistentBrandId, updateData)
            );

            assertThat(exception.getMessage()).isEqualTo("Ingredient brand with ID '99' does not belong to the ingredient with ID '1'");

            verify(ingredientService).getIngredient(ingredientId);
            verifyNoInteractions(ingredientBrandRepository);
        }

    }

    @Nested
    @DisplayName("deleteIngredientBrand")
    class DeleteIngredientBrandTests {

        @Test
        @DisplayName("should delete brand successfully")
        void shouldDeleteBrandSuccessfully() {
            IngredientBrandModel brand = IngredientBrandModel.builder()
                    .id(1L)
                    .name("Continente")
                    .ingredient(ingredient)
                    .nutritionalValues(new ArrayList<>())
                    .build();

            ingredient.getBrands().add(brand);

            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);
            ingredientBrandService.deleteIngredientBrand(1L, 1L);

            verify(ingredientService).getIngredient(1L);
            verify(ingredientBrandRepository).delete(brand);
        }

        @Test
        @DisplayName("should throw ResourceNotFound if brand does not exist")
        void shouldThrowIfBrandDoesNotExist() {
            when(ingredientService.getIngredient(1L)).thenReturn(ingredient);

            ResourceNotFound exception = assertThrows(ResourceNotFound.class, () ->
                    ingredientBrandService.deleteIngredientBrand(1L, 99L)
            );

            assertThat(exception.getMessage()).isEqualTo("Ingredient brand with ID '99' does not belong to the ingredient with ID '1'");

            verify(ingredientService).getIngredient(1L);
            verifyNoInteractions(ingredientBrandRepository);
        }
    }
}
