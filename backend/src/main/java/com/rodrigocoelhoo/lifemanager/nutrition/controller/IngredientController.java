package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.finances.dto.PageResponseDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientResponseDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientModel;
import com.rodrigocoelhoo.lifemanager.nutrition.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<PageResponseDTO<IngredientDetailsDTO>> getAllIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(PageResponseDTO.fromPage(ingredientService.getAllIngredients(pageable, name)));
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<IngredientResponseDTO>> getIngredientsById(
            @RequestParam List<Long> ids
    ) {
        List<IngredientModel> ingredients = ingredientService.getIngredients(ids);
        List<IngredientResponseDTO> response = ingredients.stream().map(IngredientResponseDTO::fromEntity).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<IngredientDetailsDTO> createIngredient(
            @RequestBody @Valid IngredientDTO data
    ) {
        IngredientModel ingredient = ingredientService.createIngredient(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(IngredientDetailsDTO.fromEntity(ingredient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDetailsDTO> updateIngredient(
            @PathVariable Long id,
            @RequestBody @Valid IngredientDTO data
    ) {
        IngredientModel ingredient = ingredientService.updateIngredient(id, data);
        return ResponseEntity.ok(IngredientDetailsDTO.fromEntity(ingredient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIngredient(
            @PathVariable Long id
    ) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
