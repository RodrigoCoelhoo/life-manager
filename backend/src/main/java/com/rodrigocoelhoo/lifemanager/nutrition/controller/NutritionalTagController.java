package com.rodrigocoelhoo.lifemanager.nutrition.controller;

import com.rodrigocoelhoo.lifemanager.nutrition.model.NutritionalTag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients/nutritional-tags")
public class NutritionalTagController {

    @GetMapping
    public ResponseEntity<List<NutritionalTagDTO>> getNutritionalTags() {
        List<NutritionalTagDTO> tags = Arrays.stream(NutritionalTag.values())
                .map(tag -> new NutritionalTagDTO(tag.name(), tag.getUnit()))
                .toList();

        return ResponseEntity.ok(tags);
    }

    public record NutritionalTagDTO(String name, String unit) {}
}
