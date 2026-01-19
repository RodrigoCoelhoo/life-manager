package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.IngredientRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final UserService userService;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "ingredients";

    public IngredientService(
            IngredientRepository ingredientRepository,
            UserService userService,
            RedisCacheService redisCacheService
    ) {
        this.ingredientRepository = ingredientRepository;
        this.userService = userService;
        this.redisCacheService = redisCacheService;
    }

    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<IngredientDetailsDTO> getAllIngredients(
            Pageable pageable,
            String name
    ) {
        UserModel user = userService.getLoggedInUser();
        Page<IngredientModel> page;
        if(name == null || name.isBlank())
            page = ingredientRepository.findAllByUser(user, pageable);
        else
            page = ingredientRepository.findByUserAndNameContainingIgnoreCase(user, name, pageable);

        return page.map(IngredientDetailsDTO::fromEntity);
    }

    public IngredientModel getIngredient(Long id) {
        UserModel user = userService.getLoggedInUser();
        return ingredientRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound("Ingredient with ID '" + id + "' does not belong to the current user"));
    }

    @Transactional
    public IngredientModel createIngredient(IngredientDTO data) {
        UserModel user = userService.getLoggedInUser();

        IngredientModel ingredient = IngredientModel.builder()
                .name(data.name())
                .user(user)
                .brands(new HashSet<>())
                .build();

        IngredientModel saved =  ingredientRepository.save(ingredient);

        redisCacheService.evictUserCache(CACHE_LIST);

        return saved;
    }

    @Transactional
    public IngredientModel updateIngredient(Long id, @Valid IngredientDTO data) {
        IngredientModel ingredient = getIngredient(id);
        ingredient.setName(data.name());
        IngredientModel saved =  ingredientRepository.save(ingredient);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCache("recipes");

        return saved;
    }

    @Transactional
    public void deleteIngredient(Long id) {
        IngredientModel ingredient = getIngredient(id);
        ingredientRepository.delete(ingredient);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCache("recipes");
        redisCacheService.evictUserCache("meals");
        redisCacheService.evictUserCache("nutritionDashboard");
    }

    public List<IngredientModel> getIngredients(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        UserModel user = userService.getLoggedInUser();
        List<IngredientModel> ingredients = ingredientRepository.findAllByUserAndIdIn(user, ids);

        if(ingredients.size() != ids.size())
            throw new ResourceNotFound("Some ingredients do not belong to the current user");

        return ingredients;
    }
}
