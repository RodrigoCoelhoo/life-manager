package com.rodrigocoelhoo.lifemanager.food.controller;

import com.rodrigocoelhoo.lifemanager.food.dto.RecipeDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.RecipeDetailsDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.RecipeResponseDTO;
import com.rodrigocoelhoo.lifemanager.food.model.RecipeModel;
import com.rodrigocoelhoo.lifemanager.food.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(
            RecipeService recipeService
    ) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeResponseDTO>> getAllRecipes() {
        List<RecipeModel> recipes = recipeService.getAllRecipes();

        List<RecipeResponseDTO> response = recipes.stream()
                .map(RecipeResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetailsDTO> getRecipe(
            @PathVariable Long id
    ) {
        RecipeModel recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(RecipeDetailsDTO.fromEntity(recipe));
    }

    @PostMapping
    public ResponseEntity<RecipeDetailsDTO> createRecipe(
            @RequestBody @Valid RecipeDTO data
    ) {
        RecipeModel recipe = recipeService.createRecipe(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(RecipeDetailsDTO.fromEntity(recipe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDetailsDTO> updateRecipe(
            @PathVariable Long id,
            @RequestBody @Valid RecipeDTO data
    ) {
        RecipeModel recipe = recipeService.updateRecipe(id, data);
        return ResponseEntity.ok(RecipeDetailsDTO.fromEntity(recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id
    ) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
