package com.rodrigocoelhoo.lifemanager.nutrition.repository;

import com.rodrigocoelhoo.lifemanager.nutrition.model.RecipeModel;
import com.rodrigocoelhoo.lifemanager.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<RecipeModel, Long> {
    @EntityGraph(attributePaths = {"ingredients", "ingredients.ingredient", "ingredients.ingredient.brands"})
    Page<RecipeModel> findAllByUser(UserModel user, Pageable pageable);

    @EntityGraph(attributePaths = {"ingredients", "ingredients.ingredient", "ingredients.ingredient.brands"})
    Page<RecipeModel> findByUserAndNameContainingIgnoreCase(UserModel user, String name, Pageable pageable);
    Optional<RecipeModel> findByUserAndId(UserModel user, Long id);

    @Query("""
        SELECT r
        FROM RecipeModel r
        JOIN r.ingredients ri
        WHERE r.user = :user
        GROUP BY r
        HAVING COUNT(ri)
             = COUNT(
                 CASE
                     WHEN ri.ingredient.id IN :ingredientIds
                     THEN 1
                 END
             )
    """)
    Page<RecipeModel> findAvailableRecipes(UserModel user, List<Long> ingredientIds, Pageable pageable);
}
