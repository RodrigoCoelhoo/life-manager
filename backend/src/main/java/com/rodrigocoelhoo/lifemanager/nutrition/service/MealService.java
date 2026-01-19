package com.rodrigocoelhoo.lifemanager.nutrition.service;

import com.rodrigocoelhoo.lifemanager.config.RedisCacheService;
import com.rodrigocoelhoo.lifemanager.exceptions.BadRequestException;
import com.rodrigocoelhoo.lifemanager.exceptions.ResourceNotFound;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealDetailsDTO;
import com.rodrigocoelhoo.lifemanager.nutrition.dto.MealIngredientDTO;

import com.rodrigocoelhoo.lifemanager.nutrition.model.*;
import com.rodrigocoelhoo.lifemanager.nutrition.repository.MealRepository;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import com.rodrigocoelhoo.lifemanager.users.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MealService {

    private final IngredientService ingredientService;
    private final UserService userService;
    private final MealRepository mealRepository;
    private final RedisCacheService redisCacheService;

    private static final String CACHE_LIST = "meals";

    public MealService(
            IngredientService ingredientService,
            UserService userService,
            MealRepository mealRepository,
            RedisCacheService redisCacheService
    ) {
        this.ingredientService = ingredientService;
        this.userService = userService;
        this.mealRepository = mealRepository;
        this.redisCacheService = redisCacheService;
    }


    @Cacheable(value = CACHE_LIST, keyGenerator = "userAwareKeyGenerator")
    public Page<MealDetailsDTO> getAllMeals(Pageable pageable) {
        UserModel user = userService.getLoggedInUser();
        return mealRepository.findAllByUser(user, pageable).map(
                meal -> MealDetailsDTO.fromEntities(meal, getNutritionalLabel(meal))
        );
    }

    public MealModel getMeal(Long id) {
        UserModel user = userService.getLoggedInUser();
        return mealRepository.findByUserAndId(user, id)
                .orElseThrow(() -> new ResourceNotFound("Meal with ID '" + id + "' doesn't belong to the current user"));
    }

    public List<MealModel> getMealsByRange(
            LocalDateTime start,
            LocalDateTime end
    ) {
        UserModel user = userService.getLoggedInUser();
        return mealRepository.findAllByUserAndDateBetweenOrderByDateDescIdDesc(user, start, end);
    }

    private Map<Long, IngredientBrandModel> mapIngredientsBrand(
            List<IngredientModel> ingredients,
            Map<Long, MealIngredientDTO> ingredientMap
    ) {
        Map<Long, IngredientBrandModel> result = new HashMap<>();

        ingredients.forEach(ingredient -> {
            Long brandId = ingredientMap.get(ingredient.getId()).brandId();

            IngredientBrandModel brand = ingredient.getBrands().stream()
                    .filter(b -> b.getId().equals(brandId))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "Brand with ID '" + brandId + "' doesn't belong to the ingredient with ID '"+ ingredient.getId() + "'"
                    ));

            result.put(ingredient.getId(), brand);
        });

        return result;
    }

    private void populateMealIngredients(MealModel meal, MealDTO data) {
        List<Long> ingredientIds = data.ingredients().stream()
                .map(MealIngredientDTO::ingredientId)
                .toList();

        List<IngredientModel> ingredientsFromDb = ingredientService.getIngredients(ingredientIds);

        Map<Long, MealIngredientDTO> ingredientMap = new HashMap<>();

        ingredientsFromDb.forEach(ingredient -> {
            MealIngredientDTO dto = data.ingredients().stream()
                    .filter(i -> i.ingredientId().equals(ingredient.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(
                            "No matching ingredient DTO found for id " + ingredient.getId()
                    ));
            ingredientMap.put(ingredient.getId(), dto);
        });

        Map<Long, IngredientBrandModel> ingredientBrandMap = mapIngredientsBrand(ingredientsFromDb, ingredientMap);

        ingredientsFromDb.forEach(ingredient -> {
            meal.getIngredients().add(
                    MealIngredientModel.builder()
                            .meal(meal)
                            .ingredient(ingredient)
                            .brand(ingredientBrandMap.get(ingredient.getId()))
                            .amount(ingredientMap.get(ingredient.getId()).amount())
                            .unit(Unit.valueOf(ingredientMap.get(ingredient.getId()).unit().toUpperCase()))
                            .build()
            );
        });
    }

    private void validateUnits(MealDTO data) {
        List<String> invalidUnits = data.ingredients().stream()
                .map(MealIngredientDTO::unit)
                .filter(unit -> {
                    try {
                        Unit.valueOf(unit.toUpperCase());
                        return false;
                    } catch (IllegalArgumentException exception) {
                        return true;
                    }
                })
                .toList();

        if(!invalidUnits.isEmpty()) {
            throw new BadRequestException("Invalid unit types: " + String.join(", ", invalidUnits));
        }
    }

    @Transactional
    public MealModel createMeal(@Valid MealDTO data) {

        validateUnits(data);

        UserModel user = userService.getLoggedInUser();
        MealModel meal = MealModel.builder()
                .user(user)
                .date(data.date())
                .ingredients(new HashSet<>())
                .build();

        populateMealIngredients(meal, data);

        MealModel saved = mealRepository.save(meal);

        LocalDate date = meal.getDate().toLocalDate().with(DayOfWeek.MONDAY);

        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("nutritionDashboard", "week:" + date);

        return saved;
    }

    @Transactional
    public MealModel updateMeal(Long id, MealDTO data) {
        validateUnits(data);

        MealModel meal = getMeal(id);
        meal.setDate(data.date());
        meal.getIngredients().clear();
        populateMealIngredients(meal, data);

        MealModel saved = mealRepository.save(meal);

        LocalDate date = meal.getDate().toLocalDate().with(DayOfWeek.MONDAY);
        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("nutritionDashboard", "week:" + date);

        return saved;
    }

    @Transactional
    public void deleteMeal(Long id) {
        MealModel meal = getMeal(id);
        mealRepository.delete(meal);

        LocalDate date = meal.getDate().toLocalDate().with(DayOfWeek.MONDAY);
        redisCacheService.evictUserCache(CACHE_LIST);
        redisCacheService.evictUserCacheSpecific("nutritionDashboard", "week:" + date);
    }

    public LinkedHashMap<NutritionalTag, Double> getNutritionalLabel(MealModel meal) {
        Map<NutritionalTag, Double> temp = new HashMap<>();

        for (MealIngredientModel ingredient : meal.getIngredients()) {
            Set<NutritionalValueModel> nutritionalValues = ingredient.getBrand().getNutritionalValues();

            addNutrients(temp, nutritionalValues, ingredient.getAmount(), ingredient.getUnit());
        }

        LinkedHashMap<NutritionalTag, Double> orderedResult = new LinkedHashMap<>();
        for (NutritionalTag tag : NutritionalTag.values()) {
            if (temp.containsKey(tag)) {
                orderedResult.put(tag, temp.get(tag));
            }
        }

        return orderedResult;
    }

    private void addNutrients(Map<NutritionalTag, Double> result, Set<NutritionalValueModel> nutritionalValues, Double amount, Unit unit) {
        double ingredientAmountInGramsOrMl = convertToBaseUnit(amount, unit);

        for (NutritionalValueModel nutrient : nutritionalValues) {
            NutritionalTag tag = nutrient.getTag();
            double existing = result.getOrDefault(tag, 0.0);
            result.put(tag, existing + (nutrient.getPer100units() * ingredientAmountInGramsOrMl) / 100);
        }
    }

    private double convertToBaseUnit(Double amount, Unit unit) {
        return switch (unit) {
            case MG -> amount / 1000;
            case G, ML -> amount;
            case KG, L -> amount * 1000;
        };
    }
}
