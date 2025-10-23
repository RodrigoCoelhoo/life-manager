package com.rodrigocoelhoo.lifemanager.food.controller;

import com.rodrigocoelhoo.lifemanager.food.dto.IngredientDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.IngredientDetailsDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.IngredientResponseDTO;
import com.rodrigocoelhoo.lifemanager.food.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.food.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(
            IngredientService ingredientService
    ) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientResponseDTO>> getAllIngredients() {
        List<IngredientModel> ingredients = ingredientService.getAllIngredients();

        List<IngredientResponseDTO> response = ingredients.stream()
                .map(IngredientResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(
            @PathVariable Long id
    ) {
        IngredientModel ingredientModel = ingredientService.getIngredient(id);
        return ResponseEntity.ok().body(IngredientDetailsDTO.fromEntity(ingredientModel)); // IngredientDetailsDTO missing
    }

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> createIngredient(
            @RequestBody @Valid IngredientDTO data
    ) {
        IngredientModel ingredient = ingredientService.createIngredient(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(IngredientResponseDTO.fromEntity(ingredient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateIngredient(
            @PathVariable Long id,
            @RequestBody @Valid IngredientDTO data
    ) {
        IngredientModel ingredient = ingredientService.updateIngredient(id, data);
        return ResponseEntity.ok(IngredientResponseDTO.fromEntity(ingredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIngredient(
            @PathVariable Long id
    ) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
