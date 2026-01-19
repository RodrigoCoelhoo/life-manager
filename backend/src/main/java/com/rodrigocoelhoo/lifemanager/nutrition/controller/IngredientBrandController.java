package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientBrandDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.IngredientBrandDetailsResponseDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.model.IngredientBrandModel;
import com.rodrigocoelhoo.lifemanager.nutrition.service.IngredientBrandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/ingredients/{ingredientId}/brands")
public class IngredientBrandController {

    private final IngredientBrandService ingredientBrandService;

    public IngredientBrandController(
            IngredientBrandService ingredientBrandService
    ) {
        this.ingredientBrandService = ingredientBrandService;
    }

    @GetMapping
    public ResponseEntity<List<IngredientBrandDetailsResponseDTO>> getAllIngredientBrands(
            @PathVariable Long ingredientId
    ) {
        Set<IngredientBrandModel> brands = ingredientBrandService.getAllIngredientBrands(ingredientId);

        List<IngredientBrandDetailsResponseDTO> response = brands.stream()
                .map(IngredientBrandDetailsResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{brandId}")
    public ResponseEntity<IngredientBrandDetailsResponseDTO> getIngredientBrand(
            @PathVariable Long ingredientId,
            @PathVariable Long brandId
    ) {
        IngredientBrandModel brand = ingredientBrandService.getIngredientBrand(ingredientId, brandId);
        return ResponseEntity.ok(IngredientBrandDetailsResponseDTO.fromEntity(brand));
    }

    @PostMapping
    public ResponseEntity<IngredientBrandDetailsResponseDTO> createIngredientBrand(
            @PathVariable Long ingredientId,
            @RequestBody @Valid IngredientBrandDTO data
    ) {
        IngredientBrandModel brand = ingredientBrandService.createIngredientBrand(ingredientId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(IngredientBrandDetailsResponseDTO.fromEntity(brand));
    }

    @PutMapping("/{brandId}")
    public ResponseEntity<IngredientBrandDetailsResponseDTO> updateIngredientBrand(
            @PathVariable Long ingredientId,
            @PathVariable Long brandId,
            @RequestBody @Valid IngredientBrandDTO data
    ) {
        IngredientBrandModel brand = ingredientBrandService.updateIngredientBrand(ingredientId, brandId, data);
        return ResponseEntity.ok(IngredientBrandDetailsResponseDTO.fromEntity(brand));
    }

    @DeleteMapping("/{brandId}")
    public ResponseEntity<?> deleteIngredientBrand(
            @PathVariable Long ingredientId,
            @PathVariable Long brandId
    ) {
        ingredientBrandService.deleteIngredientBrand(ingredientId, brandId);
        return ResponseEntity.noContent().build();
    }
}
