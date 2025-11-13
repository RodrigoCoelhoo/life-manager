package com.rodrigocoelhoo.lifemanager.nutrition.service;

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

@Service
public class IngredientBrandService {

    private final IngredientService ingredientService;
    private final IngredientBrandRepository ingredientBrandRepository;

    public IngredientBrandService(
            IngredientService ingredientService,
            IngredientBrandRepository ingredientBrandRepository
    ) {
        this.ingredientService = ingredientService;
        this.ingredientBrandRepository = ingredientBrandRepository;
    }

    public List<IngredientBrandModel> getAllIngredientBrands(Long ingredientId) {
        return ingredientService.getIngredient(ingredientId).getBrands();
    }

    public IngredientBrandModel getIngredientBrand(Long ingredientId, Long brandId) {
        IngredientModel ingredient = ingredientService.getIngredient(ingredientId);

        List<IngredientBrandModel> brands = ingredient.getBrands();

        return brands.stream()
                .filter(b -> b.getId().equals(brandId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFound(
                        "Ingredient brand with ID '" + brandId + "' does not belong to the ingredient with ID '" + ingredientId + "'"
                        )
                );
    }

    private void validateNutritionalData(List<NutritionalValueDTO> data) {
        List<String> invalidTypes = data.stream()
                .map(NutritionalValueDTO::type)
                .filter(type -> {
                    try {
                        NutritionalTag.valueOf(type.toUpperCase());
                        return false;
                    } catch (IllegalArgumentException exception) {
                        return true;
                    }
                })
                .toList();

        if(!invalidTypes.isEmpty()) {
            throw new BadRequestException("Invalid nutritional types: " + String.join(", ", invalidTypes));
        }
    }

    @Transactional
    public IngredientBrandModel createIngredientBrand(Long ingredientId, IngredientBrandDTO data) {
        List<NutritionalValueDTO> nutrients = data.nutritionalValues();

        validateNutritionalData(nutrients);

        IngredientModel ingredient = ingredientService.getIngredient(ingredientId);
        IngredientBrandModel ingredientBrand = IngredientBrandModel.builder()
                .ingredient(ingredient)
                .name(data.name())
                .build();

        List<NutritionalValueModel> nutritionalValueModels = nutrients.stream()
                .map(nutrient -> NutritionalValueModel.builder()
                        .ingredientBrand(ingredientBrand)
                        .tag(NutritionalTag.valueOf(nutrient.type().toUpperCase()))
                        .per100units(nutrient.per100units())
                        .build())
                .toList();
        ingredientBrand.setNutritionalValues(nutritionalValueModels);

        return ingredientBrandRepository.save(ingredientBrand);
    }

    // @Transactional -> if error -> rollback every change
    @Transactional
    public IngredientBrandModel updateIngredientBrand(Long ingredientId, Long brandId, IngredientBrandDTO data) {
        List<NutritionalValueDTO> nutrients = data.nutritionalValues();

        validateNutritionalData(nutrients);

        IngredientBrandModel ingredientBrand = getIngredientBrand(ingredientId, brandId);
        ingredientBrand.setName(data.name());
        ingredientBrand.getNutritionalValues().clear();

        nutrients.forEach(nutrient -> {
            NutritionalValueModel value = NutritionalValueModel.builder()
                    .ingredientBrand(ingredientBrand)
                    .tag(NutritionalTag.valueOf(nutrient.type().toUpperCase()))
                    .per100units(nutrient.per100units())
                    .build();

            ingredientBrand.getNutritionalValues().add(value);
        });

        return ingredientBrandRepository.save(ingredientBrand);
    }

    @Transactional
    public void deleteIngredientBrand(Long ingredientId, Long brandId) {
        IngredientBrandModel ingredientBrand = getIngredientBrand(ingredientId, brandId);
        ingredientBrandRepository.delete(ingredientBrand);
    }
}
