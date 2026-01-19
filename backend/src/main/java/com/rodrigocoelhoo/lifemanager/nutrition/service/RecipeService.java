package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeIngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.*;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.RecipeRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final UserService userService;
    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "recipes";

    public RecipeService(
            UserService userService,
            RecipeRepository recipeRepository,
            IngredientService ingredientService,
            RedisCacheService redisCacheService
    ) {
        this.userService = userService;
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<RecipeDetailsDTO> getAllRecipes(Pageable pageable, String name) {
        UserModel user = userService.getLoggedInUser();
        Page<RecipeModel> page;
        if(name == null || name.isBlank())
            page = recipeRepository.findAllByUser(user, pageable);
        else
            page = recipeRepository.findByUserAndNameContainingIgnoreCase(user, name, pageable);

        return page.map(RecipeDetailsDTO::fromEntity);
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<RecipeDetailsDTO> getAvailableRecipes(
            List<Long> ingredientIds,
            Pageable pageable
    ) {
        UserModel user = userService.getLoggedInUser();
        Page<RecipeModel> page;
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            page = recipeRepository.findAllByUser(user, pageable);
        } else {
            page = recipeRepository.findAvailableRecipes(user, ingredientIds, pageable);
        }

        return page.map(RecipeDetailsDTO::fromEntity);
    }


    public RecipeModel getRecipe(Long id) {
        UserModel user = userService.getLoggedInUser();
        return recipeRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound("Recipe with ID '"+ id + "' doesn't belong to the current user."));
    }

    private void validateUnits(RecipeDTO data) {
        List<String> invalidUnits = data.ingredients().stream()
                .map(RecipeIngredientDTO::unit)
                .filter(unit -> {
                    try {
                        Unit.valueOf(unit.toUpperCase());
                        return false;
                    } catch (IllegalArgumentException exception) {
                        return true;
                    }
                })
                .toList();

        if(!invalidUnits.isEmpty()) {
            throw new BadRequestException("Invalid unit types: " + String.join(", ", invalidUnits));
        }
    }

    @Transactional
    public RecipeModel createRecipe(@Valid RecipeDTO data) {
        validateUnits(data);

        UserModel user = userService.getLoggedInUser();

        RecipeModel recipe = RecipeModel.builder()
                .name(data.name())
                .user(user)
                .build();

        List<IngredientModel> ingredients = ingredientService.getIngredients(
                data.ingredients().stream()
                        .map(RecipeIngredientDTO::id)
                        .toList()
        );

        List<RecipeIngredientModel> recipeIngredients = ingredients.stream()
                .map(ingredient -> {
                    RecipeIngredientDTO ri = data.ingredients().stream()
                            .filter(element -> ingredient.getId().equals(element.id()))
                            .findFirst()
                            .orElseThrow(() -> new BadRequestException(
                                    "Ingredient data missing for id " + ingredient.getId()
                            ));

                    return RecipeIngredientModel.builder()
                            .recipe(recipe)
                            .ingredient(ingredient)
                            .amount(ri.amount())
                            .unit(Unit.valueOf(ri.unit().toUpperCase()))
                            .build();
                }).toList();

        recipe.setIngredients(recipeIngredients);
        RecipeModel saved = recipeRepository.save(recipe);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public RecipeModel updateRecipe(Long id, @Valid RecipeDTO data) {
        validateUnits(data);

        RecipeModel recipe = getRecipe(id);
        recipe.setName(data.name());
        recipe.getIngredients().clear();

        List<IngredientModel> ingredients = ingredientService.getIngredients(
                data.ingredients().stream()
                        .map(RecipeIngredientDTO::id)
                        .toList()
        );

        ingredients.forEach(ingredient -> {
            RecipeIngredientDTO ri = data.ingredients().stream()
                    .filter(element -> ingredient.getId().equals(element.id()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "Ingredient data missing for id " + ingredient.getId()
                    ));

            recipe.getIngredients().add(RecipeIngredientModel.builder()
                    .recipe(recipe)
                    .ingredient(ingredient)
                    .amount(ri.amount())
                    .unit(Unit.valueOf(ri.unit().toUpperCase()))
                    .build());
        });

        RecipeModel saved = recipeRepository.save(recipe);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        RecipeModel recipe = getRecipe(id);
        recipeRepository.delete(recipe);
        redisCacheService.evictUserCache(CACHE_LIST);
    }
}
