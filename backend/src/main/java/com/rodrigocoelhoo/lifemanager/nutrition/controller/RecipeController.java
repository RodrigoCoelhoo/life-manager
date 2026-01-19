package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.RecipeDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeModel;
import com.rodrigocoelhoo.lifemanager.nutrition.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PageResponseDTO<RecipeDetailsDTO>> getAllRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(PageResponseDTO.fromPage(recipeService.getAllRecipes(pageable, name)));
    }

    @GetMapping("/available")
    public ResponseEntity<PageResponseDTO<RecipeDetailsDTO>> getAvailableRecipes(
            @RequestParam List<Long> ingredientIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(PageResponseDTO.fromPage(recipeService.getAvailableRecipes(ingredientIds, pageable)));
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
