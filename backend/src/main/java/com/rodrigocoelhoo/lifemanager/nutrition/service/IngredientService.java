package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.IngredientRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final UserService userService;

    public IngredientService(
            IngredientRepository ingredientRepository,
            UserService userService
    ) {
        this.ingredientRepository = ingredientRepository;
        this.userService = userService;
    }

    public List<IngredientModel> getAllIngredients() {
        UserModel user = userService.getLoggedInUser();
        return ingredientRepository.findAllByUser(user);
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
                .brands(new ArrayList<>())
                .build();

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public IngredientModel updateIngredient(Long id, @Valid IngredientDTO data) {
        IngredientModel ingredient = getIngredient(id);
        ingredient.setName(data.name());
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public void deleteIngredient(Long id) {
        IngredientModel ingredient = getIngredient(id);
        ingredientRepository.delete(ingredient);
    }

    public List<IngredientModel> getIngredients(List<Long> ids) {
        UserModel user = userService.getLoggedInUser();

        List<IngredientModel> ingredients = ingredientRepository.findAllByUserAndIdIn(user, ids);

        if(ingredients.size() != ids.size())
            throw new ResourceNotFound("Some ingredients do not belong to the current user");

        return ingredients;
    }
}
