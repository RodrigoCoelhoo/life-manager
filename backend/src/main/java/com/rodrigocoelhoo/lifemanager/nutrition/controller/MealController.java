package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.MealModel;
import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import com.rodrigocoelhoo.lifemanager.nutrition.service.MealService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PageResponseDTO<MealDetailsDTO>> getAllMeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
        ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        return ResponseEntity.ok(PageResponseDTO.fromPage(mealService.getAllMeals(pageable)));
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
    public ResponseEntity<MealDetailsDTO> createMeal(
            @RequestBody @Valid MealDTO data
    ) {
        MealModel meal = mealService.createMeal(data);
        LinkedHashMap<NutritionalTag, Double> nutritionalLabel = mealService.getNutritionalLabel(meal);
        return ResponseEntity.status(HttpStatus.CREATED).body(MealDetailsDTO.fromEntities(meal, nutritionalLabel));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealDetailsDTO> updateMeal(
            @PathVariable Long id,
            @RequestBody @Valid MealDTO data
    ) {
        MealModel meal = mealService.updateMeal(id, data);
        LinkedHashMap<NutritionalTag, Double> nutritionalLabel = mealService.getNutritionalLabel(meal);
        return ResponseEntity.ok(MealDetailsDTO.fromEntities(meal, nutritionalLabel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(
            @PathVariable Long id
    ) {
        mealService.deleteMeal(id);
        return ResponseEntity.noContent().build();
    }
}
