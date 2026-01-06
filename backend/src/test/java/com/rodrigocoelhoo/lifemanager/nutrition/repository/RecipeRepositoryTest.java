package com.rodrigocoelhoo.lifemanager.nutrition.repository;

import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeIngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.Unit;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
class RecipeRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    RecipeRepository recipeRepository;

    @Test
    @DisplayName("All ingredients match all recipes → returns all recipes")
    void findAvailableRecipes_allIngredientsMatch_returnsAllRecipes() {
        UserModel user = createUser(
                "RodrigoCoelho",
                "Rodrigo",
                "Coelho",
                "rscoelho.dev@gmail.com",
                "Password-123"
        );

        IngredientModel egg = createIngredient(user, "Egg");
        IngredientModel cheese = createIngredient(user, "Cheese");
        IngredientModel bacon = createIngredient(user, "Bacon");

        RecipeModel omelette = createRecipe(user, "Omelette");
        RecipeModel omeletteAndBacon = createRecipe(user, "Omelette And Bacon");
        entityManager.flush();

        createRecipeIngredient(omelette, egg);
        createRecipeIngredient(omelette, cheese);
        createRecipeIngredient(omeletteAndBacon, egg);
        createRecipeIngredient(omeletteAndBacon, cheese);
        createRecipeIngredient(omeletteAndBacon, bacon);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        List<RecipeModel> result = recipeRepository.findAvailableRecipes(user, List.of(egg.getId(), cheese.getId(), bacon.getId()), pageable).toList();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(omelette, omeletteAndBacon);
    }

    @Test
    @DisplayName("Some ingredients match → returns only matching recipes")
    void findAvailableRecipes_someIngredientsMatch_returnsPartialRecipes() {
        UserModel user = createUser(
                "RodrigoCoelho",
                "Rodrigo",
                "Coelho",
                "rscoelho.dev@gmail.com",
                "Password-123"
        );

        IngredientModel egg = createIngredient(user, "Egg");
        IngredientModel cheese = createIngredient(user, "Cheese");
        IngredientModel bacon = createIngredient(user, "Bacon");
        IngredientModel tomato = createIngredient(user, "Tomato");

        RecipeModel omelette = createRecipe(user, "Omelette");
        RecipeModel omeletteAndBacon = createRecipe(user, "Omelette And Bacon");
        entityManager.flush();

        createRecipeIngredient(omelette, egg);
        createRecipeIngredient(omelette, cheese);
        createRecipeIngredient(omeletteAndBacon, egg);
        createRecipeIngredient(omeletteAndBacon, cheese);
        createRecipeIngredient(omeletteAndBacon, bacon);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        List<RecipeModel> result = recipeRepository.findAvailableRecipes(user, List.of(egg.getId(), cheese.getId(), tomato.getId()), pageable).toList();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyInAnyOrder(omelette);
    }

    @Test
    @DisplayName("Partial ingredients, no full match → returns empty list")
    void findAvailableRecipes_partialIngredients_noMatch_returnsEmpty() {
        UserModel user = createUser(
                "RodrigoCoelho",
                "Rodrigo",
                "Coelho",
                "rscoelho.dev@gmail.com",
                "Password-123"
        );

        IngredientModel egg = createIngredient(user, "Egg");
        IngredientModel cheese = createIngredient(user, "Cheese");
        IngredientModel bacon = createIngredient(user, "Bacon");

        RecipeModel omelette = createRecipe(user, "Omelette");
        RecipeModel omeletteAndBacon = createRecipe(user, "Omelette And Bacon");
        entityManager.flush();

        createRecipeIngredient(omelette, egg);
        createRecipeIngredient(omelette, cheese);
        createRecipeIngredient(omeletteAndBacon, egg);
        createRecipeIngredient(omeletteAndBacon, cheese);
        createRecipeIngredient(omeletteAndBacon, bacon);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        List<RecipeModel> result = recipeRepository.findAvailableRecipes(user, List.of(egg.getId()), pageable).toList();
        assertThat(result).hasSize(0);
        assertThat(result).containsExactlyInAnyOrder();
    }

    @Test
    @DisplayName("Empty ingredient list → returns empty list")
    void findAvailableRecipes_emptyIngredientList_returnsEmpty() {
        UserModel user = createUser(
                "RodrigoCoelho",
                "Rodrigo",
                "Coelho",
                "rscoelho.dev@gmail.com",
                "Password-123"
        );

        IngredientModel egg = createIngredient(user, "Egg");
        IngredientModel cheese = createIngredient(user, "Cheese");
        IngredientModel bacon = createIngredient(user, "Bacon");

        RecipeModel omelette = createRecipe(user, "Omelette");
        RecipeModel omeletteAndBacon = createRecipe(user, "Omelette And Bacon");
        entityManager.flush();

        createRecipeIngredient(omelette, egg);
        createRecipeIngredient(omelette, cheese);
        createRecipeIngredient(omeletteAndBacon, egg);
        createRecipeIngredient(omeletteAndBacon, cheese);
        createRecipeIngredient(omeletteAndBacon, bacon);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        List<RecipeModel> result = recipeRepository.findAvailableRecipes(user, List.of(), pageable).toList();
        assertThat(result).hasSize(0);
        assertThat(result).containsExactlyInAnyOrder();
    }

    @Test
    @DisplayName("Query for wrong user → returns empty list")
    void findAvailableRecipes_wrongUser_returnsEmpty() {
        UserModel user = createUser(
                "RodrigoCoelho",
                "Rodrigo",
                "Coelho",
                "rscoelho.dev@gmail.com",
                "Password-123"
        );

        UserModel wrongUser = createUser(
                "RodrigoC",
                "Rodrigo",
                "C",
                "rscoelho@gmail.com",
                "Password-123"
        );

        IngredientModel egg = createIngredient(user, "Egg");
        IngredientModel cheese = createIngredient(user, "Cheese");
        IngredientModel bacon = createIngredient(user, "Bacon");

        RecipeModel omelette = createRecipe(user, "Omelette");
        RecipeModel omeletteAndBacon = createRecipe(user, "Omelette And Bacon");
        entityManager.flush();

        createRecipeIngredient(omelette, egg);
        createRecipeIngredient(omelette, cheese);
        createRecipeIngredient(omeletteAndBacon, egg);
        createRecipeIngredient(omeletteAndBacon, cheese);
        createRecipeIngredient(omeletteAndBacon, bacon);

        Pageable pageable = PageRequest.of(0, 5, Sort.by("id").descending());

        List<RecipeModel> result = recipeRepository.findAvailableRecipes(wrongUser, List.of(egg.getId(), cheese.getId(), bacon.getId()), pageable).toList();
        assertThat(result).hasSize(0);
        assertThat(result).containsExactlyInAnyOrder();
    }

    private UserModel createUser(
            String username,
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        UserModel newUser = new UserModel(
                username,
                firstName,
                lastName,
                email,
                password
        );

        this.entityManager.persist(newUser);
        return newUser;
    }

    private IngredientModel createIngredient(
            UserModel user,
            String name
    ) {
        IngredientModel ingredient = IngredientModel.builder()
                .name(name)
                .user(user)
                .build();

        this.entityManager.persist(ingredient);
        return ingredient;
    }

    private RecipeModel createRecipe(
            UserModel user,
            String name
    ) {
        RecipeModel recipe = RecipeModel.builder()
                .user(user)
                .name(name)
                .ingredients(new ArrayList<>())
                .build();
        this.entityManager.persist(recipe);
        return recipe;
    }

    private void createRecipeIngredient(
            RecipeModel recipe,
            IngredientModel ingredient
    ) {
        RecipeIngredientModel recipeIngredient = RecipeIngredientModel.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .amount(0.0)
                .unit(Unit.G)
                .build();

        recipe.getIngredients().add(recipeIngredient);
    }
}