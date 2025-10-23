package com.rodrigocoelhoo.lifemanager.food.controller;

import com.rodrigocoelhoo.lifemanager.food.dto.MealDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.MealDetailsDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.MealIngredientResponseDTO;
import com.rodrigocoelhoo.lifemanager.food.dto.MealResponseDTO;
import com.rodrigocoelhoo.lifemanager.food.model.MealModel;
import com.rodrigocoelhoo.lifemanager.food.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.food.service.MealService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(
            MealService mealService
    ) {
        this.mealService = mealService;
    }

    @GetMapping
    public ResponseEntity<List<MealResponseDTO>> getAllMeals() {
        List<MealModel> meals = mealService.getAllMeals();

        List<MealResponseDTO> response = meals.stream()
                .map(MealResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDetailsDTO> getMeal(
            @PathVariable Long id
    ) {
        MealModel meal = mealService.getMeal(id);
        LinkedHashMap<NutritionalTag, Double> nutritionalLabel = mealService.getNutritionalLabel(meal);
        return ResponseEntity.ok(MealDetailsDTO.fromEntities(meal, nutritionalLabel));
    }

    @PostMapping
    public ResponseEntity<MealResponseDTO> createMeal(
            @RequestBody @Valid MealDTO data
    ) {
        MealModel meal = mealService.createMeal(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(MealResponseDTO.fromEntity(meal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealResponseDTO> updateMeal(
            @PathVariable Long id,
            @RequestBody @Valid MealDTO data
    ) {
        MealModel meal = mealService.updateMeal(id, data);
        return ResponseEntity.ok(MealResponseDTO.fromEntity(meal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(
            @PathVariable Long id
    ) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
