package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientBrandDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.NutritionalValueDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalValueModel;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.IngredientBrandRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IngredientBrandService {

    private final IngredientService ingredientService;
    private final IngredientBrandRepository ingredientBrandRepository;
    private final RedisCacheService redisCacheService;

    public IngredientBrandService(
            IngredientService ingredientService,
            IngredientBrandRepository ingredientBrandRepository,
            RedisCacheService redisCacheService
    ) {
        this.ingredientService = ingredientService;
        this.ingredientBrandRepository = ingredientBrandRepository;
        this.redisCacheService = redisCacheService;
    }

    public Set<IngredientBrandModel> getAllIngredientBrands(Long ingredientId) {
        return ingredientService.getIngredient(ingredientId).getBrands();
    }

    public IngredientBrandModel getIngredientBrand(Long ingredientId, Long brandId) {
        IngredientModel ingredient = ingredientService.getIngredient(ingredientId);

        Set<IngredientBrandModel> brands = ingredient.getBrands();

        return brands.stream()
                .filter(b -> b.getId().equals(brandId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound(
                        "Ingredient brand with ID '" + brandId + "' does not belong to the ingredient with ID '" + ingredientId + "'"
                        )
                );
    }

    @Transactional
    public IngredientBrandModel createIngredientBrand(Long ingredientId, IngredientBrandDTO data) {
        List<NutritionalValueDTO> nutrients = data.nutritionalValues();

        boolean hasCalories = nutrients.stream()
                .anyMatch(n -> n.type().equals(NutritionalTag.CALORIES));

        if (!hasCalories) {
            NutritionalValueDTO calories = new NutritionalValueDTO(NutritionalTag.CALORIES, 0.0);
            nutrients.add(calories);
        }

        IngredientModel ingredient = ingredientService.getIngredient(ingredientId);
        IngredientBrandModel ingredientBrand = IngredientBrandModel.builder()
                .ingredient(ingredient)
                .name(data.name())
                .build();

        Set<NutritionalValueModel> nutritionalValueModels = nutrients.stream()
                .map(nutrient -> NutritionalValueModel.builder()
                        .ingredientBrand(ingredientBrand)
                        .tag(nutrient.type())
                        .per100units(nutrient.per100units())
                        .build())
                .collect(Collectors.toSet());
        ingredientBrand.setNutritionalValues(nutritionalValueModels);

        IngredientBrandModel saved = ingredientBrandRepository.save(ingredientBrand);
        redisCacheService.evictUserCache("ingredients");
        return saved;
    }

    @Transactional
    public IngredientBrandModel updateIngredientBrand(Long ingredientId, Long brandId, IngredientBrandDTO data) {
        List<NutritionalValueDTO> nutrients = data.nutritionalValues();

        boolean hasCalories = nutrients.stream()
                .anyMatch(n -> n.type().equals(NutritionalTag.CALORIES));

        if (!hasCalories) {
            NutritionalValueDTO calories = new NutritionalValueDTO(NutritionalTag.CALORIES, 0.0);
            nutrients.add(calories);
        }

        IngredientBrandModel ingredientBrand = getIngredientBrand(ingredientId, brandId);
        ingredientBrand.setName(data.name());
        ingredientBrand.getNutritionalValues().clear();

        nutrients.forEach(nutrient -> {
            NutritionalValueModel value = NutritionalValueModel.builder()
                    .ingredientBrand(ingredientBrand)
                    .tag(nutrient.type())
                    .per100units(nutrient.per100units())
                    .build();

            ingredientBrand.getNutritionalValues().add(value);
        });

        IngredientBrandModel saved = ingredientBrandRepository.save(ingredientBrand);
        redisCacheService.evictUserCache("ingredients");
        redisCacheService.evictUserCache("recipes");
        redisCacheService.evictUserCache("meals");
        redisCacheService.evictUserCache("nutritionDashboard");
        return saved;
    }

    @Transactional
    public void deleteIngredientBrand(Long ingredientId, Long brandId) {
        IngredientBrandModel ingredientBrand = getIngredientBrand(ingredientId, brandId);
        ingredientBrandRepository.delete(ingredientBrand);

        redisCacheService.evictUserCache("ingredients");
        redisCacheService.evictUserCache("recipes");
        redisCacheService.evictUserCache("meals");
        redisCacheService.evictUserCache("nutritionDashboard");
    }
}
